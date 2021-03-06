package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.bukkit.util.commands.CommandArguments;
import net.zetaeta.bukkit.util.commands.local.LocalCommand;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementPlayer;
import net.zetaeta.settlement.util.SettlementMessenger;
import net.zetaeta.settlement.util.SettlementUtil;
import net.zetaeta.util.PermissionUtil;
import net.zetaeta.util.StringUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Info extends SettlementCommand {

    {
        permission = BASIC_PERMISSION + ".info";
        usage = new String[] {
                "�2 - /settlement [info] [settlement name]:",
                "�a  \u00bbGet information about the settlement you have focus on,",
                "�a  or the global Settlement plugin info."
        };
        shortUsage = new String[] {
                "�2 - /settlement info",
                "�a  \u00bbGet info on a specific Settlement"
        };
        aliases = new String[] {"info"};
    }
    
    public Info(LocalCommand parent) {
        super(parent);
    }
    
    @SuppressWarnings("static-access")
    public boolean execute(CommandSender sender, String alias, String[] args) {
        CommandArguments cArgs = CommandArguments.processArguments(alias, args, new String[0], new String[] {"settlement"});
        if (alias.equalsIgnoreCase("info")) {
            if (args.length < 1) {
                SettlementMessenger.sendGlobalSettlementInfo(sender);
                return true;
            }
            Settlement settlement = SettlementUtil.getStated(cArgs);
            if (settlement == null) {
                SettlementMessenger.sendGlobalSettlementInfo(sender);
                return true;
            }
            if (!PermissionUtil.checkPermission(sender, permission, true, true)) {
                return true;
            }
            settlement.sendInfoMessage(sender);
            return true;
        }
        if (args.length == 0) {
            if (sender instanceof Player) {
                SettlementPlayer sPlayer = server.getSettlementPlayer((Player) sender);
                System.out.println(sPlayer.getName());
                if (sPlayer.getFocus() == null) {
                    SettlementMessenger.sendGlobalSettlementInfo(sender);
                    return true;
                }
                if (!PermissionUtil.checkPermission(sender, permission, true, true)) {
                    return true;
                }
                sPlayer.getFocus().sendInfoMessage(sender);
                return true;
            }
            SettlementMessenger.sendGlobalSettlementInfo(sender);
        }
        Settlement settlement = server.getSettlement(StringUtil.arrayAsString(args));
        if (settlement == null) {
            SettlementMessenger.sendGlobalSettlementInfo(sender);
            return true;
        }
        if (!PermissionUtil.checkPermission(sender, permission, true, true)) {
            return true;
        }
        settlement.sendInfoMessage(sender);
        return true;
    }
    
}
