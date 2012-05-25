package net.zetaeta.settlement.listeners;

import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.SettlementThreadManager;
import net.zetaeta.settlement.object.ChunkCoordinate;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class SettlementWorldListener implements Listener, SettlementConstants {
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(final ChunkLoadEvent event) {
//        log.info("ChunkLoadEvent: " + event.getEventName());
        SettlementThreadManager.submitAsyncTask(new Runnable() {
            public void run() {
                server.getWorld(event.getWorld()).loadPlot(new ChunkCoordinate(event.getChunk()));
            }
        });
    }
    boolean log = true;
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(final ChunkUnloadEvent event) {
        final long time = System.nanoTime();
        if (log) {
            System.out.println("Scheduling loading chunk, current time: " + (time));
        }
        final boolean logLocal = log;
        SettlementThreadManager.submitAsyncTask(new Runnable() {
            public void run() {
                if (logLocal) {
                    System.out.println("Actually loading, current time = " + System.nanoTime() + ", diff = " + (System.nanoTime() - time));
                }
                server.getWorld(event.getWorld()).unloadPlotAt(new ChunkCoordinate(event.getChunk()));
            }
        });
        if (log) {
            System.out.println("Scheduled loading chunk, current time: " + System.nanoTime() + ", diff = " + (System.nanoTime() - time));
            log = false;
        }
    }
}
