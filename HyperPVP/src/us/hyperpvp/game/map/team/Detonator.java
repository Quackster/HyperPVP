package us.hyperpvp.game.map.team;

import org.bukkit.entity.Entity;

public class Detonator {

	private Entity tnt;

	public Detonator(Entity tnt2) {
		this.tnt = tnt2;
	}

	public Entity getTnt() {
		return tnt;
	}

}
