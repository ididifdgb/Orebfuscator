package lishid.orebfuscator.utils;

import gnu.trove.set.hash.TByteHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lishid.orebfuscator.Orebfuscator;

public class OrebfuscatorConfig {
    private static TByteHashSet TransparentBlocks = new TByteHashSet();

    private static TByteHashSet ObfuscateBlocks = new TByteHashSet();

    private static TByteHashSet DarknessObfuscateBlocks = new TByteHashSet();

    private static TByteHashSet LightEmissionBlocks = new TByteHashSet();

    private static byte[] RandomBlocks = new byte[0];

    private static List<String> DisabledWorlds = new ArrayList<>();

    private static final Random randomGenerator = new Random();

    private static int EngineMode;

    private static int UpdateRadius;

    private static int InitialRadius;

    private static int ProcessingThreads;

    private static boolean UpdateOnBreak;

    private static boolean UpdateOnDamage;

    private static boolean UpdateOnPhysics;

    private static boolean UpdateOnExplosion;

    private static boolean DarknessHideBlocks;

    private static boolean NoObfuscationForOps;

    private static boolean NoObfuscationForPermission;

    private static boolean Enabled;

    public static int EngineMode() {
        return EngineMode;
    }

    public static int UpdateRadius() {
        return UpdateRadius;
    }

    public static int InitialRadius() {
        if (InitialRadius < 0)
            return 0;
        return InitialRadius;
    }

    public static int ProcessingThreads() {
        if (ProcessingThreads < 0)
            return 1;
        return ProcessingThreads;
    }

    public static boolean UpdateOnBreak() {
        return UpdateOnBreak;
    }

    public static boolean UpdateOnDamage() {
        return UpdateOnDamage;
    }

    public static boolean UpdateOnPhysics() {
        return UpdateOnPhysics;
    }

    public static boolean UpdateOnExplosion() {
        return UpdateOnExplosion;
    }

    public static boolean DarknessHideBlocks() {
        return DarknessHideBlocks;
    }

    public static boolean NoObfuscationForOps() {
        return NoObfuscationForOps;
    }

    public static boolean NoObfuscationForPermission() {
        return NoObfuscationForPermission;
    }

    public static boolean Enabled() {
        return Enabled;
    }

    public static boolean isTransparent(byte id) {
        return (id == 0 || TransparentBlocks.contains(id));
    }

    public static boolean isObfuscated(byte id) {
        return (id == 1 || ObfuscateBlocks.contains(id));
    }

    public static boolean isDarknessObfuscated(byte id) {
        return DarknessObfuscateBlocks.contains(id);
    }

    public static boolean emitsLight(byte id) {
        return LightEmissionBlocks.contains(id);
    }

    public static boolean worldDisabled(String name) {
        return DisabledWorlds.contains(name.toLowerCase());
    }

    public static String disabledWorlds() {
        String retval = "";
        for (String world : DisabledWorlds)
            retval = String.valueOf(retval) + world + ", ";
        return retval.substring(0, retval.length() - 2);
    }

    public static byte GenerateRandomBlock() {
        return RandomBlocks[randomGenerator.nextInt(RandomBlocks.length)];
    }

    public static void SetEngineMode(int data) {
        SetData("Integers.EngineMode", Integer.valueOf(data));
        EngineMode = data;
    }

    public static void SetUpdateRadius(int data) {
        SetData("Integers.UpdateRadius", Integer.valueOf(data));
        UpdateRadius = data;
    }

    public static void SetInitialRadius(int data) {
        SetData("Integers.InitialRadius", Integer.valueOf(data));
        InitialRadius = data;
    }

    public static void SetProcessingThreads(int data) {
        SetData("Integers.ProcessingThreads", Integer.valueOf(data));
        ProcessingThreads = data;
    }

    public static void SetUpdateOnBreak(boolean data) {
        SetData("Booleans.UpdateOnBreak", Boolean.valueOf(data));
        UpdateOnBreak = data;
    }

    public static void SetUpdateOnDamage(boolean data) {
        SetData("Booleans.UpdateOnDamage", Boolean.valueOf(data));
        UpdateOnDamage = data;
    }

    public static void SetUpdateOnPhysics(boolean data) {
        SetData("Booleans.UpdateOnPhysics", Boolean.valueOf(data));
        UpdateOnPhysics = data;
    }

    public static void SetUpdateOnExplosion(boolean data) {
        SetData("Booleans.UpdateOnExplosion", Boolean.valueOf(data));
        UpdateOnExplosion = data;
    }

    public static void SetDarknessHideBlocks(boolean data) {
        SetData("Booleans.DarknessHideBlocks", Boolean.valueOf(data));
        DarknessHideBlocks = data;
    }

    public static void SetNoObfuscationForOps(boolean data) {
        SetData("Booleans.NoObfuscationForOps", Boolean.valueOf(data));
        NoObfuscationForOps = data;
    }

    public static void SetNoObfuscationForPermission(boolean data) {
        SetData("Booleans.NoObfuscationForPermission", Boolean.valueOf(data));
        NoObfuscationForPermission = data;
    }

    public static void SetEnabled(boolean data) {
        SetData("Booleans.Enabled", Boolean.valueOf(data));
        Enabled = data;
    }

    public static void SetDisabledWorlds(String name, boolean data) {
        if (!data) {
            DisabledWorlds.remove(name);
        } else {
            DisabledWorlds.add(name);
        }
        SetData("Lists.DisabledWorlds", DisabledWorlds);
    }

    private static boolean GetBoolean(String path, boolean defaultData) {
        if (Orebfuscator.mainPlugin.getConfiguration().getProperty(path) == null)
            SetData(path, Boolean.valueOf(defaultData));
        return Orebfuscator.mainPlugin.getConfiguration().getBoolean(path, defaultData);
    }

    private static int GetInt(String path, int defaultData) {
        if (Orebfuscator.mainPlugin.getConfiguration().getProperty(path) == null)
            SetData(path, Integer.valueOf(defaultData));
        return Orebfuscator.mainPlugin.getConfiguration().getInt(path, defaultData);
    }

    private static List<Integer> GetIntList(String path, List<Integer> defaultData) {
        if (Orebfuscator.mainPlugin.getConfiguration().getProperty(path) == null)
            SetData(path, defaultData);
        return Orebfuscator.mainPlugin.getConfiguration().getIntList(path, defaultData);
    }

    private static List<String> GetStringList(String path, List<String> defaultData) {
        if (Orebfuscator.mainPlugin.getConfiguration().getProperty(path) == null)
            SetData(path, defaultData);
        return Orebfuscator.mainPlugin.getConfiguration().getStringList(path, defaultData);
    }

    private static void SetData(String path, Object data) {
        Orebfuscator.mainPlugin.getConfiguration().setProperty(path, data);
        Save();
    }

    private static byte[] IntListToByteArray(List<Integer> list) {
        byte[] byteArray = new byte[list.size()];
        for (int i = 0; i < byteArray.length; i++)
            byteArray[i] = (byte)((Integer)list.get(i)).intValue();
        return byteArray;
    }

    private static TByteHashSet IntListToTByteHashSet(List<Integer> list) {
        TByteHashSet bytes = new TByteHashSet();
        for (int i = 0; i < list.size(); i++)
            bytes.add((byte)((Integer)list.get(i)).intValue());
        return bytes;
    }

    public static void Load() {
        EngineMode = GetInt("Integers.EngineMode", 2);
        if (EngineMode != 1 && EngineMode != 2) {
            EngineMode = 1;
            System.out.println("[Orebfuscator] EngineMode must be 1 or 2.");
        }
        UpdateRadius = GetInt("Integers.UpdateRadius", 2);
        InitialRadius = GetInt("Integers.InitialRadius", 1);
        if (InitialRadius > 4) {
            InitialRadius = 4;
            System.out.println("[Orebfuscator] InitialRadius must be less than 5.");
        }
        ProcessingThreads = GetInt("Integers.ProcessingThreads", 1);
        UpdateOnBreak = GetBoolean("Booleans.UpdateOnBreak", true);
        UpdateOnDamage = GetBoolean("Booleans.UpdateOnDamage", true);
        UpdateOnPhysics = GetBoolean("Booleans.UpdateOnPhysics", true);
        UpdateOnExplosion = GetBoolean("Booleans.UpdateOnExplosion", true);
        DarknessHideBlocks = GetBoolean("Booleans.DarknessHideBlocks", true);
        NoObfuscationForOps = GetBoolean("Booleans.NoObfuscationForOps", true);
        NoObfuscationForPermission = GetBoolean("Booleans.NoObfuscationForPermission", true);
        Enabled = GetBoolean("Booleans.Enabled", true);
        TransparentBlocks = IntListToTByteHashSet(GetIntList("Lists.TransparentBlocks", Arrays.asList(new Integer[] {
                Integer.valueOf(6), Integer.valueOf(8), Integer.valueOf(9), Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(18), Integer.valueOf(20), Integer.valueOf(26), Integer.valueOf(27), Integer.valueOf(28),
                Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(32), Integer.valueOf(34), Integer.valueOf(37), Integer.valueOf(38), Integer.valueOf(39), Integer.valueOf(40), Integer.valueOf(44), Integer.valueOf(50),
                Integer.valueOf(51), Integer.valueOf(52), Integer.valueOf(53), Integer.valueOf(54), Integer.valueOf(55), Integer.valueOf(59), Integer.valueOf(63), Integer.valueOf(64), Integer.valueOf(65), Integer.valueOf(66),
                Integer.valueOf(67), Integer.valueOf(68), Integer.valueOf(69), Integer.valueOf(70), Integer.valueOf(71), Integer.valueOf(72), Integer.valueOf(75), Integer.valueOf(76), Integer.valueOf(77), Integer.valueOf(78),
                Integer.valueOf(79), Integer.valueOf(81), Integer.valueOf(83), Integer.valueOf(85), Integer.valueOf(90), Integer.valueOf(92), Integer.valueOf(93), Integer.valueOf(94), Integer.valueOf(96), Integer.valueOf(101),
                Integer.valueOf(102), Integer.valueOf(104), Integer.valueOf(105), Integer.valueOf(106), Integer.valueOf(107), Integer.valueOf(108), Integer.valueOf(109), Integer.valueOf(111), Integer.valueOf(113), Integer.valueOf(114),
                Integer.valueOf(115) })));
        ObfuscateBlocks = IntListToTByteHashSet(GetIntList("Lists.ObfuscateBlocks", Arrays.asList(new Integer[] { Integer.valueOf(14), Integer.valueOf(15), Integer.valueOf(16), Integer.valueOf(21), Integer.valueOf(54), Integer.valueOf(56), Integer.valueOf(73), Integer.valueOf(74) })));
        DarknessObfuscateBlocks = IntListToTByteHashSet(GetIntList("Lists.DarknessObfuscateBlocks", Arrays.asList(new Integer[] { Integer.valueOf(48), Integer.valueOf(52) })));
        LightEmissionBlocks = IntListToTByteHashSet(GetIntList("Lists.LightEmissionBlocks", Arrays.asList(new Integer[] {
                Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(50), Integer.valueOf(51), Integer.valueOf(62), Integer.valueOf(74), Integer.valueOf(76), Integer.valueOf(89), Integer.valueOf(90), Integer.valueOf(91),
                Integer.valueOf(94) })));
        RandomBlocks = IntListToByteArray(GetIntList("Lists.RandomBlocks", Arrays.asList(new Integer[] { Integer.valueOf(5), Integer.valueOf(14), Integer.valueOf(15), Integer.valueOf(16), Integer.valueOf(21), Integer.valueOf(48), Integer.valueOf(56), Integer.valueOf(73) })));
        DisabledWorlds = GetStringList("Lists.DisabledWorlds", DisabledWorlds);
        Save();
    }

    public static void Reload() {
        Load();
    }

    public static void Save() {
        Orebfuscator.mainPlugin.getConfiguration().save();
    }
}
