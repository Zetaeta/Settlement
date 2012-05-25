package net.zetaeta.settlement;

import net.zetaeta.settlement.object.SettlementServer;
import net.zetaeta.settlement.persistence.PersistenceManager;

public final class SettlementAPI {
    private static SettlementPlugin plugin;
    private static SettlementServer server;
    
    private SettlementAPI() {}
    
    protected static void setPlugin(SettlementPlugin plugin) {
        SettlementAPI.plugin = plugin;
        server = plugin.getSettlementServer();
    }
    
    public static PersistenceManager getPersistenceManager() {
        return plugin.getPersistenceManager();
    }
    
    public static SettlementPlugin getPlugin() {
        return plugin;
    }
    
    public static SettlementServer getSettlementServer() {
        return server;
    }
}
