package net.zetaeta.settlement.commands.settlement;

import java.util.List;

import net.zetaeta.bukkit.util.commands.CommandArguments;
import net.zetaeta.bukkit.util.commands.local.Command;
import net.zetaeta.bukkit.util.commands.local.LocalCommand;
import net.zetaeta.bukkit.util.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.Rank;
import net.zetaeta.settlement.SettlementConstants;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementPlayer;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;
import net.zetaeta.util.PermissionUtil;
import net.zetaeta.util.StringUtil;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Set extends SettlementCommand implements LocalCommandExecutor, SettlementConstants {
    public static final String SET_PERMISSION = OWNER_PERMISSION +  ".set";
    public static final String SET_ADMIN_PERMISSION = ADMIN_OWNER_PERMISSION + ".set";
    
    public Set(LocalCommand parent) {
        super(parent);
        List<LocalCommand> registered = registerSubCommands(this);
        usage = new String[] {
                "�2 - /settlement set name (-name <slogan>) ([-settlement <settlement name>])",
                "�2 - /settlement set slogan (-slogan <slogan>) ([-settlement <settlement name>])"
        };
        shortUsage = new String[] {
                "�2 - /settlement set",
                "�a  \u00bbChange proprties of the Settlement."
        };
        aliases = new String[] {"set", "change"};
        permission = SET_PERMISSION;
    }
    
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (trySubCommand(sender, alias, args)) {
            return true;
        }
        return false;
    }
    
    @SuppressWarnings("static-access")
    @Command(aliases = {"slogan", "ham"}, 
            usage = {"�2 - /settlement set slogan (-slogan <slogan>) ([-settlement <settlement name>])", 
                     "�a  \u00bbSets the slogan of <settlement name> (or you current focus if a Settlement is not specified)"},
            shortUsage = {"�2 - /settlement set slogan",
                          "�a  \u00bbChange the settlement's slogan."  },
            permission = SET_PERMISSION + ".slogan",
            useCommandArguments = true,
            valueFlags = {"settlement", "slogan"})
    public boolean setSlogan(CommandSender sender, CommandArguments args) {
        if (sender instanceof Player && !args.hasFlagValue("settlement")) { // If a settlement is not specified
            if (args.getUnprocessedArgs().size() == 0) {
                return false;
            }
            SettlementPlayer sPlayer = server.getSettlementPlayer((Player) sender);
            if (sPlayer.getFocus() != null) {
                Settlement target = sPlayer.getFocus();
                if (sPlayer.getRank(target).isEqualOrSuperiorTo(Rank.MODERATOR) || PermissionUtil.checkPermission(sender, ADMIN_OWNER_PERMISSION + ".set.slogan")) {
                    String slogan = StringUtil.arrayAsString(args.getUnprocessedArgArray());
                    target.setSlogan(slogan);
                    target.broadcastSettlementMessage("�b  " + sender.getName() + " �achanged the Settlement's slogan to " + slogan + "!");
                    return true;
                }
                else {
                    target.sendNoRightsMessage(sender);
                }
            }
            else {
                return false;
            }
        }
        String settlementName = null, slogan;
        if (args.hasFlagValue("settlement")) {
            settlementName = args.getFlagValue("settlement");
            if (args.hasFlagValue("slogan")) {
                slogan = args.getFlagValue("slogan");
            }
            else {
                slogan = StringUtil.arrayAsString(args.getUnprocessedArgArray());
            }
        }
        else if (args.hasFlagValue("slogan")) {
            slogan = args.getFlagValue("slogan");
            settlementName = StringUtil.arrayAsString(args.getUnprocessedArgArray());
        }
        else {
            return false;
        }
        Settlement target = server.getSettlement(settlementName);
        if (target == null) {
            sender.sendMessage("�cThere is no Settlement of that name!");
            return true;
        }
        if (sender.hasPermission(ADMIN_OWNER_PERMISSION + ".set.slogan") || server.getSettlementPlayer((Player) sender).getRank(target).isEqualOrSuperiorTo(Rank.MODERATOR)) {
            target.setSlogan(slogan);
            target.broadcastSettlementMessage("�b  " + sender.getName() + " �achanged the Settlement's slogan to " + slogan + "!");
            return true;
        }
        else {
            target.sendNoRightsMessage(sender);
            return true;
        }
    }
    
    @SuppressWarnings("static-access")
    @Command(aliases = {"name", "tag"}, 
            usage = {"�2 - /settlement set name (-name <slogan>) ([-settlement <settlement name>]) ", 
                     "�a  \u00bbSets the name of <settlement name> (or you current focus if a Settlement is not specified)"},
            shortUsage = {"�2 - /settlement set name",
                          "�a  \u00bbChange the settlement's name."},
            permission = SET_PERMISSION + ".name",
            useCommandArguments = true,
            valueFlags = {"settlement", "name"})
    public boolean setName(CommandSender sender, CommandArguments args) {
        if (!SettlementUtil.checkCommandValid(sender, SET_PERMISSION + ".name")) {
            return true;
        }
        SettlementPlayer sPlayer = server.getSettlementPlayer((Player) sender);
        Settlement settlement = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (settlement == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        if (args.hasFlagValue("name")) {
            if (sPlayer.getRank(settlement).isEqualOrSuperiorTo(Rank.MODERATOR)) {
                settlement.changeName(args.getFlagValue("name"), sPlayer);
                return true;
            }
            else {
                settlement.sendNoRightsMessage(sender);
                return true;
            }
        }
        else if (!settlement.getName().equalsIgnoreCase(StringUtil.arrayAsString(args.getUnprocessedArgArray()))) {
            if (sPlayer.getRank(settlement).isEqualOrSuperiorTo(Rank.MODERATOR)) {
                settlement.changeName(StringUtil.arrayAsString(args.getUnprocessedArgArray()), sPlayer);
                return true;
            }
            else {
                settlement.sendNoRightsMessage(sender);
                return true;
            }
        }
        else {
            return false;
        }
    }
    
    @SuppressWarnings("static-access")
    @Command(usage = {"�2 - /settlement set spawn [settlement]:",
                      "�a  \u00bbSet the settlement's spawn to your current location"},
            shortUsage = {"�2 - /settlement set spawn:",
                    "�a  \u00bbSet a settlement's spawn."},
            aliases = {"spawn", "home"},
            valueFlags = {"settlement"},
            permission = SET_PERMISSION + ".spawn",
            playersOnly = true,
            checkPermissions = true
    )
    public boolean setSpawn(CommandSender sender, CommandArguments args) {
        Player player = (Player) sender;
        SettlementPlayer sPlayer = server.getSettlementPlayer(player);
        Settlement settlement = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (settlement == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        if (PermissionUtil.checkPermission(sender, SET_ADMIN_PERMISSION + ".spawn", false, true) || sPlayer.getRank(settlement).isEqualOrSuperiorTo(Rank.MODERATOR)) {
            if (!settlement.equals(server.getOwnerSettlement(player.getLocation().getChunk()))) {
                SettlementMessenger.sendSettlementMessage(sender, StringUtil.concatString(80, "�c  The settlement �6", settlement.getName(), " �cdoes not own this plot!"));
                return true;
            }
            Location loc = player.getLocation();
            settlement.setSpawn(loc);
            SettlementMessenger.sendSettlementMessage(sender, StringUtil.concatString(125, "�b  You �aset the spawn of �6", settlement.getName(), "�a to x=", loc.getBlockX(), ", y=", loc.getBlockY(), ", z=", loc.getBlockZ(), " in world ", loc.getWorld().getName(), "!"));
            return true;
        }
        settlement.sendNoRightsMessage(sender);
        return true;
    }
}
