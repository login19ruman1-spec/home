package ru.domeguard;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public final class VoidGenerator extends ChunkGenerator {
    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        // Намеренно пустой генератор: в DomeVoid нет обычного ландшафта.
    }

    @Override public boolean shouldGenerateNoise() { return false; }
    @Override public boolean shouldGenerateSurface() { return false; }
    @Override public boolean shouldGenerateBedrock() { return false; }
    @Override public boolean shouldGenerateCaves() { return false; }
    @Override public boolean shouldGenerateDecorations() { return false; }
    @Override public boolean shouldGenerateMobs() { return false; }
    @Override public boolean shouldGenerateStructures() { return false; }
}
