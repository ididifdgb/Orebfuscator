//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lishid.orebfuscator.utils;

//import com.nijiko.permissions.PermissionHandler;
//import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class PermissionRelay {
//    public static PermissionHandler handler;

    public PermissionRelay() {
    }

    public static void Setup(PluginManager pm) {
//        if (handler == null && pm.getPlugin("Permissions") != null) {
//            handler = ((Permissions)pm.getPlugin("Permissions")).getHandler();
//        }

    }

    public static boolean hasPermission(Player player, String permission) {
//        if (handler != null) {
//            return handler.has(player, permission);
//        } else {
            return player.isOp() || player.hasPermission(permission);
//        }
    }
}
