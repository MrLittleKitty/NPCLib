package net.techcable.npclib.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.techcable.npclib.NPC;
import net.techcable.npclib.nms.NMS;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.google.common.base.Throwables;

public class Util {
    private Util() {}
    
    
    private static NMS nms;
    public static NMS getNMS() {
    	if (nms == null) {
    		try {
        		String version = getVersion();
        		Class<?> rawClass = Class.forName("net.techcable.npclib.nms.versions." + version + ".NMS");
        		Class<? extends NMS> nmsClass = rawClass.asSubclass(NMS.class);
        		Constructor<? extends NMS> constructor = nmsClass.getConstructor();
        		return constructor.newInstance();
        	} catch (ClassNotFoundException ex) {
        		throw new UnsupportedOperationException("Unsupported nms version", ex);
        	} catch (InvocationTargetException ex) {
        		throw Throwables.propagate(ex.getTargetException());
        	} catch (Exception ex) {
        		throw Throwables.propagate(ex);
        	}
    	}
    	return nms;
    }
    
    public static String getVersion() {
    	String packageName = Bukkit.getServer().getClass().getPackage().getName();
    	return packageName.substring(packageName.lastIndexOf(".") + 1);
    }
    
    public static void look(Entity entity, Location toLook) {
        if (!entity.getWorld().equals(toLook.getWorld()))
            return;
        Location fromLocation = entity.getLocation();
        double xDiff, yDiff, zDiff;
        xDiff = toLook.getX() - fromLocation.getX();
        yDiff = toLook.getY() - fromLocation.getY();
        zDiff = toLook.getZ() - fromLocation.getZ();

        double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double distanceY = Math.sqrt(distanceXZ * distanceXZ + yDiff * yDiff);

        double yaw = Math.toDegrees(Math.acos(xDiff / distanceXZ));
        double pitch = Math.toDegrees(Math.acos(yDiff / distanceY)) - 90;
        if (zDiff < 0.0)
            yaw += Math.abs(180 - yaw) * 2;

        getNMS().getAsNPC(entity).look(pitch, yaw);
    }
    
    public static Player[] getNearbyPlayers(int range, Location l) {
    	List<Player> nearby = new ArrayList<>(12);
    	for (Player p : Bukkit.getOnlinePlayers()) {
    		double distance = p.getLocation().distanceSquared(l);
    		if (distance <= range) {
    			nearby.add(p);
    		}
    	}
    	return nearby.toArray(new Player[nearby.size()]);
    }
}