package net.zetaeta.settlement.object;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import net.zetaeta.settlement.FlatFileIO;
import net.zetaeta.settlement.SettlementAPI;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.util.StringUtil;
import net.zetaeta.util.Util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SettlementWorld implements SettlementConstants{
    private World world;
    private Map<ChunkCoordinate, Plot> plots = new ConcurrentHashMap<ChunkCoordinate, Plot>();
    
    public SettlementWorld(World world) {
        this.world = world;
//        for (Chunk chunk : world.getLoadedChunks()) {
//            loadPlot(new ChunkCoordinate(chunk));
//        }
    }
    
    public File getPlotsFolder() {
        File pFolder = new File(plugin.getPlotsFolder(), world.getName());
        pFolder.mkdirs();
        return pFolder;
    }
    
    public Collection<Plot> getPlots() {
        return plots.values();
    }
    
    public void addPlot(Plot plot) {
        plots.put(plot.getCoordinates(), plot);
    }
    
    public Plot getExistingPlot(ChunkCoordinate location) {
        return plots.get(location);
    }
    
    public Plot getPlot(ChunkCoordinate location) {
        if (plots.containsKey(location)) {
            return plots.get(location);
        }
        Plot p = new Plot(this, location);
        plots.put(location, p);
        p.setInUse(false);
        return p;
    }
    
    public Plot getPlot(Chunk chunk) {
        ChunkCoordinate cc = new ChunkCoordinate(chunk);
        return getPlot(cc);
    }
    
    public Plot getPlot(int x, int z) {
        ChunkCoordinate cc = new ChunkCoordinate(x, 0, z);
        return getPlot(cc);
    }
    
    public Plot getPlot(Location loc) {
        ChunkCoordinate cc = new ChunkCoordinate(loc);
        return getPlot(cc);
    }
    
    public Plot getPlot(Block block) {
        ChunkCoordinate cc = new ChunkCoordinate(block.getLocation());
        return getPlot(cc);
    }
    
    public void loadPlot(ChunkCoordinate coords) {
        SettlementAPI.getPersistenceManager().loadPlots(new WorldCoordinate(world, coords));
//        if (plots.containsKey(coords)) {
//            return;
//        }
//        int superChunkX = coords.x >> 2, superChunkZ = coords.z >> 2;
//        File file = new File(getPlotsFolder(), "plots@" + superChunkX + "," + superChunkZ + ".dat");
////        log.info("Loading plots, possible file = " + file.getName());
//        if (!file.exists()) {
//            plots.put(coords, new Plot(this, coords));
//            return;
//        }
//        loadPlots(file);
    }
    
//    public void loadPlots(File file) {
//        if (!file.exists()) {
//            throw new IllegalArgumentException("file must exist!");
//        }
//        FileInputStream fis = null;
//        DataInputStream dis = null;
//        try {
//            fis = new FileInputStream(file);
//            dis = new DataInputStream(fis);
//            int version = dis.readInt();
//            if (version == 0) {
//                int count = 0;
//                while (dis.available() > 0) {
//                    Plot plot = null;
//                    try {
//                        plot = FlatFileIO.loadPlotV0_0(dis);
//                        ++count;
//                        log.info("Loaded " + count + " plots");
////                        log.info(String.valueOf(plot));
//                        plots.put(plot.getCoordinates(), plot);
//                    }
//                    catch (Throwable thrown) {
//                        log.severe("Error occurred while loading plot " + (plot == null ? "" : plot.toString()) + ": " + thrown.getClass().getName());
//                        log.log(Level.SEVERE, "Error loading plots!", thrown);
//                        while (dis.readChar() != '\n') {
//                            
//                        }
//                    }
//                    // \n
//                    dis.readChar();
//                }
//            }
//            else {
//                log.severe("Error reading from plots file " + file.getName() + " Unsupported format version: " + version);
//            }
//        } catch (FileNotFoundException e) {
//            log.log(Level.SEVERE, "Could not load plots file " + file.getName(), e);
//        } catch (IOException e) {
//            log.log(Level.SEVERE, "Error while loading plots file " + file.getName(), e);
//        }
//        finally {
//            if (fis != null) {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (dis != null) {
//                try {
//                    dis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
    
    public void unloadPlotAt(ChunkCoordinate coords) {
        // TODO: Add persistence manager.
        if (plots.containsValue(coords)) {
            SettlementAPI.getPersistenceManager().savePlot(plots.get(coords));
        }
//        Plot plot = getPlot(coords);
//        plot.setInUse(false);
//        Collection<ChunkCoordinate> group = coords.getCoordsGroup();
//        boolean save = true;
//        for (ChunkCoordinate other : group) {
//            if (getPlot(other) == null) {
//                continue;
//            }
//            if (getPlot(other).isInUse()) {
//                save = false;
//            }
//        }
//        if (!save) {
//            return;
//        }
//        
//        Collection<Plot> plots = new HashSet<Plot>((int) (group.size() / 0.75));
//        for (ChunkCoordinate coord : group) {
//            plots.add(getPlot(coord));
//        }
//        savePlots(plots);
    }
    
    boolean tolog = true;
    
    public Chunk getChunk(ChunkCoordinate location) {
        Chunk chunk = world.getChunkAt(location.x << 4, location.z << 4);
        if (chunk == null) {
            log.info("getChunk: Chunk at " + location.x + ", " + location.z + " is null!");
        }
        return world.getChunkAt(location.x << 4, location.z << 4);
    }
    
    public Settlement getOwnerSettlement(ChunkCoordinate coords) {
        return getPlot(coords).getOwnerSettlement();
    }

    public World getWorld() {
        return world;
    }
}
