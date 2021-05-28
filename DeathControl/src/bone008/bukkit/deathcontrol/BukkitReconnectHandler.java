package bone008.bukkit.deathcontrol;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitReconnectHandler implements Listener {
  private Map<UUID, QuitHandlerTask> logoffExpireTimers = new HashMap<>();
  
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    DeathContextImpl context = DeathControl.instance.getActiveDeath(player.getUniqueId());
    if (context != null) {
      QuitHandlerTask task = new QuitHandlerTask(context, player.getUniqueId());
      int t = context.getDisconnectTimeout();
      if (t > 0) {
        task.runTaskLater((Plugin)DeathControl.instance, 20L * t);
        this.logoffExpireTimers.put(player.getUniqueId(), task);
        DeathControl.instance.log(Level.INFO, String.valueOf(player.getName()) + " left the game. Cancelling in " + t + " seconds ...");
      } else if (t == 0) {
        task.run();
        DeathControl.instance.log(Level.INFO, String.valueOf(player.getName()) + " left the game. Cancelling now ...");
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (this.logoffExpireTimers.containsKey(player.getUniqueId())) {
      ((QuitHandlerTask)this.logoffExpireTimers.remove(player.getUniqueId())).cancel();
      if (DeathControl.instance.getActiveDeath(player.getUniqueId()) != null)
        DeathControl.instance.log(Level.FINE, String.valueOf(player.getName()) + " rejoined. Expiration timer stopped."); 
    } 
  }
  
  private class QuitHandlerTask extends BukkitRunnable {
    private final DeathContextImpl context;
    
    private final UUID victimUniqueId;
    
    public QuitHandlerTask(DeathContextImpl context, UUID victimUniqueId) {
      this.context = context;
      this.victimUniqueId = victimUniqueId;
    }
    
    public void run() {
      if (!this.context.isCancelled()) {
        this.context.cancel();
        DeathControl.instance.log(Level.INFO, "Death handling for disconnected player " + this.context.getVictim().getName() + " was cancelled!");
      } 
      BukkitReconnectHandler.this.logoffExpireTimers.remove(this.victimUniqueId);
    }
  }
}
