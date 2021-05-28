package bone008.bukkit.deathcontrol.config;

import bone008.bukkit.deathcontrol.StoredItemStack;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.entity.PlayerDeathEvent;

public interface DeathContext {
  Location getDeathLocation();
  
  OfflinePlayer getVictim();
  
  List<StoredItemStack> getItemDrops();
  
  PlayerDeathEvent getDeathEvent();
  
  String replaceVariables(CharSequence paramCharSequence);
  
  Object getVariable(String paramString);
  
  void setVariable(String paramString, Object paramObject);
  
  boolean continueExecution(ActionResult paramActionResult);
  
  void cancel();
}
