package net.zetaeta.settlement;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import net.zetaeta.settlement.object.ChunkCoordinate;
import net.zetaeta.settlement.object.Plot;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementData;
import net.zetaeta.settlement.object.SettlementPlayer;
import net.zetaeta.settlement.object.SettlementWorld;
import net.zetaeta.util.StringUtil;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public final class FlatFileIO implements SettlementConstants {
//    static Map<World, Collection<ChunkCoordinate>> reusableWorldPlots;
//    
//    public static final int SETTLEMENT_FILE_VERSION = 0;
//    public static final int PLAYER_FILE_VERSION = 0;
//    public static final int PLOT_FILE_VERSION = 0;
//    
//    public static void saveSettlementV0_0(Settlement set, DataOutputStream dos) throws IOException {
//        dos.writeInt(set.getUid());
//        dos.writeUTF(set.getName());
//        dos.writeUTF(set.getSlogan());
//        dos.writeInt(set.getBonusPlots());
//        if (set.getSpawn() != null) {
//            dos.writeChar('[');
//            UUID sWorldUid = set.getSpawn().getWorld().getUID();
//            dos.writeLong(sWorldUid.getMostSignificantBits());
//            dos.writeLong(sWorldUid.getLeastSignificantBits());
//            dos.writeInt(set.getSpawn().getBlockX());
//            dos.writeInt(set.getSpawn().getBlockY());
//            dos.writeInt(set.getSpawn().getBlockZ());
//            dos.writeFloat(set.getSpawn().getYaw());
//            dos.writeFloat(set.getSpawn().getPitch());
//            dos.writeChar(']');
//        }
//        else {
//            dos.writeChar('|');
//        }
//        dos.writeUTF(set.getOwnerName());
//        if (set.getModeratorNames().size() > 0) {
//            dos.writeChar('{');
//            for (String s : set.getModeratorNames()) {
//                dos.writeUTF(s);
//                dos.writeChar(',');
//            }
//            dos.writeChar('}');
//        }
//        else {
//            dos.writeChar('|');
//        }
//        if (set.getBaseMemberNames().size() > 0) {
//            dos.writeChar('{');
//            for (String s : set.getBaseMemberNames()) {
//                dos.writeUTF(s);
//                dos.writeChar(',');
//            }
//            dos.writeChar('}');
//        }
//        else {
//            dos.writeChar('|');
//        }
//        {
//            if (set.getPlots().size() > 0) {
//                if (reusableWorldPlots == null) {
//                    reusableWorldPlots = new HashMap<World, Collection<ChunkCoordinate>>();
//                }
//                for (Plot p : set.getPlots()) {
//                    World w = p.getWorld().getWorld();
//                    ChunkCoordinate cc = p.getCoordinates();
//                    if (reusableWorldPlots.get(w) == null) {
//                        reusableWorldPlots.put(w, new HashSet<ChunkCoordinate>());
//                    }
//                    reusableWorldPlots.get(w).add(cc);
//                }
//                dos.writeChar('{');
//                for (World wrld : reusableWorldPlots.keySet()) {
//                    Collection<ChunkCoordinate> chunks = reusableWorldPlots.get(wrld);
//                    if (chunks.size() > 0) {
//                        dos.writeChar('{');
//                        dos.writeLong(wrld.getUID().getMostSignificantBits());
//                        dos.writeLong(wrld.getUID().getLeastSignificantBits());
//                        dos.writeChar(':');
//                        for (ChunkCoordinate ch : chunks) {
//                            dos.writeChar(',');
//                            dos.writeInt(ch.x);
//                            dos.writeInt(ch.z);
//                        }
//                        dos.writeChar('}');
//                        dos.writeChar(';');
//                    }
//                    else {
//                        dos.writeChar('|');
//                    }
//                }
//                dos.writeChar('}');
//            }
//            else {
//                dos.writeChar('|');
//            }
//        }
//    }
//    
//    public static Settlement loadSettlementV0_0(DataInputStream dis) throws IOException {
//        int uid = dis.readInt();
//        String name = dis.readUTF();
//        Settlement set = new Settlement(name, uid);
//        set.setSlogan(dis.readUTF());
//        set.setBonusPlots(dis.readInt());
//        
//        // Spawn location
//        if (dis.readChar() == '[') { // [
//            long sUidStart = dis.readLong();
//            long sUidEnd = dis.readLong();
//            int x = dis.readInt();
//            int y = dis.readInt();
//            int z = dis.readInt();
//            float yaw = dis.readFloat();
//            float pitch = dis.readFloat();
//            dis.readChar(); // ]
//            UUID sWorldUid = new UUID(sUidStart, sUidEnd);
//            World world = Bukkit.getWorld(sWorldUid);
//            if (world != null) {
//                set.setSpawn(new Location(world, x, y, z, yaw, pitch));
//            }
//        }
//        // done spawn
//        
//        String ownerName = dis.readUTF();
//        set.setOwnerName(ownerName);
//        
//        char c = dis.readChar();
//        while (c != '|' && c != '}') {
//            set.addModerator(dis.readUTF());
//            dis.readChar();
//            c = dis.readChar();
//        } // }
//        c = dis.readChar(); // | or {
//        log.info("About to read members, char = " + String.valueOf(c));
//        while (c != '|' && c != '}') {
//            log.info("Started reading member loop");
//            String s = dis.readUTF();
//            log.info("Adding member: " + s);
//            set.addMember(s);
//            c = dis.readChar();
//            log.info("Reading members, char = " + String.valueOf(c));
//            c = dis.readChar();
//            log.info("Reading members, char = " + String.valueOf(c));
//        } // }
//        c = dis.readChar(); // | or {
//        while (c != '|' && c != '}') { // Worlds
//            c = dis.readChar(); // { / |
//            if (c == '|') {
//                continue;
//            }
//            long uidStart = dis.readLong();
//            long uidEnd = dis.readLong();
//            UUID worldUid = new UUID(uidStart, uidEnd);
//            World currWorld = Bukkit.getWorld(worldUid);
//            dis.readChar();
//            c = dis.readChar();
//            while (c != '}') {
//                int cx = dis.readInt();
//                int cz = dis.readInt();
//                Chunk chk = currWorld.getChunkAt(cx, cz);
//                set.addChunk(chk);
//                c = dis.readChar();
//            }
//            dis.readChar();
//            c = dis.readChar();
//        }
////        dis.readChar(); // \n
//        set.updateMembers();
//        set.updateClaimablePlots();
//        return set;
//    }
//    
//    
//    
//    
//    
//    public static void savePlayerV0_0(SettlementPlayer sPlayer, DataOutputStream dos) throws IOException {
//        dos.writeInt(PLAYER_FILE_VERSION); // Save format version
//        dos.writeLong(System.currentTimeMillis());
//        dos.writeChar('{');
//        for (SettlementData data : sPlayer.getData()) {
//            dos.writeChar(';');
//            saveSettlementDataV0_0(data, dos);
//        }
//        dos.writeChar('}');
//    }
//    
//    public static void saveSettlementDataV0_0(SettlementData data, DataOutputStream dos) throws IOException {
//        dos.writeChar('[');
//        dos.writeInt(data.getUid());
//        dos.writeInt(data.getRank().getPriority());
//        dos.writeUTF(data.getTitle());
//        dos.writeChar(']');
//    }
//    
//    public static void loadPlayerV0_0(SettlementPlayer sPlayer, DataInputStream dis) throws IOException {
//        sPlayer.initialiseLastOnline(dis.readLong());
//        char c = dis.readChar();
//        if (c == '{') {
//            while (dis.readChar() != '}') {
//                SettlementData sd = loadDataV0_0(sPlayer, dis);
//                if (sd != null) {
//                    sPlayer.addData(sd);
//                }
//            }
//        }
//    }
//    
//    public static SettlementData loadDataV0_0(SettlementPlayer sPlayer, DataInputStream dis) throws IOException {
//        if (dis.readChar() != '[') {
//            return null;
//        }
//        int uid = dis.readInt();
//        int pri = dis.readInt();
//        String title = dis.readUTF();
//        if (dis.readChar() != ']') {
//            return null;
//        }
//        Settlement set = server.getSettlement(uid);
//        if (set == null) {
//            return null;
//        }
//        switch (pri) {
//        case 0 :
//            return null;
//        case 1 :
//            return new SettlementData(set, Rank.MEMBER, title);
//        case 2 :
//            return new SettlementData(set, Rank.MODERATOR, title);
//        case 3 :
//            return new SettlementData(set, Rank.OWNER, title);
//        default :
//            log.warning("Player " + sPlayer.getName() + " had an invalid rank in " + set.getName());
//            return null;
//        }
//    }
//    
//    public static void loadPlayerV0_1(SettlementPlayer sPlayer, DataInputStream dis) throws IOException {
//        sPlayer.initialiseLastOnline(dis.readLong());
//        char c = dis.readChar();
//        if (c == '{') {
//            while (dis.readChar() != '}') {
//                SettlementData sd = loadDataV0_0(sPlayer, dis);
//                if (sd != null) {
//                    sPlayer.addData(sd);
//                }
//            }
//        }
//    }
//    
//    public static SettlementData loadDataV0_1(SettlementPlayer sPlayer, DataInputStream dis) throws IOException {
//        if (dis.readChar() != '[') {
//            return null;
//        }
//        int uid = dis.readInt();
//        int pri = dis.readInt();
//        String title = dis.readUTF();
//        if (dis.readChar() != ']') {
//            return null;
//        }
//        Settlement set = server.getSettlement(uid);
//        if (set == null) {
//            return null;
//        }
//        switch (pri) {
//        case 0 :
//            return null;
//        case 1 :
//            return null;
//        case 2 :
//            return new SettlementData(set, Rank.MEMBER, title);
//        case 3 :
//            return new SettlementData(set, Rank.MODERATOR, title);
//        case 4 :
//            return new SettlementData(set, Rank.OWNER, title);
//        default :
//            log.warning("Player " + sPlayer.getName() + " had an invalid rank in " + set.getName());
//            return null;
//        }
//    }
//    
//    public static void savePlotV0_0(Plot plot,/* ByteArrayOutputStream bos,*/ DataOutputStream dos) throws IOException {
//        ChunkCoordinate cc = plot.getCoordinates();
//        UUID uid = plot.getWorld().getWorld().getUID();
////        log.info("Written none: " + bos.size());
//        dos.writeLong(uid.getMostSignificantBits());
//        dos.writeLong(uid.getLeastSignificantBits());
////        log.info("Written World UID: " + bos.size());
//        dos.writeInt(cc.x);
//        dos.writeInt(cc.z);
////        log.info("Written coords: " + bos.size());
//        dos.writeUTF(StringUtil.padString(plot.getOwnerPlayer().getName(), 16));
////        log.info("Written owner name: " + bos.size());
////        dos.writeUTF(plot.getOwnerPlayer().getName());
//        dos.writeInt(plot.getOwnerSettlement().getUid());
////        log.info("Written owner settlement UID: " + bos.size());
////        log.info("SAVING: " + plot);
//    }
//    
//    public static Plot loadPlotV0_0(DataInputStream dis) throws IOException {
//        long uid1 = dis.readLong();
//        long uid2 = dis.readLong();
//        UUID uid = new UUID(uid1, uid2);
//        ChunkCoordinate cc = new ChunkCoordinate(dis.readInt(), dis.readInt());
//        String ownerName = dis.readUTF().trim();
//        int setUid = dis.readInt();
//        World world = Bukkit.getWorld(uid);
//        SettlementWorld sWorld = server.getWorld(world);
//        Settlement settlement;
//        if (setUid != Settlement.WILDERNESS.getUid()) {
//            settlement = server.getSettlement(setUid);
//        }
//        else {
//            settlement = Settlement.WILDERNESS;
//        }
//        if (sWorld == null || settlement == null) {
//            return null;
//        }
//        Plot plot = new Plot(sWorld, cc);
//        if (!ownerName.equalsIgnoreCase(SettlementPlayer.NONE.getName())) {
//            plot.setOwnerPlayer(ownerName);
//        }
//        plot.setOwnerSettlement(settlement);
//        return plot;
//    }
}
