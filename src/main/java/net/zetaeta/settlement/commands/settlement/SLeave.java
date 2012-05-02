package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.CommandArguments;
import net.zetaeta.libraries.commands.local.LocalCommandExecutor;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementPermission;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SLeave extends SettlementCommand {
    
    {
        usage = new String[] {
                "�2 - /settlement leave [settlement name]",
                "�a  Leave the specified settlement, or your currently focused one if you have one."
        };
        aliases = new String[] {"leave", "quit", "exit"};
        permission = new SettlementPermission("leave", SettlementPermission.USE_BASIC_PERMISSION);
    }
    
    public SLeave(LocalCommandExecutor parent) {
        super(parent);
    }
    
    @SuppressWarnings("static-access")
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!SettlementUtil.checkCommandValid(sender, permission)) {
            return true;
        }
        CommandArguments arguments = CommandArguments.processArguments(args, new String[] {"silent", "s"}, new String[0], sender);
        if (arguments == null)
            return true;
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer((Player) sender);
        Settlement from = SettlementUtil.getFocusedOrStated(sPlayer, arguments.getUnprocessedArgArray(), true);
        if (from == null) {
            return true;
        }
        if (SettlementUtil.checkPermissionSilent(sender, permission.getAdminPermission())) {
            if (arguments.hasBooleanFlag("silent") || arguments.hasBooleanFlag("s")) {
                from.removeMember(sPlayer);
                SettlementMessenger.sendSettlementMessage(sender, SettlementUtil.concatString(40 + 16, "  �a  You left the Settlement �6", from.getName(), " �asilently"));
                return true;
            }
        }
        if (from.isMember(sPlayer)) {
            from.removeMember(sPlayer);
            SettlementMessenger.sendSettlementMessage(sender, SettlementUtil.concatString(40 + 16, "  �a  You left the Settlement �6", from.getName(), " �a!"));
            from.broadcastSettlementMessage(SettlementUtil.concatString(0, "  �b", sPlayer.getName(), " �aleft the Settlement!"));
            return true;
        }
        SettlementMessenger.sendSettlementMessage(sender, "�c  You are not in that Settlement!");
        return true;
    }
}
