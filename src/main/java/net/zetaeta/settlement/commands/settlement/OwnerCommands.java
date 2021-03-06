package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.bukkit.util.commands.CommandArguments;
import net.zetaeta.bukkit.util.commands.local.Command;
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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OwnerCommands implements LocalCommandExecutor, SettlementConstants {
    
    @SuppressWarnings("static-access")
    @Command(aliases = {"create", "new"},
            usage = {"�2 - /settlement create <settlement name>",
                     "�a  \u00bbCreate a settlement with the given name, with you as owner.",},
            shortUsage = {"�2 - /settlement create",
                          "�a  \u00bbCreate a settlement"},
            permission = SettlementCommand.OWNER_PERMISSION + ".create",
            useCommandArguments = true,
            playersOnly = true)
    public boolean create(CommandSender sender, CommandArguments args) {
        String[] rawArgs = args.getUnprocessedArgArray();
        if (rawArgs.length < 1) {
            return false;
        }
        SettlementPlayer sPlayer = server.getSettlementPlayer((Player) sender);
        String setName = StringUtil.arrayAsString(rawArgs);
        if (server.getSettlement(setName) != null) {
            SettlementMessenger.sendSettlementMessage(sender, "�cA settlement with the name �6" + setName + " �calready exists!");
            return true;
        }
        if (setName.length() > 32) {
            SettlementMessenger.sendSettlementMessage(sender, "�cThat name is too long!");
            return true;
        }
        Settlement settlement = new Settlement(sPlayer, StringUtil.arrayAsString(rawArgs), plugin.getSettlementServer().getNewUID());
        server.registerSettlement(settlement);
        sPlayer.setRank(settlement, Rank.OWNER);
        settlement.broadcastSettlementMessage("�a  Settlement Created!");
        return true;
    }
    
    @Command(aliases = {"delete", "disband"},
            usage = {"�2 - /settlement delete [settlement]:",
                      "�a  \u00bbDelete the settlement you specify or have focus over."},
            shortUsage = {"�2 - /settlement delete",
                          "�a  \u00bbDelete a settlement."},
            permission = SettlementCommand.OWNER_PERMISSION + ".delete",
            useCommandArguments = true,
            playersOnly = true)
    public boolean delete(CommandSender sender, CommandArguments args) {
        SettlementPlayer sPlayer = server.getSettlementPlayer((Player) sender);
        Settlement target = SettlementUtil.getFocusedOrStated(sPlayer, args);
        if (target == null) {
            SettlementMessenger.sendInvalidSettlementMessage(sender);
            return true;
        }
        if (sPlayer.getRank(target).isEqualOrSuperiorTo(Rank.OWNER) || PermissionUtil.checkPermission(sender, SettlementCommand.ADMIN_OWNER_PERMISSION + ".delete", false, true)) {
            getDeletionConfirmation(sPlayer, target);
            SettlementMessenger.sendSettlementMessage(sender, new String[] {
                    "�4  Are you sure you want to do this?",
                    "�c  This will delete the Settlement " + target.getName() +" and all its plot/player",
                    "�c  information!",
                    "�a  If you are sure, use �2/settlement confirm �ato confirm the",
                    "�a  deletion"
            });
            return true;
        }
        else {
            target.sendNoRightsMessage(sender);
            return true;
        }
    }

    private static void getDeletionConfirmation(final SettlementPlayer sPlayer, final Settlement settlement) {
        sPlayer.setConfirmable(new Runnable() {
            @SuppressWarnings("static-access")
            @Override
            public void run() {
                String sName = settlement.getName();
                settlement.delete();
                SettlementMessenger.sendSettlementMessage(sPlayer.getPlayer(), StringUtil.concatString("�2Settlement ", sName, " has been deleted!"));
            }
        }, 400);
    }
}
