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
package us.hyperpvp.game.map.region;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Region {

	private RegionType type;
	private String alert;
	private int maxX;
	private int maxY;
	private int maxZ;
	private int minX;
	private int minY;
	private int minZ;
	private World world;
	private List<ChatColor> teamWhitelist;
	private List<Material> blocks;
	private List<Block> monumentBlocks;
	
	public Region(World world, RegionType type, List<ChatColor> teamWhitelist, List<Material> blocks, String alert, int highX, int highY, int highZ, int lowX, int lowY, int lowZ) {

		this.type = type;
		this.world = world;
		this.alert = alert;

		this.maxX = Math.max(highX, lowX) + 1;
		this.maxY = Math.max(highY, lowY) + 1;
		this.maxZ = Math.max(highZ, lowZ) + 1;

		this.minX = Math.min(highX, lowX) - 1;
		this.minY = Math.min(highY, lowY) - 1;
		this.minZ = Math.min(highZ, lowZ) - 1;

		this.teamWhitelist = teamWhitelist;
		this.blocks = blocks;
		this.monumentBlocks = new ArrayList<Block>();
	
		if (this.type == RegionType.DTM) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					for (int z = minZ; z <= maxZ; z++) {

						Block block = this.world.getBlockAt(x, y, z);
						
						if (block.getType() == Material.OBSIDIAN) {
							this.monumentBlocks.add(block);
						}
					}
				}
			}
		}
		
	}

	public boolean hasLocation(Location loc) {

		if (loc.getBlockX() <= this.minX || loc.getBlockX() >= this.maxX)
			return false;

		if (loc.getBlockZ() <= this.minZ || loc.getBlockZ() >= this.maxZ)
			return false;

		if (loc.getBlockY() <= this.minY || loc.getBlockY() >= this.maxY)
			return false;

		return true;
	}

	public RegionType getType() {
		return this.type;
	}

	public String getAlert() {
		return this.alert;
	}

	public List<ChatColor> getTeamWhitelist() {
		return this.teamWhitelist;
	}

	public List<Material> getBlocks() {
		return this.blocks;
	}

	public List<Block> getMonumentBlocks() {
		return this.monumentBlocks;
	}

	/**
	 * @return the maxX
	 */
	public int getMaxX() {
		return maxX;
	}

	/**
	 * @param maxX the maxX to set
	 */
	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	/**
	 * @return the maxY
	 */
	public int getMaxY() {
		return maxY;
	}

	/**
	 * @param maxY the maxY to set
	 */
	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	/**
	 * @return the maxZ
	 */
	public int getMaxZ() {
		return maxZ;
	}

	/**
	 * @param maxZ the maxZ to set
	 */
	public void setMaxZ(int maxZ) {
		this.maxZ = maxZ;
	}

	/**
	 * @return the minX
	 */
	public int getMinX() {
		return minX;
	}

	/**
	 * @param minX the minX to set
	 */
	public void setMinX(int minX) {
		this.minX = minX;
	}

	/**
	 * @return the minY
	 */
	public int getMinY() {
		return minY;
	}

	/**
	 * @param minY the minY to set
	 */
	public void setMinY(int minY) {
		this.minY = minY;
	}

	/**
	 * @return the minZ
	 */
	public int getMinZ() {
		return minZ;
	}

	/**
	 * @param minZ the minZ to set
	 */
	public void setMinZ(int minZ) {
		this.minZ = minZ;
	}

}
