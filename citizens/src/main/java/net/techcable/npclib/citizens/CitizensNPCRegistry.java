package net.techcable.npclib.citizens;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.SimpleNPCDataStore;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.MemoryDataKey;
import net.citizensnpcs.api.util.Storage;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.NPC;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CitizensNPCRegistry implements net.techcable.npclib.NPCRegistry {
	@Getter
	private final NPCRegistry backing;
	private final Map<UUID, NPC> npcMap = new HashMap<>();
	@Getter
	private final Plugin plugin;
	private IDTracker idTracker = new IDTracker();

	public NPC convertNPC(net.citizensnpcs.api.npc.NPC citizensNpc) {
		return npcMap.get(citizensNpc.getUniqueId());
	}
	public net.citizensnpcs.api.npc.NPC convertNPC(NPC techcableNpc) {
		return ((CitizensNPC)techcableNpc).getBacking();
	}
	
	@Override
	public NPC createNPC(EntityType type) {
		return createNPC(type, UUID.randomUUID());
	}

	public NPC createNPC(EntityType type, UUID uuid) {
		if (getByUUID(uuid) != null) return getByUUID(uuid);
		net.citizensnpcs.api.npc.NPC npc = getBacking().createNPC(type, uuid, idTracker.getNextId(), "");
		NPC techcableNpc = CitizensNPC.createNPC(npc, this);
		npcMap.put(npc.getUniqueId(), techcableNpc);
		return techcableNpc;
	}

	public void deregister(NPC npc) {
		if (npc.isSpawned()) throw new IllegalStateException("Npc is spawned");
		convertNPC(npc).destroy();
		getBacking().deregister(convertNPC(npc));
		idTracker.removeId(convertNPC(npc).getId());
		npcMap.remove(npc.getUUID());
	}

	public void deregisterAll() {
		getBacking().deregisterAll();
		npcMap.clear();
	}

	public NPC getByUUID(UUID uuid) {
		return npcMap.get(uuid);
	}

	public NPC getAsNPC(Entity entity) {
		return convertNPC(getBacking().getNPC(entity));
	}

	public boolean isNPC(Entity entity) {
		return getBacking().isNPC(entity);
	}

	public Collection<NPC> listNpcs() {
		return npcMap.values();
	}
	
	public static CitizensNPCRegistry getRegistry(Plugin plugin) {
       	if (CitizensAPI.getNamedNPCRegistry("NPCLib") == null) {
	        CitizensAPI.createNamedNPCRegistry("NPCLib", makeDataStore());
	    }
	    return new CitizensNPCRegistry(CitizensAPI.getNamedNPCRegistry("NPCLib"), plugin);
	}
	
	public static CitizensNPCRegistry getRegistry(String registryName, Plugin plugin) {
	    if (CitizensAPI.getNamedNPCRegistry("NPCLib." + registryName) == null) {
	        CitizensAPI.createNamedNPCRegistry("NPCLib." + registryName, makeDataStore());
	    }
	    return new CitizensNPCRegistry(CitizensAPI.getNamedNPCRegistry("NPCLib." + registryName), plugin);
	}
	
	private static NPCDataStore makeDataStore() {
	    Storage storage = new MemoryStorage();
	    return SimpleNPCDataStore.create(storage);
	}
	
	public static class MemoryStorage implements Storage {
	    
	    public DataKey dataKey = new MemoryDataKey();
	    
	    @Override
	    public DataKey getKey(String root) {
	        return dataKey.getRelative(root);
	    }
	    
	    //NO Ops
	    @Override
	    public boolean load() {
	        return true;
	    }
	    
	    @Override
	    public void save() {}
	}
	
	private static class IDTracker {
		private int nextId;
		private Set<Integer> usedIds = new HashSet<>();
		
		public int getNextId() {
			if (isUsed(nextId)) computeNextId();
			int id = nextId;
			useId(id);
			nextId++;
			return id;
		}
		
		public void removeId(int id) {
			usedIds.remove(id);
		}
		public void useId(int id) {
			if (isUsed(id)) throw new RuntimeException("id is already in use");
			usedIds.add(id);
		}
		
		public void computeNextId() {
			for (int i = 0; i < Integer.MAX_VALUE; i++) {
				if (!isUsed(i)) { 
					this.nextId = i;
					return;
				}
			}
			throw new RuntimeException("Ran out of ids");
		}
		public boolean isUsed(int id) {
			return usedIds.contains(id);
		}
	}

	@Override
	public HumanNPC createHumanNPC() {
		return (HumanNPC) createNPC(EntityType.PLAYER);
	}
	@Override
	public HumanNPC createHumanNPC(UUID uuid) {
		return (HumanNPC) createNPC(EntityType.PLAYER, uuid);
	}
}
