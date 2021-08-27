//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lishid.orebfuscator.utils;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import net.minecraft.server.Packet51MapChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public class OrebfuscatorCalculationThread extends Thread implements Runnable {
    private boolean kill = false;
    private static final int QUEUE_CAPACITY = 10240;
    private static ArrayList<OrebfuscatorCalculationThread> threads = new ArrayList();
    private static final LinkedBlockingDeque<ObfuscatedPlayerPacket> queue = new LinkedBlockingDeque(10240);

    public OrebfuscatorCalculationThread() {
    }

    public static int getThreads() {
        return threads.size();
    }

    public static boolean CheckThreads() {
        return threads.size() == OrebfuscatorConfig.ProcessingThreads();
    }

    public static void SyncThreads() {
        if (threads.size() != OrebfuscatorConfig.ProcessingThreads()) {
            int extra = threads.size() - OrebfuscatorConfig.ProcessingThreads();
            int i;
            if (extra > 0) {
                for(i = extra; i > 0; --i) {
                    ((OrebfuscatorCalculationThread)threads.get(i - 1)).kill = true;
                    threads.remove(i - 1);
                }
            } else if (extra < 0) {
                for(i = 0; i < -extra; ++i) {
                    OrebfuscatorCalculationThread thread = new OrebfuscatorCalculationThread();
                    thread.start();
                    thread.setName("Orebfuscator Calculation Thread");
                    threads.add(thread);
                }
            }

        }
    }

    public void run() {
        while(!this.isInterrupted() && !this.kill) {
            try {
                this.handle();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

    }

    private void handle() {
        try {
            ObfuscatedPlayerPacket packet = (ObfuscatedPlayerPacket)queue.take();
            Calculations.Obfuscate(packet.packet, packet.player);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public static void Queue(Packet51MapChunk packet, CraftPlayer player) {
        while(true) {
            try {
                queue.put(new ObfuscatedPlayerPacket(player, packet));
                return;
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }
    }
}
