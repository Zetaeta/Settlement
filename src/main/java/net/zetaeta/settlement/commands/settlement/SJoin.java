package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.CommandArguments.UnclosedStringException;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementPermission;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SJoin extends SettlementCommand {
    
    {
        usage = new String[] {
                "�2 - /settlement join <settlement name>",
                "�a  Join the specified settlement if you have a pending invite."
        };
        permission = new SettlementPermission("join", SettlementPermission.USE_BASIC_PERMISSION);
        aliases = new String[] {"join", "enter"};
    }
    
    public SJoin(LocalCommandExecutor parent) {
        super(parent);
    }
    
    @SuppressWarnings("static-access")
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        CommandArguments parsedArgs = CommandArguments.processArguments(args, new String[] {"silent"}, new String[0]);
        if (parsedArgs == null) {
            SettlementMessenger.sendSettlementMessage(sender, "�c  Your command could not be parsed!");
            return true;
        }
        if (!SettlementUtil.checkCommandValid(sender, permission)) {
            return true;
        }
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        Settlement target = SettlementUtil.getFocusedOrStated(sPlayer, args, true);
        if (SettlementUtil.checkPermissionSilent(sender, permission.getAdminPermission())) {
            target.addMember(sPlayer);
            if (parsedArgs.hasBooleanFlag("silent") || parsedArgs.hasBooleanFlag("s")) {
                SettlementMessenger.sendSettlementMessage(sender, SettlementUtil.concatString(42 + 16, "�a  You joined the Settlement �6", target.getName(), " �asilently"));
                return true;
            }
            target.broadcastSettlementMessage(SettlementUtil.concatString(33 + 16, "�6  ", sPlayer.getName(), " �ahas joined the Settlement!"));
            return true;
        }
        if (target.isInvited(sPlayer)) {
            target.addMember(sPlayer);
            target.broadcastSettlementMessage(SettlementUtil.concatString(33 + 16, "�6  ", sPlayer.getName(), " �ahas joined the Settlement!"));
            return true;
        }
        else {
            SettlementMessenger.sendSettlementMessage(sender, "�c  You have not been invited to the Settlement " + target.getName());
            target.broadcastSettlementMessage(SettlementUtil.concatString(0, "�6  ", sPlayer.getName(), " �atried to join the Settlement!"));
            return true;
        }
    }
}
