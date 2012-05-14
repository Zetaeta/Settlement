package net.zetaeta.settlement.object;

import net.zetaeta.settlement.Rank;

public class SettlementPermission {
    private PermissionType type;
    private Rank rank;
    private boolean value;
    
    public  SettlementPermission(Rank rank, PermissionType type, boolean value) {
        this.rank = rank;
        this.type = type;
        this.value = value;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public PermissionType getType() {
        return type;
    }
    
    public boolean value() {
        return value;
    }
}
