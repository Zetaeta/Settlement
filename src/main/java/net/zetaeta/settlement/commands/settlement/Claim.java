package net.zetaeta.settlement.commands.settlement;

import net.zetaeta.libraries.commands.local.LocalCommand;
import net.zetaeta.settlement.Settlement;
import net.zetaeta.settlement.SettlementPlayer;
import net.zetaeta.settlement.SettlementRank;
import net.zetaeta.settlement.commands.SettlementCommand;
import net.zetaeta.settlement.commands.SettlementCommandsManager;
import net.zetaeta.settlement.commands.SettlementPermission;
import net.zetaeta.settlement.util.SettlementUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Claim extends SettlementCommand {
    
    public Claim(LocalCommand parent) {
        super(parent);
        aliases = new String[] {"claim", "acquire", "reappropriate"};
        usage = new String[] {
                "�2 - /settlement claim [settlement name]",
                "�a  \u00bbClaim a chunk for your currently focused Settlement, or the specified one."
        };
        shortUsage = new String[] {
                "�2 - /settlement claim",
                "�a  \u00bbClaim land for settlement."
        };
        permission = OWNER_PERMISSION + ".claim";
    }
    
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        
        if (!SettlementUtil.checkCommandValid(sender, permission)) {
            return true;
        }
        Player player = (Player) sender;
        SettlementPlayer sPlayer = SettlementPlayer.getSettlementPlayer(player);
        Settlement set = SettlementUtil.getFocusedOrStated(sPlayer, args, true);
        if (set == null) {
            return true;
        }
        if (SettlementUtil.checkPermission(sender, permission, false, true)) {
            set.claimLand(player);
            return true;
        }
        if (sPlayer.getRank(set).isEqualOrSuperiorTo(SettlementRank.MOD)) {
            set.claimLand(player);
            return true;
        }
        else {
            set.sendNoRightsMessage(sender);
            return true;
        }
        
    }
}