package net.techcable.npclib.nms.versions.v1_7_R4.network;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R4.NetworkManager;
import net.techcable.npclib.util.ReflectUtil;
import lombok.*;

@Getter
public class NPCNetworkManager extends NetworkManager {

	public NPCNetworkManager() {
		super(false); //MCP = isClientSide
		
		Field channel = ReflectUtil.makeField(NetworkManager.class, "m"); //MCP = channel
		Field address = ReflectUtil.makeField(NetworkManager.class, "n"); //MCP = address
		
		ReflectUtil.setField(channel, this, new NullChannel());
		ReflectUtil.setField(address, this, new NullSocketAddress());
		
	}

}
