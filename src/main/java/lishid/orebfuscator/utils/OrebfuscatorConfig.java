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
        return InitialRadius < 0 ? 0 : InitialRadius;
    }

    public static int ProcessingThreads() {
        return ProcessingThreads < 0 ? 1 : ProcessingThreads;
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
        StringBuilder retval = new StringBuilder();
        for (String world : DisabledWorlds)
            retval.append(world).append(", ");
        return retval.substring(0, retval.length() - 2);
    }

    public static byte GenerateRandomBlock() {
        return RandomBlocks[randomGenerator.nextInt(RandomBlocks.length)];
    }

    public static void SetEngineMode(int data) {
        SetData("Integers.EngineMode", data);
        EngineMode = data;
    }

    public static void SetUpdateRadius(int data) {
        SetData("Integers.UpdateRadius", data);
        UpdateRadius = data;
    }

    public static void SetInitialRadius(int data) {
        SetData("Integers.InitialRadius", data);
        InitialRadius = data;
    }

    public static void SetProcessingThreads(int data) {
        SetData("Integers.ProcessingThreads", data);
        ProcessingThreads = data;
    }

    public static void SetUpdateOnBreak(boolean data) {
        SetData("Booleans.UpdateOnBreak", data);
        UpdateOnBreak = data;
    }

    public static void SetUpdateOnDamage(boolean data) {
        SetData("Booleans.UpdateOnDamage", data);
        UpdateOnDamage = data;
    }

    public static void SetUpdateOnPhysics(boolean data) {
        SetData("Booleans.UpdateOnPhysics", data);
        UpdateOnPhysics = data;
    }

    public static void SetUpdateOnExplosion(boolean data) {
        SetData("Booleans.UpdateOnExplosion", data);
        UpdateOnExplosion = data;
    }

    public static void SetDarknessHideBlocks(boolean data) {
        SetData("Booleans.DarknessHideBlocks", data);
        DarknessHideBlocks = data;
    }

    public static void SetNoObfuscationForOps(boolean data) {
        SetData("Booleans.NoObfuscationForOps", data);
        NoObfuscationForOps = data;
    }

    public static void SetNoObfuscationForPermission(boolean data) {
        SetData("Booleans.NoObfuscationForPermission", data);
        NoObfuscationForPermission = data;
    }

    public static void SetEnabled(boolean data) {
        SetData("Booleans.Enabled", data);
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
            SetData(path, defaultData);
        return Orebfuscator.mainPlugin.getConfiguration().getBoolean(path, defaultData);
    }

    private static int GetInt(String path, int defaultData) {
        if (Orebfuscator.mainPlugin.getConfiguration().getProperty(path) == null)
            SetData(path, defaultData);
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
            byteArray[i] = (byte) list.get(i).intValue();
        return byteArray;
    }

    private static TByteHashSet IntListToTByteHashSet(List<Integer> list) {
        TByteHashSet bytes = new TByteHashSet();
        for (Integer integer : list) bytes.add((byte) integer.intValue());
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
        TransparentBlocks = IntListToTByteHashSet(GetIntList("Lists.TransparentBlocks", Arrays.asList(
                6, 8, 9, 10, 11, 18, 20, 26, 27, 28, 30,
                31, 32, 34, 37, 38, 39, 40, 44, 50, 51,
                52, 53, 54, 55, 59, 63, 64, 65, 66, 67,
                68, 69, 70, 71, 72, 75, 76, 77, 78, 79,
                81, 83, 85, 90, 92, 93, 94, 96, 101, 102,
                104, 105, 106, 107, 108, 109, 111, 113,
                114, 115
        )));
        ObfuscateBlocks = IntListToTByteHashSet(GetIntList("Lists.ObfuscateBlocks", Arrays.asList(14, 15, 16, 21, 54, 56, 73, 74)));
        DarknessObfuscateBlocks = IntListToTByteHashSet(GetIntList("Lists.DarknessObfuscateBlocks", Arrays.asList(48, 52)));
        LightEmissionBlocks = IntListToTByteHashSet(GetIntList("Lists.LightEmissionBlocks", Arrays.asList(10, 11, 50, 51, 62, 74, 76, 89, 90, 91, 94)));
        RandomBlocks = IntListToByteArray(GetIntList("Lists.RandomBlocks", Arrays.asList(5, 14, 15, 16, 21, 48, 56, 73)));
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
