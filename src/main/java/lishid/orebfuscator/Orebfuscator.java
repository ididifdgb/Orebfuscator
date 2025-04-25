package lishid.orebfuscator;

import lishid.orebfuscator.commands.OrebfuscatorCommandExecutor;
import lishid.orebfuscator.utils.OrebfuscatorCalculationThread;
import lishid.orebfuscator.utils.OrebfuscatorConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Orebfuscator extends JavaPlugin {

    private final OrebfuscatorPlayerListener playerListener = new OrebfuscatorPlayerListener(this);
    public static Orebfuscator mainPlugin;
    public static long lastSentPacket = System.currentTimeMillis() / 1000L;
    public static long lastPacketSentAttempt = (System.currentTimeMillis() / 1000L);
    private boolean shutdownState = false;

    public boolean isShutdownState() {
        return shutdownState;
    }

    public void setShutdownState(boolean shutdownState) {
        this.shutdownState = shutdownState;
    }

    public void onEnable() {
        PluginManager pm = this.getServer().getPluginManager();
        mainPlugin = this;
        OrebfuscatorConfig.Load();
        pm.registerEvents(playerListener, this);

        if (pm.getPlugin("OrebfuscatorSpoutBridge") != null) {
            System.out.println("[Orebfuscator] OrebfuscatorSpoutBridge is found, please remove it as it's no longer needed.");
            pm.disablePlugin(pm.getPlugin("OrebfuscatorSpoutBridge"));
        } else {
            PluginDescriptionFile pdfFile = this.getDescription();
            System.out.println("[Orebfuscator] version " + pdfFile.getVersion() + " initialization complete!");
            this.getCommand("ofc").setExecutor(new OrebfuscatorCommandExecutor());
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (!OrebfuscatorCalculationThread.CheckThreads()) {
                OrebfuscatorCalculationThread.SyncThreads();
            }
        }, 0, 60 * 20);
    }

    public void onDisable() {
        OrebfuscatorConfig.Save();
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("[Orebfuscator] version " + pdfFile.getVersion() + " disabled!");
    }
}
