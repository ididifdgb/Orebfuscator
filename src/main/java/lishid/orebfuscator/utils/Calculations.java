package lishid.orebfuscator.utils;

import gnu.trove.set.hash.TByteHashSet;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

import lishid.orebfuscator.Orebfuscator;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet51MapChunk;
import net.minecraft.server.TileEntity;
import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Calculations {

    public static void UpdateBlocksNearby(Block block) {
        if (OrebfuscatorConfig.Enabled() && !OrebfuscatorConfig.isTransparent((byte) block.getTypeId())) {
            HashSet<Block> blocks = GetAdjacentBlocks(new HashSet<>(), block, OrebfuscatorConfig.UpdateRadius());
            UpdateBlock(block);

            for (Block nearbyBlock : blocks) {
                UpdateBlock(nearbyBlock);
            }
        }
    }

    public static HashSet<Block> GetAdjacentBlocks(HashSet<Block> allBlocks, Block block, int countdown) {
        AddBlockCheck(allBlocks, block);
        if (countdown != 0) {
            GetAdjacentBlocks(allBlocks, block.getRelative(BlockFace.UP), countdown - 1);
            GetAdjacentBlocks(allBlocks, block.getRelative(BlockFace.DOWN), countdown - 1);
            GetAdjacentBlocks(allBlocks, block.getRelative(BlockFace.NORTH), countdown - 1);
            GetAdjacentBlocks(allBlocks, block.getRelative(BlockFace.SOUTH), countdown - 1);
            GetAdjacentBlocks(allBlocks, block.getRelative(BlockFace.EAST), countdown - 1);
            GetAdjacentBlocks(allBlocks, block.getRelative(BlockFace.WEST), countdown - 1);
        }
        return allBlocks;
    }

    public static void AddBlockCheck(HashSet<Block> allBlocks, Block block) {
        if (block == null) return;

        if (OrebfuscatorConfig.isObfuscated((byte) block.getTypeId()) ||
            OrebfuscatorConfig.isDarknessObfuscated((byte) block.getTypeId())
        ) {
            allBlocks.add(block);
        }
    }

    public static void UpdateBlock(Block block) {
        if (block == null) return;
        HashSet<CraftPlayer> players = new HashSet<>();

        for (Player player : block.getWorld().getPlayers()) {
            if (Math.abs(player.getLocation().getX() - (double) block.getX()) < 176.0D && Math.abs(player.getLocation().getZ() - (double) block.getZ()) < 176.0D) {
                players.add((CraftPlayer) player);
            }
        }

        for (CraftPlayer player2 : players) {
            player2.sendBlockChange(block.getLocation(), block.getTypeId(), block.getData());
        }
    }

    public static boolean GetAdjacentBlocksTypeID(BlockInfo info, TByteHashSet IDPool, int index, int x, int y, int z, int countdown) {
        byte id = 0;
        if (y > 126) {
            return true;
        } else {
            if (y < info.sizeY && y >= 0 && x < info.sizeX && x >= 0 && z < info.sizeZ && z >= 0 && index > 0 && info.original.length > index) {
                id = info.original[index];
            } else if (info.startY >= 0) {
                id = (byte) info.world.getTypeId(x + info.startX, y + info.startY, z + info.startZ);
            }

            if (!IDPool.contains(id) && OrebfuscatorConfig.isTransparent(id)) {
                return true;
            } else {
                if (!IDPool.contains(id)) {
                    IDPool.add(id);
                }

                return countdown != 0 && (GetAdjacentBlocksTypeID(info, IDPool, index + 1, x, y + 1, z, countdown - 1) || GetAdjacentBlocksTypeID(info, IDPool, index - 1, x, y - 1, z, countdown - 1) || GetAdjacentBlocksTypeID(info, IDPool, index + info.sizeY * info.sizeZ, x + 1, y, z, countdown - 1) || GetAdjacentBlocksTypeID(info, IDPool, index - info.sizeY * info.sizeZ, x - 1, y, z, countdown - 1) || GetAdjacentBlocksTypeID(info, IDPool, index + info.sizeY, x, y, z + 1, countdown - 1) || GetAdjacentBlocksTypeID(info, IDPool, index - info.sizeY, x, y, z - 1, countdown - 1));
            }
        }
    }

    public static boolean GetAdjacentBlocksHaveLight(BlockInfo info, int index, int x, int y, int z, int countdown) {
        return info.world.getLightLevel(x + info.startX, y + info.startY, z + info.startZ) > 0 || countdown != 0 && (GetAdjacentBlocksHaveLight(info, index + 1, x, y + 1, z, countdown - 1) || GetAdjacentBlocksHaveLight(info, index - 1, x, y - 1, z, countdown - 1) || GetAdjacentBlocksHaveLight(info, index + info.sizeY * info.sizeZ, x + 1, y, z, countdown - 1) || GetAdjacentBlocksHaveLight(info, index - info.sizeY * info.sizeZ, x - 1, y, z, countdown - 1) || GetAdjacentBlocksHaveLight(info, index + info.sizeY, x, y, z + 1, countdown - 1) || GetAdjacentBlocksHaveLight(info, index - info.sizeY, x, y, z - 1, countdown - 1));
    }

    public static void Obfuscate(OrebfuscatorCalculationThread oct, Packet51MapChunk packet, CraftPlayer player) {
        NetServerHandler handler = player.getHandle().netServerHandler;
        packet.k = false;

        BlockInfo info = new BlockInfo();
        info.world = player.getHandle().world.getWorld().getHandle();
        info.startX = packet.a;
        info.startY = packet.b;
        info.startZ = packet.c;
        info.sizeX = packet.d;
        info.sizeY = packet.e;
        info.sizeZ = packet.f;
        TByteHashSet blockList = new TByteHashSet();
        int index;
        int x;
        int y;
        if (info.world.getWorld().getEnvironment() == Environment.NORMAL && !OrebfuscatorConfig.worldDisabled(info.world.getServer().getName()) && OrebfuscatorConfig.Enabled()) {
            info.original = new byte[packet.rawData.length];
            System.arraycopy(packet.rawData, 0, info.original, 0, packet.rawData.length);
            if (info.sizeY > 1) {
                index = 0;

                for (x = 0; x < info.sizeX; ++x) {
                    for (int z = 0; z < info.sizeZ; ++z) {
                        for (y = 0; y < info.sizeY; ++y) {
                            boolean Obfuscate = false;
                            blockList.clear();
                            if (OrebfuscatorConfig.isObfuscated(info.original[index])) {
                                Obfuscate = OrebfuscatorConfig.InitialRadius() == 0 || !GetAdjacentBlocksTypeID(info, blockList, index, x, y, z, OrebfuscatorConfig.InitialRadius());
                            }

                            if (!Obfuscate && OrebfuscatorConfig.DarknessHideBlocks() && OrebfuscatorConfig.isDarknessObfuscated(info.original[index])) {
                                if (OrebfuscatorConfig.InitialRadius() == 0) {
                                    Obfuscate = true;
                                } else if (!GetAdjacentBlocksHaveLight(info, index, x, y, z, OrebfuscatorConfig.InitialRadius())) {
                                    Obfuscate = true;
                                }
                            }

                            if (Obfuscate) {
                                if (OrebfuscatorConfig.EngineMode() == 1) {
                                    packet.rawData[index] = 1;
                                } else if (OrebfuscatorConfig.EngineMode() == 2) {
                                    packet.rawData[index] = OrebfuscatorConfig.GenerateRandomBlock();
                                }
                            }

                            ++index;
                        }
                    }
                }
            }
        }

        index = packet.rawData.length;
        if (oct.deflateBuffer.length < index + 100) {
            oct.deflateBuffer = new byte[index + 100];
        }

        oct.deflater.reset();
        oct.deflater.setLevel(index < 20480 ? 1 : 6);
        oct.deflater.setInput(packet.rawData);
        oct.deflater.finish();
        x = oct.deflater.deflate(oct.deflateBuffer);
        if (x == 0) {
            x = oct.deflater.deflate(oct.deflateBuffer);
        }

        packet.g = new byte[x];
        packet.h = x;
        System.arraycopy(oct.deflateBuffer, 0, packet.g, 0, x);

        handler.networkManager.queue(packet);
        Bukkit.getServer().getScheduler().callSyncMethod(Orebfuscator.mainPlugin, () -> Orebfuscator.lastSentPacket = (System.currentTimeMillis() / 1000L));

        Object[] list = info.world.getTileEntities(info.startX, info.startY, info.startZ, info.startX + info.sizeX, info.startY + info.sizeY, info.startZ + info.sizeZ).toArray();

        for (y = 0; y < list.length; ++y) {
            TileEntity tileentity = (TileEntity) list[y];
            if (tileentity != null) {
                Packet p = tileentity.f();
                if (p != null) {
                    handler.sendPacket(p);
                }
            }
        }

    }

    public static boolean GetNetworkManagerQueue(NetworkManager networkManager, int number) {
        try {
            Field p = networkManager.getClass().getDeclaredField("x");
            p.setAccessible(true);
            return Integer.parseInt(p.get(networkManager).toString()) < number;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void LightingUpdate(Block block, boolean skipCheck) {
    }

    public static String MD5(byte[] data) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(data);
            byte[] messageDigest = algorithm.digest();
            StringBuffer hexString = new StringBuffer();

            for (byte b : messageDigest) {
                hexString.append(Integer.toHexString(255 & b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getIndex(int x, int y, int z) {
        return (x & 15) << 11 | (z & 15) << 7 | y & 127;
    }
}
