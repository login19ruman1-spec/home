```java
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
        // Блоки создаются только самим VoidManager.
    }

    @Override
    public boolean shouldGenerateNoise() {
        return false;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return false;
    }

    @Override
    public boolean shouldGenerateBedrock() {
        return false;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return false;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return false;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return false;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return false;
    }
}
```


