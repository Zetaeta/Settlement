package net.zetaeta.settlement;


public enum Rank {
    OUTSIDER(0, ConfigurationConstants.outsiderName),
    ALLY(1, ConfigurationConstants.allyName),
    MEMBER(2, ConfigurationConstants.memberName), 
    MODERATOR(3, ConfigurationConstants.modName), 
    OWNER(4, ConfigurationConstants.ownerName);
    
    private int priority;
    private String name;
    
    private Rank(int pri, String name) {
        priority = pri;
        this.name = name;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public String getName() {
        return name;
    }
    
    public static Rank getSuperior(Rank a, Rank b) {
        return a.priority > b.priority ? a : b;
    }
    
    public boolean isSuperiorTo(Rank other) {
        return this.priority > other.priority;
    }
    
    public static Rank getByPriority(int priority) {
        switch  (priority) {
        case 0 :
            return OUTSIDER;
        case 1 :
            return ALLY;
        case 2 :
            return MEMBER;
        case 3 :
            return MODERATOR;
        case 4 :
            return OWNER;
        default :
            return null;
        }
    }
    
    public boolean isEqualOrSuperiorTo(Rank other) {
        return this.priority >= other.priority;
    }
}
