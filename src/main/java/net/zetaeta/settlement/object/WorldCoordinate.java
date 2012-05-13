package net.zetaeta.settlement.object;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldCoordinate extends ChunkCoordinate {
    public final World world;
    
    public WorldCoordinate(Location location) {
        super(location);
        world = location.getWorld();
    }
    
    public WorldCoordinate(Chunk chunk) {
        super(chunk);
        world = chunk.getWorld();
    }
    
    public WorldCoordinate(World world, int x, int y, int z) {
        super(x, y, z);
        this.world = world;
    }
    
    public WorldCoordinate(World world, int x, int z) {
        super(x, z);
        this.world = world;
    }
    
//    public World getWorld() {
//        return world;
//    }
}
