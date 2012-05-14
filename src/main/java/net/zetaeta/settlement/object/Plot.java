package net.zetaeta.settlement.object;

import java.util.HashSet;
import java.util.Set;

import net.zetaeta.settlement.PlotRank;
import net.zetaeta.settlement.SettlementConstants;

import org.bukkit.Chunk;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Plot implements SettlementConstants {
    private ChunkCoordinate location;
    private SettlementWorld world;
    private Settlement ownerSettlement;
    private String ownerPlayerName;
    private boolean inUse = true;
    private Set<PlotPermission> permissions = new HashSet<PlotPermission>();
    private Table<PlotRank, PermissionType, Boolean> runtimePermissions = HashBasedTable.create();
    
    public Plot(SettlementWorld world, ChunkCoordinate cc) {
        this.world = world;
        this.location = cc;
        ownerSettlement = Settlement.WILDERNESS;
        ownerPlayerName = SettlementPlayer.NONE.getName();
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
    
    public boolean hasPermission(PlotRank rank, PermissionType permission) {
        if (runtimePermissions.contains(rank, permission)) {
            return runtimePermissions.get(rank, permission);
        }
        else {
            return rank.getDefault(permission);
        }
    }
    
    public String toString() {
        return getClass().getSimpleName() + " at " + location + ", settlement owner = " + ownerSettlement.getName() + ", player owner = " + ownerPlayerName;
    }
}
