package net.zetaeta.settlement.object;

import net.zetaeta.settlement.SettlementConstants;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldCoordinate extends ChunkCoordinate implements SettlementConstants {
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
    
    public WorldCoordinate(World world, ChunkCoordinate coord) {
        super(coord.x, coord.z);
        this.world = world;
    }
    
    public SettlementWorld getSettlementWorld() {
        return server.getWorld(world);
    }
    
//    public World getWorld() {
//        return world;
//    }
}
