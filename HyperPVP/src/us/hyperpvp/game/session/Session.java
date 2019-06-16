/*******************************************************************************
 * Copyright 2014 Alex Miller
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package us.hyperpvp.game.session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction;
import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.GameSpawns;
import us.hyperpvp.game.GameType;
import us.hyperpvp.game.map.team.TeamMap;
import us.hyperpvp.misc.CycleUtil;
import us.hyperpvp.misc.OverideValue;

public class Session extends OverideValue {

	private boolean isPlaying = false;
	private int kills;
	private int spawnIndex = 0;
	private double killTimer = 0;
	private boolean brokeMonument;
	private boolean lastDamagedByTimer = false;
	private boolean deathHandle = false;
	private boolean isDead = false;
	private Player player;
	private TeamMap team;
	private Player lastDamagedBy;
	private String lastDamageTool;
	private List<Block> monumentBlockCount;
	private Map<Integer, GameSpawns> spawns;

	private Integer userId;

	public Session(Player player) {

		try {
			this.userId = HyperPVP.getStorage().readInt32("SELECT id FROM users WHERE uuid = '" + player.getUniqueId() + "'");
		} catch (SQLException e) {
			this.userId = 0;
		}

		this.player = player;
		this.monumentBlockCount = new ArrayList<Block>();
		this.spawns = new HashMap<Integer, GameSpawns>();
		this.reset();
	}

	public void reset() {
		this.setDestroyer(false);
		this.resetKills();
		this.resetMonumentBlock();
		this.userId = 0;
		this.lastDamagedBy = null;
		this.monumentBlockCount.clear();
		this.spawns.clear();
		this.spawnIndex = 0;
		this.setLastDamagedBy(null);
		this.setLastDamageTool(null);
		this.lastDamagedByTimer = false;
	}

	public void updateStatistics(ScoreType type, Session from) {

		if (this.userId == 0) {
			return;
		}

		try {
			java.sql.PreparedStatement statement = HyperPVP.getStorage().queryParams("INSERT INTO `users_statistics` (`from_id`, `to_id`, `type`, `time`, `map`, `mode`) VALUES (?, ?, ?, ?, ?, ?)"); {
				statement.setInt(1, this.getUserId());
				statement.setInt(2, from.getUserId());
				statement.setString(3, type.toString());
				statement.setLong(4, (System.currentTimeMillis() / 1000L));
				statement.setString(5, HyperPVP.getMap().getMapName());
				statement.setString(6, HyperPVP.getMap().getType().getType());
				statement.execute();
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateStats(ScoreType type) {

		if (this.userId == 0) {
			return;
		}

		try {
			java.sql.PreparedStatement statement = HyperPVP.getStorage().queryParams("INSERT INTO `users_statistics` (`from_id`, `to_id`, `type`, `time`, `map`, `mode`) VALUES (?, ?, ?, ?, ?, ?)"); {
				statement.setInt(1, this.getUserId());
				statement.setInt(2, 0);
				statement.setString(3, type.toString());
				statement.setLong(4, (System.currentTimeMillis() / 1000L));
				statement.setString(5, HyperPVP.getMap().getMapName());
				statement.setString(6, HyperPVP.getMap().getType().getType());
				statement.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getUserId() {
		return this.userId;
	}

	public String getItemHand(Player killed) {

		if (player.getInventory().getItemInMainHand().getType() == Material.WOOD_HOE && 
				player.getInventory().getItemInMainHand().hasItemMeta() && 
				player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Paintball Gun")) {
			
			return "paintball gun";
		} else if (player.getInventory().getItemInMainHand().getType() == null) {
			return "fist";
		} else if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
			return "fist";
		} else {
			return player.getInventory().getItemInMainHand().getType().name().replace("_", " ").toLowerCase();
		}
	}

	public String getItemHand() {

		if (player.getInventory().getItemInMainHand().getType() == null) {
			return "fist";
		} else if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
			return "fist";
		} else {
			return player.getInventory().getItemInMainHand().getType().name().replace("_", " ").toLowerCase();
		}
	}

	public void updateScoreboard(boolean update) {

		Scoreboard scoreboard = player.getScoreboard();

		Objective objective = scoreboard.getObjective("Leaderboard") != null ? scoreboard.getObjective("Leaderboard") : scoreboard.registerNewObjective("Leaderboard", "");

		if (HyperPVP.isCycling()) {
			objective.unregister();
			return;
		}

		GameType type = HyperPVP.getMap().getType();

		if (update) {
			if (type == GameType.FFA) {

				List<Session> topPlayers = HyperPVP.getMap().getTop();

				objective.unregister();
				objective = scoreboard.getObjective("Leaderboard") != null ? scoreboard.getObjective("Leaderboard") : scoreboard.registerNewObjective("Leaderboard", "");
				objective.setDisplaySlot(DisplaySlot.SIDEBAR);
				objective.setDisplayName("Leaderboard");

				int i = 1;

				for (Session set : topPlayers) {

					if (i > 10 && set.getKills() != 0) {
						continue;
					}

					Score score = objective.getScore(HyperPVP.getMap().getTeams().get(0).getColor().toString() + set.getPlayer().getName());
					score.setScore(set.getKills());
				}

			} else {

				objective.unregister();
				objective = scoreboard.getObjective("Leaderboard") != null ? scoreboard.getObjective("Leaderboard") : scoreboard.registerNewObjective("Leaderboard", "");
				objective.setDisplaySlot(DisplaySlot.SIDEBAR);;

				if (HyperPVP.getMap().getType() == GameType.CONQUEST) {

					objective.setDisplayName("Tickets Remaining");

					TeamMap first = HyperPVP.getMap().getTeamTicketsWinning();
					TeamMap second = HyperPVP.getMap().getTeamTicketsLosing();

					if (first == null) {
						first = HyperPVP.getMap().getTeams().get(0);
					}

					if (second == null) {
						second = HyperPVP.getMap().getTeams().get(1);
					}

					objective.getScore(first.getColor().toString() + "" + HyperPVP.capitalize(first.getColor().name().toLowerCase().replace("_", " ").replace("dark ", "")) + " Team").setScore(first.getTickets());
					objective.getScore(second.getColor().toString() + "" + HyperPVP.capitalize(second.getColor().name().toLowerCase().replace("_", " ").replace("dark ", "")) + " Team").setScore(second.getTickets());

				} else {

					objective.setDisplayName("Leaderboard");

					TeamMap first = HyperPVP.getMap().getTeamWinning();
					TeamMap second = HyperPVP.getMap().getTeamLosing();

					if (first == null) {
						first = HyperPVP.getMap().getTeams().get(0);
					}

					if (second == null) {
						second = HyperPVP.getMap().getTeams().get(1);
					}

					if (HyperPVP.getSession(player).isPlaying()) {
						objective.getScore("My Kills").setScore(HyperPVP.getSession(player).getKills());
					}

					objective.getScore(first.getColor().toString() + "" + HyperPVP.capitalize(first.getColor().name().toLowerCase().replace("_", " ").replace("dark ", "")) + " Team").setScore(first.getKills());
					objective.getScore(second.getColor().toString() + "" + HyperPVP.capitalize(second.getColor().name().toLowerCase().replace("_", " ").replace("dark ", "")) + " Team").setScore(second.getKills());
				}
			}


		} else {
			objective.unregister();
		}


	}

	public String getDeathMessage(Player killed, EntityDamageEvent e, boolean pastTense) {

		if (player == null) {
			player = killed;
		}

		DamageCause lastCause = e.getCause();

		if (lastCause == DamageCause.DROWNING) {
			return " drowned";	
		}

		if (lastCause == DamageCause.ENTITY_ATTACK) {
			return pastTense ? " were ambushed by mobs" : " was ambushed by mobs";
		}

		if (lastCause == DamageCause.ENTITY_EXPLOSION) {
			return " 'sploded";
		}

		if (lastCause == DamageCause.FALL) {
			return " hit the ground too hard (" + (int)player.getFallDistance() + " blocks)";
		}

		if (lastCause == DamageCause.FALLING_BLOCK) {
			return " suffocated";
		}

		if (lastCause == DamageCause.FIRE) {
			return " burnt alive";
		}

		if (lastCause == DamageCause.LAVA) {
			return " burnt to a crisp";
		}

		if (lastCause == DamageCause.MAGIC) {
			return " died from magic";
		}

		if (lastCause == DamageCause.MELTING) {
			return " melted";
		}

		if (lastCause == DamageCause.POISON) {
			return " failed the battle of wits";
		}

		if (lastCause == DamageCause.STARVATION) {
			return " died from poverty";
		}

		if (lastCause == DamageCause.SUFFOCATION) {
			return " suffocated";
		}

		if (lastCause == DamageCause.SUICIDE) {
			return " killed himself";
		}

		if (lastCause == DamageCause.THORNS) {
			return " didn't see the cactus";
		}

		if (lastCause == DamageCause.VOID) {
			return " fell out of the world";
		}

		if (lastCause == DamageCause.BLOCK_EXPLOSION) {

			String death = " exploded";
			return death;
		}

		if (lastCause == DamageCause.WITHER) {
			return pastTense ? " were killed by a wither" : " was killed by a wither";
		} 


		return pastTense ? " died" : " has died";

	}

	public void handleDeath(EntityDamageEvent e) {

		if (this.isHandlingDeath()) {
			return;
		}

		this.setDeathHandle(true);

		ItemStack[] inventory = this.player.getInventory().getContents();
		List<ItemStack> drops = new ArrayList<ItemStack>();

		if (HyperPVP.getMap().getType() == GameType.FFA || HyperPVP.getMap().getType() == GameType.TDM) {

			for (ItemStack drop : inventory)  {

				if (drop == null) {
					continue;
				}

				if (HyperPVP.getMap().getFeatures().contains("oneshotarrow")) {
					if (drop.getType() == Material.ARROW) {
						continue;
					}
				}

				if (drop.getType() == Material.GOLDEN_APPLE) {
					drops.add(drop);
				}
			}


			/*<?java if ($death = true) { print 'Dead'; } else { print 'Alive'; } ?>/*/

		} else {

			for (ItemStack drop : inventory) {

				if (drop == null) {
					continue;
				}

				if (drop.getType() == Material.LEATHER_BOOTS || drop.getType() == Material.LEATHER_HELMET || drop.getType() == Material.LEATHER_CHESTPLATE || drop.getType() == Material.LEATHER_LEGGINGS) {
					continue;
				} else {

					if (HyperPVP.getMap().getFeatures().contains("oneshotarrow")) {
						if (drop.getType() == Material.ARROW) {
							continue;
						}
					}


					drops.add(drop);
				}

			}
		}

		for (ItemStack stack : drops) {
			this.player.getLocation().getWorld().dropItem(this.player.getLocation(), stack);
		}

		drops.clear();
		this.player.getInventory().clear();

		Player killer = null;

		if (this.getLastDamagedBy() != null) {
			killer = this.getLastDamagedBy();
		} else {
			if (e.getCause() != null) {
				if (e.getCause() == DamageCause.VOID) {
					this.killDecrease();
				}
			}
		}

		if (killer != null) {
			if (this.player == killer) {
				Bukkit.broadcastMessage(this.getTeam().getColor() + this.player.getName() + ChatColor.GRAY + " killed himself!");
			} else {

				if (HyperPVP.getMap().getFeatures().contains("oneshotarrow")) {
					killer.getInventory().addItem(new ItemStack(Material.ARROW, 1));
				}

				Session killerSession = HyperPVP.getSession(killer);

				if (HyperPVP.getMap().getType() != GameType.FFA) {
					killerSession.getTeam().killIncrease();
					killerSession.getOtherTeam().decreaseTickets();
					killerSession.killIncrease();



				} else {
					killerSession.killIncrease();
				}

				try {

					int death = HyperPVP.getStorage().readInt32("SELECT deaths FROM users WHERE uuid = '" + this.player.getUniqueId() + "'");
					HyperPVP.getStorage().executeQuery("UPDATE users SET deaths = '" + (death + 1) + "' WHERE uuid = '" + this.player.getUniqueId() + "'");

					int kills = HyperPVP.getStorage().readInt32("SELECT kills FROM users WHERE uuid = '" + killer.getUniqueId() + "'");
					HyperPVP.getStorage().executeQuery("UPDATE users SET kills = '" + (kills + 1) + "' WHERE uuid = '" + killer.getUniqueId() + "'");

				} catch (SQLException ex) {
					ex.printStackTrace();
				}

				this.updateStatistics(ScoreType.DEATH, killerSession);
				killerSession.updateStatistics(ScoreType.KILL, this);

				String itemKilledBy = killerSession.getItemHand(this.player);

				if (this.getLastDamagedBy() != null) {
					itemKilledBy = this.getLastDamageTool();
				}

				Bukkit.broadcastMessage(this.getTeam().getColor() + this.player.getName() + ChatColor.GRAY + " was slain by " + killerSession.getTeam().getColor() + killer.getName() + ChatColor.GRAY + "'s " + itemKilledBy);
			}
		} else {
			this.getTeam().decreaseTickets();
			Bukkit.broadcastMessage(this.getTeam().getColor() + this.player.getName() + ChatColor.GRAY + this.getDeathMessage(this.player, e, false));
		}

		Session killedSession = this;
		
		if (e.getCause() != DamageCause.VOID) {
		
			this.isDead = true;
			CycleUtil.visibilityStatus(false, this.player); // Hide player
			this.player.setGameMode(GameMode.SPECTATOR);
			this.player.playSound(this.player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
			this.player.teleport(this.player.getLocation().add(0, 5, 0));
			
			
			new BukkitRunnable () {
				
				private int ticks = 10;
				
				@Override
				public void run() {
					
					killedSession.clearTitles();
					
					if (HyperPVP.isCycling()) {
						this.cancel();
						return;
					}
					
					if (ticks == 0) {
						this.cancel();					
						respawnPlayer();
						return;
					}
					

					killedSession.sendTitle(ChatColor.RED + "You died", ChatColor.GRAY + "Respawning in " + ticks + " seconds", 20);
					
					ticks--;
					
				}
			}.runTaskTimer(HyperPVP.getJavaPlugin(), 0, 20);
			
		} else {
			respawnPlayer();
		}
	}

	protected void clearTitles() {
		this.sendTitle("", "", 20);
	}
	
	protected void sendTitle(String title, String subTitle, int duration) {
		
		PacketPlayOutTitle packet = null;
		
		packet = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + title + "\"}"), 0, duration, 0);
		((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);

		packet = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"" + subTitle + "\"}"), 0, duration, 0);
		((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
		
	}

	private void respawnPlayer() {
		
		if (this.isDead) {
			this.isDead = false;
			CycleUtil.refreshShowHidePlayer();
		}
		
		Location respawnLocation = null;

		if (HyperPVP.isCycling()) {
			respawnLocation = HyperPVP.getPreviousWorld().getRandomSpawn(player);
		} else {
			respawnLocation = this.getGameSpawn();
		}

		player.setFallDistance(0);
		player.teleport(respawnLocation);
		player.setHealth(20F);
		player.setFoodLevel(20);
		player.setGameMode(GameMode.SURVIVAL);


		CycleUtil.resetInventory(player, false);

		this.setLastDamagedBy(null);	
		this.resetKillTimer();
		this.setDeathHandle(false);
		
		HyperPVP.updateScoreboards();
	}

	@SuppressWarnings("deprecation")
	public void leaveGame(boolean normalLeave, boolean leaveAfterCycling) {

		this.reset();

		if (normalLeave) {
			player.sendMessage(ChatColor.AQUA + "You are now spectating!");
			HyperPVP.setListName(ChatColor.AQUA, player);
			player.getInventory().clear();
			player.updateInventory();
		}

		Session.addSpectator(this.player, normalLeave);
		CycleUtil.refreshShowHidePlayer();

		if (!leaveAfterCycling) {
			HyperPVP.updateScoreboards();
		}

		List<TeamMap> teams = HyperPVP.getGame().getMapManager().getGameMap().getTeams();

		try {
			HyperPVP.getStorage().executeQuery("UPDATE servers SET team_one = '" + HyperPVP.getGame().getMapManager().getGameMap().getTeamMembers(teams.get(0).getColor()).size() + "' WHERE bungee_name = '" + HyperPVP.getConfiguration().getConfig().getString("Server").toLowerCase() + "'");

			if (HyperPVP.getGame().getMapManager().getGameMap().getType() != GameType.FFA) {
				HyperPVP.getStorage().executeQuery("UPDATE servers SET team_two = '" + HyperPVP.getGame().getMapManager().getGameMap().getTeamMembers(teams.get(1).getColor()).size() + "' WHERE bungee_name = '" + HyperPVP.getConfiguration().getConfig().getString("Server").toLowerCase() + "'");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	@SuppressWarnings("deprecation")
	public static Session addSpectator(Player player, boolean normalLeave) {

		Session session = null;

		if (!HyperPVP.getSessions().containsKey(player.getName())) {
			session = new Session(player);
			HyperPVP.getSessions().put(player.getName(), session);
		}

		HyperPVP.getSession(player).setPlaying(false);

		player.setGameMode(GameMode.CREATIVE);
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setSaturation(0);
		player.setExhaustion(0);

		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}

		player.getInventory().setHelmet(null);
		player.getInventory().setBoots(null);	
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setItemInOffHand(null);
		player.getInventory().clear();
		player.updateInventory();

		if (normalLeave) {
			CycleUtil.resetSpectatorInventory(player);
		}

		return session;
	}


	public Location getGameSpawn() {

		if (this.spawns.containsKey(this.spawnIndex + 1)) {
			this.spawnIndex = this.spawnIndex + 1;
		} else {
			this.spawnIndex = 0;
		}

		GameSpawns spawn = this.spawns.get(this.spawnIndex);
		return new Location(HyperPVP.getGameWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
	}

	public Map<Integer, GameSpawns> getSpawns() {
		return spawns;
	}

	public Player getPlayer() {
		return player;
	}

	public TeamMap getTeam() {
		return team;
	}

	public TeamMap getOtherTeam() {

		List<TeamMap> teams = HyperPVP.getGame().getMapManager().getGameMap().getTeams();

		if (teams.get(0).getColor() != this.team.getColor()) {
			return teams.get(0);
		} else {
			return teams.get(1);
		}


	}

	public void setTeam(TeamMap team) {
		this.team = team;
	}

	public void setDestroyer(boolean flag) {
		this.brokeMonument = flag;
	}

	public boolean isDestroyer() {
		return this.brokeMonument;
	}

	public Player getLastDamagedBy() {
		return lastDamagedBy;
	}

	public void setLastDamagedBy(Player lastDamagedBy) {

		this.lastDamagedBy = lastDamagedBy;

		if (this.lastDamagedBy != null) {
			this.lastDamageTool = HyperPVP.getSession(this.lastDamagedBy).getItemHand(this.lastDamagedBy.getPlayer());
		}

		final Session session = this;

		if (!session.lastDamagedByTimer) {
			session.lastDamagedByTimer = true;

			Bukkit.getScheduler().scheduleSyncDelayedTask(HyperPVP.getJavaPlugin(), new Runnable() {
				@Override
				public void run() {
					session.setLastDamagedBy(null);
					session.setLastDamageTool(null);
					session.lastDamagedByTimer = false;
				}
			}, 15 * 20L);
		}
	}

	public void killIncrease() {
		this.kills = this.kills + 1;
	}

	public void killDecrease() {
		this.kills = this.kills - 1;
	}

	public void resetKills() {
		kills = 0;
	}

	public Integer getKills() {
		return kills;
	}

	public void monumentBlockIncrease(Block block) {
		monumentBlockCount.add(block);
	}

	public void resetMonumentBlock() {
		monumentBlockCount.clear();
	}

	public List<Block> getMonumentBlocks() {
		return this.monumentBlockCount;
	}

	public double getKillTimer() {
		return killTimer;
	}

	public boolean expiredKillTimer () {
		return killTimer < (System.currentTimeMillis() / 1000L);
	}

	public void resetKillTimer () {
		this.killTimer = (System.currentTimeMillis() / 1000L) + 0;
	}

	public void setKillTimer(double killTimer) {
		this.killTimer = killTimer;
	}

	public int getSpawnIndex() {
		return spawnIndex;
	}

	public void setSpawnIndex(int spawnIndex) {
		this.spawnIndex = spawnIndex;
	}

	public boolean isLastDamagedByTimer() {
		return lastDamagedByTimer;
	}

	public void setLastDamagedByTimer(boolean lastDamagedByTimer) {
		this.lastDamagedByTimer = lastDamagedByTimer;
	}

	public String getLastDamageTool() {
		return lastDamageTool;
	}

	public void setLastDamageTool(String lastDamageTool) {
		this.lastDamageTool = lastDamageTool;
	}

	public boolean isHandlingDeath() {
		return deathHandle;
	}

	public void setDeathHandle(boolean deathHandle) {
		this.deathHandle = deathHandle;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public void togglePlaying() {
		this.isPlaying = !isPlaying;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}
}
