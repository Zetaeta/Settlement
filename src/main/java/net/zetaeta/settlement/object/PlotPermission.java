package net.zetaeta.settlement.object;

import net.zetaeta.settlement.PlotRank;

public class PlotPermission {
    private PermissionType type;
    private PlotRank rank;
    private boolean value;
    
    public PlotPermission(PlotRank rank, PermissionType type, boolean value) {
        this.rank = rank;
        this.type = type;
        this.value = value;
    }
    
    public PlotRank getRank() {
        return rank;
    }
    
    public PermissionType getType() {
        return type;
    }
    
    public boolean value() {
        return value;
    }
}
