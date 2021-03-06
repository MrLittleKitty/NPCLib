package net.techcable.npclib;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface NPC {
	/**
	 * Despawn this npc
	 * 
	 * Once despawned it can not be respawned
	 * It will be deregistered from the registry
	 * 
	 * @return true if was able to despawn
	 */
	public boolean despawn();
	/**
	 * The npc's head will look in this direction
	 * @param toFace the direction to look
	 */
	public void faceLocation(Location toFace);
	
	/**
	 * Get the entity associated with this npc
	 * @return the entity
	 */
	public Entity getEntity();
	/**
	 * Retrieve the name of this npc
	 * @return this npc's name
	 */
	public String getName();
	/**
	 * Get this npc's uuid
	 * @return the uuid of this npc
	 */
	public UUID getUUID();
	/**
	 * Returns weather the npc is spawned
	 * @return true if the npc is spawned
	 */
	public boolean isSpawned();
	
	/**
	 * Set the current name of the npc
	 * @param name the new name
	 */
	public void setName(String name);
	
	/**
	 * Spawn this npc
	 * @param toSpawn location to spawn this npc
	 * @return true if the npc was able to spawn
	 */
	public boolean spawn(Location toSpawn);
	
	/**
	 * Set the protected status of this NPC
	 * true by default
	 * @param protect whether or not this npc is invincible
	 */
	public void setProtected(boolean protect);
	
	/**
	 * Check if the NPC is protected from damage
	 * @return The protected status of the NPC
	 */
	public boolean isProtected();
	
	/**
	 * Return this npc's skin
	 * 
	 * A value of null represents a steve skin
	 * 
	 * @return this npc's skin
	 */
	public UUID getSkin();
	
	/**
	 * Set the npc's skin
	 * 
	 * A value of null represents a steve skin
	 * 
	 * @param skin the player id with the skin you want
	 * 
	 * @throws UnsupportedOperationException if skins aren't supported
	 */
	public void setSkin(UUID skin);
	
	/**
	 * Set the npc's skin
	 * 
	 * A value f null represents a steve skin
	 * 
	 * @param skin the player name with the skin you want
	 * 
	 * @throws UnsupportedOperationException if skins aren't supported
	 */
	public void setSkin(String skin);
}
