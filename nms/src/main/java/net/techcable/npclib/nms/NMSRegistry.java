package net.techcable.npclib.nms;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.NPC;
import net.techcable.npclib.NPCRegistry;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class NMSRegistry implements NPCRegistry {

	private static final EntityType[] SUPPORTED_TYPES = new EntityType[] {EntityType.PLAYER};
	private final Plugin plugin;
	private Map<UUID, NMSNPC> npcMap = new HashMap<>();

	@Override
	public NPC createNPC(EntityType type) {
		return createNPC(type, UUID.randomUUID());
	}

	@Override
	public NPC createNPC(EntityType type, UUID uuid) {
		if (!ArrayUtils.contains(SUPPORTED_TYPES, type)) throw new UnsupportedOperationException(type.toString() + " is an Unsupported Entity Type");
		if (npcMap.containsKey(uuid)) return getByUUID(uuid);
		NMSNPC npc = new NMSNPC(uuid, type, this);
		npcMap.put(uuid, npc);
		return npc;
	}

	@Override
	public void deregister(NPC npc) {
		if (!isRegistered(npc)) return;
		if (npc.isSpawned()) throw new IllegalStateException("NPC is spawned; can't deregister");
		getNpcMap().remove(npc.getUUID());
	}

	public boolean isRegistered(NPC npc) {
		return npcMap.containsKey(npc.getUUID());
	}
	
	@Override
	public void deregisterAll() {
		for (NPC npc : listNpcs()) {
			deregister(npc);
		}
	}

	@Override
	public NPC getByUUID(UUID uuid) {
		return getNpcMap().get(uuid);
	}

	@Override
	public NPC getAsNPC(Entity entity) {
		if (!isNPC(entity)) throw new IllegalStateException("Not an NPC");
		return Util.getNMS().getAsNPC(entity).getNpc();
	}

	@Override
	public boolean isNPC(Entity entity) {
		return Util.getNMS().getAsNPC(entity) != null;
	}

	@Override
	public Collection<? extends NPC> listNpcs() {
		return Collections.unmodifiableCollection(getNpcMap().values());
	}

	@Override
	public HumanNPC createHumanNPC() {
		return createHumanNPC(UUID.randomUUID());
	}

	@Override
	public HumanNPC createHumanNPC(UUID uuid) {
		return (HumanNPC) createNPC(EntityType.PLAYER, uuid);
	}

}
