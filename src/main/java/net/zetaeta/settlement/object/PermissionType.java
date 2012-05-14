package net.zetaeta.settlement.object;

public enum PermissionType {
    BLOCK_PLACE(false),
    BLOCK_BREAK(false),
    BUCKET_FILL(false),
    BUCKET_EMPTY(false),
    FISH(true),
    EGG_THROW(true),
    SLEEP(true),
    ENTITY_DAMAGE(true),
    DAMAGE_BY_ENTITY(true),
    ENTITY_SHEAR(true);
    
    private boolean defaultValue;
    
    private PermissionType(boolean def) {
        defaultValue = def;
    }
    
    public boolean getDefault() {
        return defaultValue;
    }
}
