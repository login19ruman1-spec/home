package ru.domeguard.voidworld;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public final class VoidWorldGenerator extends ChunkGenerator {

    @Override
    public void generateNoise(
            WorldInfo worldInfo,
            Random random,
            int chunkX,
            int chunkZ,
            ChunkData chunkData
    ) {
        // Полностью пустой мир.
    }
}
