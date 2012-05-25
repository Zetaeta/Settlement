package net.zetaeta.settlement.persistence;

import java.util.Collection;

import net.zetaeta.settlement.object.Plot;
import net.zetaeta.settlement.object.Settlement;
import net.zetaeta.settlement.object.SettlementPlayer;
import net.zetaeta.settlement.object.WorldCoordinate;

import org.bukkit.entity.Player;

public interface PersistenceManager {
    
    public void saveSettlements(Collection<Settlement> settlements);
    
    public void savePlayer(SettlementPlayer player);
    
    public void savePlot(Plot plot);
    
    public Collection<Settlement> loadSettlements();
    
    public SettlementPlayer loadPlayer(Player player);
    
    public Collection<Plot> loadPlots(WorldCoordinate coord);
}
