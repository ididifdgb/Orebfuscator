package lishid.orebfuscator;

import com.legacyminecraft.poseidon.PoseidonPlugin;
import com.legacyminecraft.poseidon.event.PlayerSendPacketEvent;
import com.legacyminecraft.poseidon.event.PoseidonCustomListener;
import lishid.orebfuscator.utils.Calculations;
import lishid.orebfuscator.utils.OrebfuscatorCalculationThread;
import lishid.orebfuscator.utils.OrebfuscatorConfig;
import net.minecraft.server.Packet51MapChunk;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class OrebfuscatorPlayerListener implements PoseidonCustomListener {

    private final Orebfuscator plugin;
    public static HashMap<String, Block> blockLog = new HashMap<>();

    public OrebfuscatorPlayerListener(Orebfuscator plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = Event.Priority.Monitor)
    public void onSendPacket(PlayerSendPacketEvent event) {
        if (event.getPacketID() == 51 && event.getUsername() != null && Bukkit.getPlayer(event.getUsername()) != null) {
            event.setCancelled(true); //Orbfuscator will worry about dispatching the packet
            Orebfuscator.lastPacketSentAttempt = (System.currentTimeMillis() / 1000L); //Reset the last packet sent attempt
            if (!OrebfuscatorCalculationThread.CheckThreads()) {
                OrebfuscatorCalculationThread.SyncThreads();
            }

            OrebfuscatorCalculationThread.Queue((Packet51MapChunk) event.getPacket(), (CraftPlayer) Bukkit.getPlayer(event.getUsername()));
        }
    }

    @EventHandler(priority = Event.Priority.Monitor)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
//            long currentTime = (System.currentTimeMillis() / 1000L);
            if (Bukkit.getOnlinePlayers().length == 0) {
                return;
            }
            if (Orebfuscator.lastPacketSentAttempt - Orebfuscator.lastSentPacket > 30 && !plugin.isShutdownState()) {
                plugin.setShutdownState(true);
                //Warn players that the server will shutdown
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Orebfuscator" + ChatColor.WHITE + "] " + ChatColor.RED + "The server will shutdown in 30 seconds due to a internal error in the Orebfuscator plugin.");
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Orebfuscator" + ChatColor.WHITE + "] " + ChatColor.RED + "We are working to address this issue. Restarts are a temporary workaround until the issue is fixed.");

                //Shutdown the server in 30 seconds
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    System.out.println("Oreobfuscator is restarting the server as it most likely has stopped sending packets or something. I hate this fucking plugin so much");

                    //Print some debug info
                    System.out.println("Orebfuscator Debug: " +
                            "Last Packet Sent Attempt: " + Orebfuscator.lastPacketSentAttempt + ", " +
                            "Last Sent Packet: " + Orebfuscator.lastSentPacket + ", " +
                            "Current Calculation Threads: " + OrebfuscatorCalculationThread.getThreads() + ", " +
                            "Current Calculation Queue Size: " + OrebfuscatorCalculationThread.getQueueSize() + ", " +
                            "Running Calculation Threads: " + OrebfuscatorCalculationThread.getRunningThreads());


                    ((CraftServer) Bukkit.getServer()).setShuttingdown(true);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.saveData();
                        player.kickPlayer(ChatColor.RED + "Server is restarting to resolve issues. Please rejoin shortly.");
                    }
                    for (World world : Bukkit.getWorlds()) {
                        world.save();
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(new PoseidonPlugin(), () -> {
                        Bukkit.broadcastMessage("Stopping the server..");
                        Bukkit.shutdown();
                    }, 100);
                }, 20 * 30);

            }



            }, 200L);

    }

    @EventHandler(priority = Event.Priority.Monitor)
    public void onPlayerQuit(PlayerQuitEvent event) {
        blockLog.remove(event.getPlayer().getName());
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!OrebfuscatorConfig.DarknessHideBlocks() ||
            !OrebfuscatorConfig.Enabled() ||
            event.useInteractedBlock() == Result.DENY ||
            event.getAction() != Action.RIGHT_CLICK_BLOCK
        ) {
            return;
        }

        int id = event.getMaterial().getId();
        if (id == 10 || id == 11 || id == 327) {
            Calculations.LightingUpdate(event.getClickedBlock(), true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!OrebfuscatorConfig.UpdateOnExplosion() || !OrebfuscatorConfig.Enabled())
            return;

        for (Block block : event.blockList()) {
            Calculations.UpdateBlocksNearby(block);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!OrebfuscatorConfig.UpdateOnBreak()) return;

        Calculations.UpdateBlocksNearby(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onBlockDamage(BlockDamageEvent event) {
        if (!OrebfuscatorConfig.UpdateOnDamage()) return;

        if (!blockLog.containsKey(event.getPlayer().getName()) ||
            !blockLog.get(event.getPlayer().getName()).equals(event.getBlock())
        ) {
            blockLog.put(event.getPlayer().getName(), event.getBlock());
            Calculations.UpdateBlocksNearby(event.getBlock());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (!OrebfuscatorConfig.UpdateOnPhysics()) return;

        if (event.getBlock().getType() == Material.SAND || event.getBlock().getType() == Material.GRAVEL) {
            Calculations.UpdateBlocksNearby(event.getBlock());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (OrebfuscatorConfig.DarknessHideBlocks() && OrebfuscatorConfig.Enabled()) {
            Calculations.LightingUpdate(event.getBlock(), false);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (OrebfuscatorConfig.DarknessHideBlocks() && OrebfuscatorConfig.Enabled()) {
            Calculations.LightingUpdate(event.getBlock(), true);
        }
    }

}
