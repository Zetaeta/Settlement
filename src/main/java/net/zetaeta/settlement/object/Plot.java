package net.zetaeta.settlement.object;

import net.zetaeta.settlement.SettlementConstants;

import org.bukkit.Chunk;

public class Plot implements SettlementConstants {
    private ChunkCoordinate location;
    private SettlementWorld world;
    private Settlement ownerSettlement;
    private String ownerPlayerName;
    private boolean inUse = true;
    
    public Plot(SettlementWorld world, ChunkCoordinate cc) {
        this.world = world;
        this.location = cc;
        ownerSettlement = Settlement.WILDERNESS;
        ownerPlayerName = SettlementPlayer.NONE.getName();
//        log.info("Creating plot " + toString());
    }
    
    public Chunk getChunk() {
        return world.getChunk(location);
    }
    
    public Settlement getOwnerSettlement() {
        return ownerSettlement;
    }
    
    public void setOwnerSettlement(Settlement owner) {
        ownerSettlement = owner;
    }
    
    public SettlementPlayer getOwnerPlayer() {
        if (ownerPlayerName == SettlementPlayer.NONE.getName()) {
            return SettlementPlayer.NONE;
        }
        return server.getSettlementPlayer(ownerPlayerName);
    }
    
    public String getOwnerPlayerName() {
        return ownerPlayerName;
    }
    
    public void setOwnerPlayer(SettlementPlayer owner) {
        ownerPlayerName = owner.getName();
    }
    
    public void setOwnerPlayer(String ownerName) {
        ownerPlayerName = ownerName;
    }
    
    public ChunkCoordinate getCoordinates() {
        return location;
    }

    public SettlementWorld getWorld() {
        return world;
    }
    
    public void setInUse(boolean save) {
        inUse = save;
    }
    
    public boolean isInUse() {
        return inUse;
    }
    
    public String toString() {
        return getClass().getSimpleName() + " at " + location + ", settlement owner = " + ownerSettlement.getName() + ", player owner = " + ownerPlayerName;
    }
}
