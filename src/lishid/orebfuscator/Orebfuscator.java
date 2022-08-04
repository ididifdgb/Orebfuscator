//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lishid.orebfuscator;

import lishid.orebfuscator.commands.OrebfuscatorCommandExecutor;
import lishid.orebfuscator.utils.OrebfuscatorConfig;
import lishid.orebfuscator.utils.PermissionRelay;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Orebfuscator extends JavaPlugin {
    //    private final OrebfuscatorBlockListener blockListener = new OrebfuscatorBlockListener(this);
//    private final OrebfuscatorEntityListener entityListener = new OrebfuscatorEntityListener(this);
    private final OrebfuscatorPlayerListener playerListener = new OrebfuscatorPlayerListener(this);
    public static boolean usingSpout = false;
    public static Orebfuscator mainPlugin;
    public static long lastSentPacket = System.currentTimeMillis() / 1000L;

    public Orebfuscator() {
    }

    public void onEnable() {
        PluginManager pm = this.getServer().getPluginManager();
        PermissionRelay.Setup(pm);
        mainPlugin = this;
        OrebfuscatorConfig.Load();
//        pm.registerEvent(Type.PLAYER_JOIN, this.playerListener, Priority.Monitor, this);
//        pm.registerEvent(Type.PLAYER_QUIT, this.playerListener, Priority.Monitor, this);
//        pm.registerEvent(Type.BLOCK_BREAK, this.blockListener, Priority.Monitor, this);
//        pm.registerEvent(Type.BLOCK_DAMAGE, this.blockListener, Priority.Monitor, this);
//        pm.registerEvent(Type.BLOCK_PHYSICS, this.blockListener, Priority.Monitor, this);
//        pm.registerEvent(Type.ENTITY_EXPLODE, this.entityListener, Priority.Monitor, this);
//        pm.registerEvent(Type.PLAYER_JOIN, this.playerListener, Priority.Low, this);
        pm.registerEvents(playerListener, this);
        if (pm.getPlugin("OrebfuscatorSpoutBridge") != null) {
            System.out.println("[Orebfuscator] OrebfuscatorSpoutBridge is found, please remove it as it's no longer needed.");
            pm.disablePlugin(pm.getPlugin("OrebfuscatorSpoutBridge"));
        } else {
            PluginDescriptionFile pdfFile = this.getDescription();
            System.out.println("[Orebfuscator] version " + pdfFile.getVersion() + " initialization complete!");
            this.getCommand("ofc").setExecutor(new OrebfuscatorCommandExecutor(this));
        }
    }

    public void onDisable() {
        OrebfuscatorConfig.Save();
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("[Orebfuscator] version " + pdfFile.getVersion() + " disabled!");
    }
}
