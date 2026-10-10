package org.cyu.snowyleaves.neoforge.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.neoforged.fml.loading.FMLPaths;
import org.cyu.snowyleaves.neoforge.CyuSnowyLeavesNeoForge;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SnowyLeavesConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean enabled = true;
    public double accumulationSpeed = 0.003;
    public double meltingSpeed = 0.004;
    public double maxFrostAmount = 1.0;
    public String snowColor = "#F2F7FA";
    public boolean enablePerLeafVariation = true;
    public boolean enableBiomeCheck = true;
    public boolean enableDebugHud = false;

    public static SnowyLeavesConfig load() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("cyusnowyleaves.json");
        SnowyLeavesConfig config = read(path);
        config.sanitize();
        config.writeDefaults(path);
        return config;
    }

    public int snowColorRgb() {
        String value = snowColor == null ? "" : snowColor.trim();
        if (value.startsWith("#")) {
            value = value.substring(1);
        }
        if (value.length() != 6) {
            CyuSnowyLeavesNeoForge.LOGGER.warn("Invalid snowColor '{}', expected 6-hex RGB like #F2F7FA.", snowColor);
            return 0xF2F7FA;
        }
        try {
            return Integer.parseInt(value, 16) & 0xFFFFFF;
        } catch (NumberFormatException ex) {
            CyuSnowyLeavesNeoForge.LOGGER.warn("Failed to parse snowColor '{}' as hex RGB, using default.", snowColor);
            return 0xF2F7FA;
        }
    }

    private static SnowyLeavesConfig read(Path path) {
        if (!Files.exists(path)) {
            return new SnowyLeavesConfig();
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            SnowyLeavesConfig config = GSON.fromJson(reader, SnowyLeavesConfig.class);
            return config == null ? new SnowyLeavesConfig() : config;
        } catch (IOException | JsonSyntaxException ex) {
            CyuSnowyLeavesNeoForge.LOGGER.warn("Failed to read config/cyusnowyleaves.json, using defaults.", ex);
            return new SnowyLeavesConfig();
        }
    }

    private void writeDefaults(Path path) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException ex) {
            CyuSnowyLeavesNeoForge.LOGGER.warn("Failed to write config/cyusnowyleaves.json.", ex);
        }
    }

    private void sanitize() {
        accumulationSpeed = clamp(accumulationSpeed, 0.0, 0.05);
        meltingSpeed = clamp(meltingSpeed, 0.0, 0.05);
        maxFrostAmount = clamp(maxFrostAmount, 0.0, 1.0);
        if (snowColor == null || snowColor.isBlank()) {
            snowColor = "#F2F7FA";
        }
    }

    private static double clamp(double value, double min, double max) {
        if (!Double.isFinite(value)) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }
}
