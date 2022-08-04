//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lishid.orebfuscator;

import java.lang.reflect.Field;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;

import com.legacyminecraft.poseidon.PoseidonPlugin;
import com.legacyminecraft.poseidon.event.PlayerSendPacketEvent;
import com.legacyminecraft.poseidon.event.PoseidonCustomListener;
import lishid.orebfuscator.utils.Calculations;
import lishid.orebfuscator.utils.OrebfuscatorCalculationThread;
import lishid.orebfuscator.utils.OrebfuscatorConfig;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet51MapChunk;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OrebfuscatorPlayerListener implements PoseidonCustomListener {
    Orebfuscator plugin;
    public static HashMap<String, Block> blockLog = new HashMap();

    public OrebfuscatorPlayerListener(Orebfuscator plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = Event.Priority.Monitor)
    public void onSendPacket(PlayerSendPacketEvent event) {
        if (event.getPacketID() == 51 && event.getUsername() != null && Bukkit.getPlayer(event.getUsername()) != null) {
            event.setCancelled(true); //Orbfuscator will worry about dispatching the packet
            if (!OrebfuscatorCalculationThread.CheckThreads()) {
                OrebfuscatorCalculationThread.SyncThreads();
            }

            OrebfuscatorCalculationThread.Queue((Packet51MapChunk) event.getPacket(), (CraftPlayer) Bukkit.getPlayer(event.getUsername()));
        }
    }

    @EventHandler(priority = Event.Priority.Monitor)
    public void onPlayerJoin(PlayerJoinEvent event) {
//        this.TryUpdateNetServerHandler(event.getPlayer());

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            long currentTime = (System.currentTimeMillis() / 1000L);
            if (currentTime - Orebfuscator.lastSentPacket > 60) {
                System.out.println("Oreobfuscator is restarting the server as it most likely has stopped sending packets or something. I hate this fucking plugin so much");

                ((CraftServer) Bukkit.getServer()).setShuttingdown(true);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.saveData();
                    player.kickPlayer(ChatColor.RED + "Server is shutting down, please rejoin later.");
                }
                for (World world : Bukkit.getWorlds()) {
                    world.save();
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(new PoseidonPlugin(), () -> {
                    Bukkit.broadcastMessage("Stopping the server..");
                    Bukkit.shutdown();
                }, 100);

            }
        }, 200L);

    }

    @EventHandler(priority = Event.Priority.Monitor)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (this.blockLog.containsKey(event.getPlayer().getName())) {
            this.blockLog.remove(event.getPlayer().getName());
        }

    }

    @EventHandler(priority = Event.Priority.Monitor)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.isCancelled() && OrebfuscatorConfig.DarknessHideBlocks() && OrebfuscatorConfig.Enabled() && event.useInteractedBlock() != Result.DENY && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getMaterial().getId() == 10 || event.getMaterial().getId() == 11 || event.getMaterial().getId() == 327) {
                Calculations.LightingUpdate(event.getClickedBlock(), true);
            }

        }
    }

    @EventHandler(priority = Event.Priority.Monitor)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!event.isCancelled() && OrebfuscatorConfig.UpdateOnExplosion() && OrebfuscatorConfig.Enabled()) {
            Iterator var2 = event.blockList().iterator();

            while (var2.hasNext()) {
                Block block = (Block) var2.next();
                Calculations.UpdateBlocksNearby(block);
            }

        }
    }

    @EventHandler(priority = Event.Priority.Monitor)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled() && OrebfuscatorConfig.UpdateOnBreak()) {
            Calculations.UpdateBlocksNearby(event.getBlock());
        }
    }

    @EventHandler(priority = Event.Priority.Monitor)
    public void onBlockDamage(BlockDamageEvent event) {
        if (!event.isCancelled() && OrebfuscatorConfig.UpdateOnDamage()) {
            if (!blockLog.containsKey(event.getPlayer().getName()) || !((Block) blockLog.get(event.getPlayer().getName())).equals(event.getBlock())) {
                blockLog.put(event.getPlayer().getName(), event.getBlock());
                Calculations.UpdateBlocksNearby(event.getBlock());
            }
        }
    }

    @EventHandler(priority = Event.Priority.Monitor)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (!event.isCancelled() && OrebfuscatorConfig.UpdateOnPhysics()) {
            if (event.getBlock().getType() == Material.SAND && event.getBlock().getType() == Material.GRAVEL) {
                Calculations.UpdateBlocksNearby(event.getBlock());
            }
        }
    }

    @EventHandler(priority = Event.Priority.Monitor)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled() && OrebfuscatorConfig.DarknessHideBlocks() && OrebfuscatorConfig.Enabled()) {
            Calculations.LightingUpdate(event.getBlock(), false);
        }
    }

    @EventHandler(priority = Event.Priority.Monitor)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!event.isCancelled() && OrebfuscatorConfig.DarknessHideBlocks() && OrebfuscatorConfig.Enabled()) {
            Calculations.LightingUpdate(event.getBlock(), true);
        }
    }

//    public void TryUpdateNetServerHandler(Player player) {
//        try {
//            this.updateNetServerHandler(player);
//        } catch (Exception var3) {
//            System.out.println("[Orebfuscator] Error updating NerServerHandler.");
//            var3.printStackTrace();
//        }
//
//    }
//
//    public void updateNetServerHandler(Player player) {
//        CraftPlayer cp = (CraftPlayer) player;
//        CraftServer server = (CraftServer) Bukkit.getServer();
//        if (!cp.getHandle().netServerHandler.getClass().equals(OrbfuscatorNetServerHandler.class)) {
//            NetServerHandler oldHandler = cp.getHandle().netServerHandler;
//            Location loc = player.getLocation();
//            OrbfuscatorNetServerHandler handler = new OrbfuscatorNetServerHandler(server.getHandle().server, oldHandler);
//            handler.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
//            cp.getHandle().netServerHandler = handler;
//            NetworkManager nm = cp.getHandle().netServerHandler.networkManager;
//            this.setNetServerHandler(nm, handler);
//            ((CraftServer) player.getServer()).getServer().networkListenThread.a(handler);
//        }
//
//    }
//
//    public void setNetServerHandler(NetworkManager nm, NetServerHandler nsh) {
//        try {
//            Field p = nm.getClass().getDeclaredField("p");
//            p.setAccessible(true);
//            p.set(nm, nsh);
//        } catch (NoSuchFieldException var4) {
//            var4.printStackTrace();
//        } catch (IllegalArgumentException var5) {
//            var5.printStackTrace();
//        } catch (IllegalAccessException var6) {
//            var6.printStackTrace();
//        }
//
//    }
}
