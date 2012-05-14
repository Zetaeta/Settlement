package net.zetaeta.settlement;

import java.util.HashMap;
import java.util.Map;

import net.zetaeta.settlement.object.PermissionType;

public enum PlotRank {
    OUTSIDER(0, ConfigurationConstants.plot_outsiderName,
            new PermissionType[] {}, new PermissionType[] {}),
    ALLY(1, ConfigurationConstants.plot_settlementAllyName,
            new PermissionType[] {}, new PermissionType[] {}),
    MEMBER(2, ConfigurationConstants.plot_settlementMemberName,
            new PermissionType[] {}, new PermissionType[] {}),
    FRIEND(3, ConfigurationConstants.plot_friendName,
            new PermissionType[] {}, new PermissionType[] {}),
    PLOT_MODERATOR(4, ConfigurationConstants.plot_moderatorName,
            new PermissionType[] {}, new PermissionType[] {}),
    PLOT_OWNER(5, ConfigurationConstants.plot_ownerName,
            new PermissionType[] {}, new PermissionType[] {}),
    SETTLEMENT_MODERATOR(6, ConfigurationConstants.plot_settlementModeratorName,
            new PermissionType[] {}, new PermissionType[] {}),
    SETTLEMENT_OWNER(7, ConfigurationConstants.plot_settlementOwnerName,
            new PermissionType[] {}, new PermissionType[] {});
    
    private int priority;
    private String name;
    private Map<PermissionType, Boolean> defaults;
    
    private PlotRank(int priority, String name, PermissionType[] defaultTrue, PermissionType[] defaultFalse) {
        this.priority = priority;
        this.name = name;
        defaults = new HashMap<PermissionType, Boolean>();
        for (PermissionType pType : defaultTrue) {
            defaults.put(pType, true);
        }
        for (PermissionType pType : defaultFalse) {
            defaults.put(pType, false);
        }
        for (PermissionType pType : PermissionType.values()) {
            if (!defaults.containsKey(pType)) {
                defaults.put(pType, pType.getDefault());
            }
        }
    }
    
    public int getPriority() {
        return priority;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean getDefault(PermissionType permission) {
        return defaults.containsKey(permission) ? false : defaults.get(permission);
    }
    
    public static PlotRank getSuperior(PlotRank a, PlotRank b) {
        return a.priority > b.priority ? a : b;
    }
    
    public static PlotRank getByPriority(int priority) {
        switch  (priority) {
        case 0 :
            return OUTSIDER;
        case 1 :
            return ALLY;
        case 2 :
            return MEMBER;
        case 3 :
            return FRIEND;
        case 4 :
            return PLOT_MODERATOR;
        case 5 :
            return PLOT_OWNER;
        case 6 :
            return SETTLEMENT_MODERATOR;
        case 7 :
            return SETTLEMENT_OWNER;
        default :
            return null;
        }
    }
    
    public boolean isEqualOrSuperiorTo(PlotRank other) {
        return this.priority >= other.priority;
    }
}
