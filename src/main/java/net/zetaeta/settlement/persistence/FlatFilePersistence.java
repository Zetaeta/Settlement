package net.zetaeta.settlement.persistence;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import net.zetaeta.settlement.Rank;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.SettlementPlugin;
import net.zetaeta.settlement.object.ChunkCoordinate;
import net.zetaeta.settlement.object.Plot;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementData;
import net.zetaeta.settlement.object.SettlementPlayer;
import net.zetaeta.settlement.object.SettlementWorld;
import net.zetaeta.settlement.object.WorldCoordinate;
import net.zetaeta.util.StringUtil;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class FlatFilePersistence implements PersistenceManager, SettlementConstants {

    @Override
    public void saveSettlements(Collection<Settlement> settlements) {
        log.info("Saving Settlements...");
        File settlementsFile = new File(plugin.getSettlementsFolder(), "settlements.dat");
        if (!settlementsFile.exists()) {
            try {
                settlementsFile.createNewFile();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not create settlements.dat file!", e);
                e.printStackTrace();
                return;
            }
        }
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(new FileOutputStream(settlementsFile));
        } catch (FileNotFoundException e) {
            log.severe("Could not open settlements.dat file!");
            return;
        }
        try {
            dos.writeInt(SETTLEMENT_FILE_VERSION);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        for (Settlement settlement : settlements) {
            log.info("Saving settlement " + settlement.getName());
            try {
                saveSettlementV0_0(settlement, dos);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error while saving Settlement " + settlement.getName() + "!", e);
                e.printStackTrace();
            }
            try {
                dos.writeChar('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File oldFile = new File(plugin.getSavedDataFolder(), "settlements.dat");
        if (oldFile.exists()) {
            oldFile.delete();
        }
    }

    @Override
    public void savePlayer(SettlementPlayer player) {
        File playerFile = new File(SettlementPlugin.plugin.getPlayersFolder(), player.getName() + ".dat");
        try {
            playerFile.createNewFile();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not create data file for player " + player.getName(), e);
            e.printStackTrace();
        }
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(new FileOutputStream(playerFile));
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Could not find data file for player " + player.getName(), e);
            e.printStackTrace();
            try {
                dos.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return;
        }
        try {
            savePlayerV0_0(player, dos);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error occurred during saving of player " + player.getName(), e);
            e.printStackTrace();
        }
        finally {
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void savePlot(Plot plot) {
        plot.setInUse(false);
        ChunkCoordinate coords = plot.getCoordinates();
        Collection<ChunkCoordinate> group = coords.getCoordsGroup();
        SettlementWorld world = plot.getWorld();
        boolean save = true;
        for (ChunkCoordinate other : group) {
            if (world.getPlot(other) == null) {
                continue;
            }
            if (world.getPlot(other).isInUse()) {
                save = false;
            }
        }
        if (!save) {
            return;
        }
        int superChunkX = coords.x >> 2, superChunkZ = coords.z >> 2;
        File file = new File(world.getPlotsFolder(), "plots@" + superChunkX + "," + superChunkZ + ".dat");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not create plot file for " + file.getName(), e);
                return;
            }
        }
        Collection<Plot> plots = new HashSet<Plot>((int) (group.size() / 0.75));
        for (ChunkCoordinate coord : group) {
            plots.add(world.getPlot(coord));
        }
        savePlots(plots, file);
    }
    
    public void savePlots(Collection<Plot> plots, File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("file must exist!");
        }
        FileOutputStream fos = null;
        DataOutputStream dos = null;
        try {
            fos = new FileOutputStream(file);
            dos = new DataOutputStream(fos);
            dos.writeInt(PLOT_FILE_VERSION);
            for (Plot plot : plots) {
                if (plot == null) {
                    continue;
                }
                try {
                    savePlotV0_0(plot, dos);
                }
                catch (Throwable thrown) {
                    log.severe("Error occurred while saving plot " + (plot == null ? "" : plot.toString()) + ": " + thrown.getClass().getName());
                    log.log(Level.SEVERE, "Error saving plots!", thrown);
                }
                dos.writeChar('\n');
            }
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Could not load plots file " + file.getName(), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error while loading plots file " + file.getName(), e);
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Collection<Plot> loadPlots(WorldCoordinate coord) {
        Collection<Plot> plots = new HashSet<Plot>();
        SettlementWorld world = coord.getSettlementWorld();
        if (world.getExistingPlot(coord) != null) {
            plots.add(world.getExistingPlot(coord));
            return plots;
        }
        int superChunkX = coord.x >> 2, superChunkZ = coord.z >> 2;
        File file = new File(world.getPlotsFolder(), "plots@" + superChunkX + "," + superChunkZ + ".dat");
//        log.info("Loading plots, possible file = " + file.getName());
        if (!file.exists()) {
            plots.add(new Plot(coord));
//            world.addPlot(plot);
            return plots;
        }
        return loadPlots(file);
    }

    public Collection<Plot> loadPlots(File file) {
        Collection<Plot> plots = new HashSet<Plot>();
        if (!file.exists()) {
            throw new IllegalArgumentException("file must exist!");
        }
        FileInputStream fis = null;
        DataInputStream dis = null;
        try {
            fis = new FileInputStream(file);
            dis = new DataInputStream(fis);
            int version = dis.readInt();
            if (version == 0) {
                int count = 0;
                while (dis.available() > 0) {
                    Plot plot = null;
                    try {
                        plot = loadPlotV0_0(dis);
                        ++count;
                        log.info("Loaded " + count + " plots");
                        plot.getWorld().addPlot(plot);
                        plots.add(plot);
                    }
                    catch (Throwable thrown) {
                        log.severe("Error occurred while loading plot " + (plot == null ? "" : plot.toString()) + ": " + thrown.getClass().getName());
                        log.log(Level.SEVERE, "Error loading plots!", thrown);
                        while (dis.readChar() != '\n') {
                            
                        }
                    }
                    // \n
                    dis.readChar();
                }
            }
            else {
                log.severe("Error reading from plots file " + file.getName() + " Unsupported format version: " + version);
            }
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Could not load plots file " + file.getName(), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error while loading plots file " + file.getName(), e);
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return plots;
    }
    
    @Override
    public Collection<Settlement> loadSettlements() {
        Collection<Settlement> settlements = new HashSet<Settlement>();
        log.info("Loading Settlements...");
        File settlementsFile = new File(plugin.getSettlementsFolder(), "settlements.dat");
        if (!settlementsFile.exists()) {
            settlementsFile = new File(plugin.getSavedDataFolder(), "settlements.dat");
            try {
                settlementsFile.createNewFile();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not create settlements.dat file!", e);
                e.printStackTrace();
            }
            return settlements;
        }
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new FileInputStream(settlementsFile));
        } catch (FileNotFoundException e) {
            log.severe("Could not open settlements.dat file!");
            return settlements;
        }
        
        int count = 0;
        try {
            if (dis.available() > 0) {
                log.info("Bytes left to read: " + dis.available());
                int version = dis.readInt();
                log.info("Read version: " + version);
                if (version == 0) {
                    while(dis.available() > 0) {
                        log.info("Bytes left to read: " + dis.available());
                        Settlement set = null;
                        try {
                            set = loadSettlementV0_0(dis);
//                            registerSettlement(set);
                            settlements.add(set);
                            ++count;
                            log.info("Loaded settlement " + set.getName());
                        }
                        catch (Throwable thrown) {
                            log.severe("Error occurred while loading settlement " + (set == null ? "" : set.getName()) + ": " + thrown.getClass().getName());
                            log.log(Level.SEVERE, "Error loading settlements!", thrown);
                            while (dis.readChar() != '\n') {
                                
                            }
                        }
                    }
                }
                else {
                    log.severe("Error reading from settlements.dat: Unsupported format version: " + version);
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error while loading Settlements!", e);
            e.printStackTrace();
        }
        finally {
            try {
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return settlements;
    }

    @Override
    public SettlementPlayer loadPlayer(Player player) {
        String name = player.getName();
        SettlementPlayer sPlayer = new SettlementPlayer(player);
        File playerFile = new File(SettlementPlugin.plugin.getPlayersFolder(), name + ".dat");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not create data file for player " + name, e);
                e.printStackTrace();
            }
            return sPlayer;
        }
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new FileInputStream(playerFile));
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Could not find data file for player " + name, e);
            e.printStackTrace();
            return sPlayer;
        }
        try {
            int fileVersion = dis.readInt();
            if (fileVersion == 0) {
                loadPlayerV0_0(sPlayer, dis);
//                return sPlayer;
            }
            else {
                log.severe("Error reading from player file " + name + "Unsupported format version: " + fileVersion);
                player.sendMessage("§4Error occurred loading Settlement info! Please report this to your administrator!");
//                return sPlayer;
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error reading from data file of player " + name, e);
            e.printStackTrace();
        }
        finally {
            try {
                if (dis != null) {
                    dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sPlayer;
    }

    
    static Map<World, Collection<ChunkCoordinate>> reusableWorldPlots;
    
    public static final int SETTLEMENT_FILE_VERSION = 0;
    public static final int PLAYER_FILE_VERSION = 0;
    public static final int PLOT_FILE_VERSION = 0;
    
    public static void saveSettlementV0_0(Settlement set, DataOutputStream dos) throws IOException {
        dos.writeInt(set.getUid());
        dos.writeUTF(set.getName());
        dos.writeUTF(set.getSlogan());
        dos.writeInt(set.getBonusPlots());
        if (set.getSpawn() != null) {
            dos.writeChar('[');
            UUID sWorldUid = set.getSpawn().getWorld().getUID();
            dos.writeLong(sWorldUid.getMostSignificantBits());
            dos.writeLong(sWorldUid.getLeastSignificantBits());
            dos.writeInt(set.getSpawn().getBlockX());
            dos.writeInt(set.getSpawn().getBlockY());
            dos.writeInt(set.getSpawn().getBlockZ());
            dos.writeFloat(set.getSpawn().getYaw());
            dos.writeFloat(set.getSpawn().getPitch());
            dos.writeChar(']');
        }
        else {
            dos.writeChar('|');
        }
        dos.writeUTF(set.getOwnerName());
        if (set.getModeratorNames().size() > 0) {
            dos.writeChar('{');
            for (String s : set.getModeratorNames()) {
                dos.writeUTF(s);
                dos.writeChar(',');
            }
            dos.writeChar('}');
        }
        else {
            dos.writeChar('|');
        }
        if (set.getBaseMemberNames().size() > 0) {
            dos.writeChar('{');
            for (String s : set.getBaseMemberNames()) {
                dos.writeUTF(s);
                dos.writeChar(',');
            }
            dos.writeChar('}');
        }
        else {
            dos.writeChar('|');
        }
        {
            if (set.getPlots().size() > 0) {
                if (reusableWorldPlots == null) {
                    reusableWorldPlots = new HashMap<World, Collection<ChunkCoordinate>>();
                }
                for (Plot p : set.getPlots()) {
                    World w = p.getWorld().getWorld();
                    ChunkCoordinate cc = p.getCoordinates();
                    if (reusableWorldPlots.get(w) == null) {
                        reusableWorldPlots.put(w, new HashSet<ChunkCoordinate>());
                    }
                    reusableWorldPlots.get(w).add(cc);
                }
                dos.writeChar('{');
                for (World wrld : reusableWorldPlots.keySet()) {
                    Collection<ChunkCoordinate> chunks = reusableWorldPlots.get(wrld);
                    if (chunks.size() > 0) {
                        dos.writeChar('{');
                        dos.writeLong(wrld.getUID().getMostSignificantBits());
                        dos.writeLong(wrld.getUID().getLeastSignificantBits());
                        dos.writeChar(':');
                        for (ChunkCoordinate ch : chunks) {
                            dos.writeChar(',');
                            dos.writeInt(ch.x);
                            dos.writeInt(ch.z);
                        }
                        dos.writeChar('}');
                        dos.writeChar(';');
                    }
                    else {
                        dos.writeChar('|');
                    }
                }
                dos.writeChar('}');
            }
            else {
                dos.writeChar('|');
            }
        }
    }
    
    public static Settlement loadSettlementV0_0(DataInputStream dis) throws IOException {
        int uid = dis.readInt();
        String name = dis.readUTF();
        Settlement set = new Settlement(name, uid);
        set.setSlogan(dis.readUTF());
        set.setBonusPlots(dis.readInt());
        
        // Spawn location
        if (dis.readChar() == '[') { // [
            long sUidStart = dis.readLong();
            long sUidEnd = dis.readLong();
            int x = dis.readInt();
            int y = dis.readInt();
            int z = dis.readInt();
            float yaw = dis.readFloat();
            float pitch = dis.readFloat();
            dis.readChar(); // ]
            UUID sWorldUid = new UUID(sUidStart, sUidEnd);
            World world = Bukkit.getWorld(sWorldUid);
            if (world != null) {
                set.setSpawn(new Location(world, x, y, z, yaw, pitch));
            }
        }
        // done spawn
        
        String ownerName = dis.readUTF();
        set.setOwnerName(ownerName);
        
        char c = dis.readChar();
        while (c != '|' && c != '}') {
            set.addModerator(dis.readUTF());
            dis.readChar();
            c = dis.readChar();
        } // }
        c = dis.readChar(); // | or {
        log.info("About to read members, char = " + String.valueOf(c));
        while (c != '|' && c != '}') {
            log.info("Started reading member loop");
            String s = dis.readUTF();
            log.info("Adding member: " + s);
            set.addMember(s);
            c = dis.readChar();
            log.info("Reading members, char = " + String.valueOf(c));
            c = dis.readChar();
            log.info("Reading members, char = " + String.valueOf(c));
        } // }
        c = dis.readChar(); // | or {
        while (c != '|' && c != '}') { // Worlds
            c = dis.readChar(); // { / |
            if (c == '|') {
                continue;
            }
            long uidStart = dis.readLong();
            long uidEnd = dis.readLong();
            UUID worldUid = new UUID(uidStart, uidEnd);
            World currWorld = Bukkit.getWorld(worldUid);
            dis.readChar();
            c = dis.readChar();
            while (c != '}') {
                int cx = dis.readInt();
                int cz = dis.readInt();
                Chunk chk = currWorld.getChunkAt(cx, cz);
                set.addChunk(chk);
                c = dis.readChar();
            }
            dis.readChar();
            c = dis.readChar();
        }
//        dis.readChar(); // \n
        set.updateMembers();
        set.updateClaimablePlots();
        return set;
    }
    
    
    
    
    
    public static void savePlayerV0_0(SettlementPlayer sPlayer, DataOutputStream dos) throws IOException {
        dos.writeInt(PLAYER_FILE_VERSION); // Save format version
        dos.writeLong(System.currentTimeMillis());
        dos.writeChar('{');
        for (SettlementData data : sPlayer.getData()) {
            dos.writeChar(';');
            saveSettlementDataV0_0(data, dos);
        }
        dos.writeChar('}');
    }
    
    public static void saveSettlementDataV0_0(SettlementData data, DataOutputStream dos) throws IOException {
        dos.writeChar('[');
        dos.writeInt(data.getUid());
        dos.writeInt(data.getRank().getPriority());
        dos.writeUTF(data.getTitle());
        dos.writeChar(']');
    }
    
    public static void loadPlayerV0_0(SettlementPlayer sPlayer, DataInputStream dis) throws IOException {
        sPlayer.initialiseLastOnline(dis.readLong());
        char c = dis.readChar();
        if (c == '{') {
            while (dis.readChar() != '}') {
                SettlementData sd = loadDataV0_0(sPlayer, dis);
                if (sd != null) {
                    sPlayer.addData(sd);
                }
            }
        }
    }
    
    public static SettlementData loadDataV0_0(SettlementPlayer sPlayer, DataInputStream dis) throws IOException {
        if (dis.readChar() != '[') {
            return null;
        }
        int uid = dis.readInt();
        int pri = dis.readInt();
        String title = dis.readUTF();
        if (dis.readChar() != ']') {
            return null;
        }
        Settlement set = server.getSettlement(uid);
        if (set == null) {
            return null;
        }
        switch (pri) {
        case 0 :
            return null;
        case 1 :
            return new SettlementData(set, Rank.MEMBER, title);
        case 2 :
            return new SettlementData(set, Rank.MODERATOR, title);
        case 3 :
            return new SettlementData(set, Rank.OWNER, title);
        default :
            log.warning("Player " + sPlayer.getName() + " had an invalid rank in " + set.getName());
            return null;
        }
    }
    
    public static void loadPlayerV0_1(SettlementPlayer sPlayer, DataInputStream dis) throws IOException {
        sPlayer.initialiseLastOnline(dis.readLong());
        char c = dis.readChar();
        if (c == '{') {
            while (dis.readChar() != '}') {
                SettlementData sd = loadDataV0_0(sPlayer, dis);
                if (sd != null) {
                    sPlayer.addData(sd);
                }
            }
        }
    }
    
    public static SettlementData loadDataV0_1(SettlementPlayer sPlayer, DataInputStream dis) throws IOException {
        if (dis.readChar() != '[') {
            return null;
        }
        int uid = dis.readInt();
        int pri = dis.readInt();
        String title = dis.readUTF();
        if (dis.readChar() != ']') {
            return null;
        }
        Settlement set = server.getSettlement(uid);
        if (set == null) {
            return null;
        }
        switch (pri) {
        case 0 :
            return null;
        case 1 :
            return null;
        case 2 :
            return new SettlementData(set, Rank.MEMBER, title);
        case 3 :
            return new SettlementData(set, Rank.MODERATOR, title);
        case 4 :
            return new SettlementData(set, Rank.OWNER, title);
        default :
            log.warning("Player " + sPlayer.getName() + " had an invalid rank in " + set.getName());
            return null;
        }
    }
    
    public static void savePlotV0_0(Plot plot,/* ByteArrayOutputStream bos,*/ DataOutputStream dos) throws IOException {
        ChunkCoordinate cc = plot.getCoordinates();
        UUID uid = plot.getWorld().getWorld().getUID();
//        log.info("Written none: " + bos.size());
        dos.writeLong(uid.getMostSignificantBits());
        dos.writeLong(uid.getLeastSignificantBits());
//        log.info("Written World UID: " + bos.size());
        dos.writeInt(cc.x);
        dos.writeInt(cc.z);
//        log.info("Written coords: " + bos.size());
        dos.writeUTF(StringUtil.padString(plot.getOwnerPlayer().getName(), 16));
//        log.info("Written owner name: " + bos.size());
//        dos.writeUTF(plot.getOwnerPlayer().getName());
        dos.writeInt(plot.getOwnerSettlement().getUid());
//        log.info("Written owner settlement UID: " + bos.size());
//        log.info("SAVING: " + plot);
    }
    
    public static Plot loadPlotV0_0(DataInputStream dis) throws IOException {
        long uid1 = dis.readLong();
        long uid2 = dis.readLong();
        UUID uid = new UUID(uid1, uid2);
        ChunkCoordinate cc = new ChunkCoordinate(dis.readInt(), dis.readInt());
        String ownerName = dis.readUTF().trim();
        int setUid = dis.readInt();
        World world = Bukkit.getWorld(uid);
        SettlementWorld sWorld = server.getWorld(world);
        Settlement settlement;
        if (setUid != Settlement.WILDERNESS.getUid()) {
            settlement = server.getSettlement(setUid);
        }
        else {
            settlement = Settlement.WILDERNESS;
        }
        if (sWorld == null || settlement == null) {
            return null;
        }
        Plot plot = new Plot(sWorld, cc);
        if (!ownerName.equalsIgnoreCase(SettlementPlayer.NONE.getName())) {
            plot.setOwnerPlayer(ownerName);
        }
        plot.setOwnerSettlement(settlement);
        return plot;
    }
}
