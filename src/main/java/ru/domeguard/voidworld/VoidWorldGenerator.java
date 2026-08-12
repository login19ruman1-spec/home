package ваш.пакет.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import java.util.Random;

public class VoidWorldGenerator extends ChunkGenerator {
    
    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int x, int z, ChunkData chunkData) {
        // Ничего не генерируем - мир полностью пустой
    }
    
    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int x, int z, ChunkData chunkData) {
        // Тоже пусто
    }
    
    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        // Спавн в центре мира (0, 64, 0)
        return new Location(world, 0, 64, 0);
    }
}


