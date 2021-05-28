package bone008.bukkit.deathcontrol;

import bone008.bukkit.deathcontrol.config.HandlingDescriptor;
import bone008.bukkit.deathcontrol.util.Message;
import bone008.bukkit.deathcontrol.util.MessageUtil;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitDeathHandler implements Listener {
  @EventHandler(priority = EventPriority.HIGH)
  public void onRespawn(PlayerRespawnEvent event) {
    final Player player = event.getPlayer();
    (new BukkitRunnable() {
        public void run() {
          DeathContextImpl context = DeathControl.instance.getActiveDeath(player.getUniqueId());
          if (context != null)
            if (!DeathControl.instance.config.allowsCrossworld() && !DeathControl.instance.hasPermission((Permissible)player, DeathControl.PERMISSION_CROSSWORLD) && !player.getWorld().equals(context.getDeathLocation().getWorld())) {
              MessageUtil.sendMessage((CommandSender)player, Message.NOTIF_NOCROSSWORLD, new String[0]);
              context.cancel();
            } else {
              context.executeAgents();
            }  
        }
      }).runTask((Plugin)DeathControl.instance);
  }
  
  @EventHandler(priority = EventPriority.HIGH)
  public void onDeath(PlayerDeathEvent event) {
    Player ply = event.getEntity();
    DeathContextImpl lastActiveContext = DeathControl.instance.getActiveDeath(ply.getUniqueId());
    if (lastActiveContext != null)
      lastActiveContext.cancelManually(); 
    if (!DeathControl.instance.hasPermission((Permissible)ply, DeathControl.PERMISSION_USE))
      return; 
    DeathContextImpl context = new DeathContextImpl(event);
    Set<String> deathCauses = new HashSet<>();
    byte b;
    int i;
    DeathCause[] arrayOfDeathCause;
    for (i = (arrayOfDeathCause = DeathCause.values()).length, b = 0; b < i; ) {
      DeathCause dc = arrayOfDeathCause[b];
      if (dc.appliesTo(ply.getLastDamageCause()))
        deathCauses.add(dc.toHumanString()); 
      b++;
    } 
    StringBuilder log1 = new StringBuilder(), log2 = new StringBuilder();
    log1.append(ply.getName()).append(" died (").append(Util.pluralNum(deathCauses.size(), "cause")).append(": ").append(Util.joinCollection(", ", deathCauses)).append(")");
    if (!DeathControl.instance.hasPermission((Permissible)ply, DeathControl.PERMISSION_NOLIMITS) && !DeathControl.instance.config.isWorldAllowed(ply.getWorld().getName())) {
      DeathControl.instance.log(Level.FINE, log1.append("; Not in a valid world!").toString());
      return;
    } 
    if (BukkitRuleNotifHandler.isProblematicRuleEnabled(ply.getWorld())) {
      DeathControl.instance.log(Level.SEVERE, "The vanilla gamerule keepInventory is enabled in world \"" + ply.getWorld().getName() + "\"!");
      DeathControl.instance.log(Level.SEVERE, "You have to disable that rule to make the plugin work properly.");
      DeathControl.instance.log(Level.SEVERE, "Handling of " + ply.getName() + "'s death was cancelled.");
      return;
    } 
    List<String> executed = new ArrayList<>();
    for (HandlingDescriptor handling : DeathControl.instance.config.getHandlings()) {
      if (handling.areConditionsMet(context)) {
        handling.assignAgents(context);
        context.setDisconnectTimeout(handling.getTimeoutOnDisconnect());
        context.setCancelMessage(handling.getCancelMessage());
        executed.add(handling.getName());
        if (handling.isLastHandling())
          break; 
      } 
    } 
    if (!context.hasAgents()) {
      DeathControl.instance.log(Level.FINE, log1.append("; No actions to be executed!").toString());
      DeathControl.instance.log(Level.FINE, String.valueOf(ply.getName()) + " dropped " + event.getDrops());
      return;
    } 
    DeathControl.instance.addActiveDeath(ply.getUniqueId(), context);
    context.preprocessAgents();
    event.getDrops().clear();
    for (StoredItemStack dropped : context.getItemDrops())
      event.getDrops().add(dropped.itemStack); 
    event.setKeepLevel(false);
    event.setNewExp(0);
    event.setNewLevel(0);
    event.setNewTotalExp(0);
    log1.append("; Executed handlings: " + Util.joinCollection(", ", executed));
    log2.append("Handled death:\n");
    log2.append("| Player: ").append(ply.getName()).append('\n');
    log2.append("| Dropped: " + event.getDrops());
    for (String cause : deathCauses)
      log2.append("| Death cause: ").append(cause).append('\n'); 
    log2.append("| Executed handlings: " + Util.joinCollection(", ", executed)).append('\n');
    log2.append("| Disconnect timeout: " + context.getDisconnectTimeout());
    if (DeathControl.instance.config.getLoggingLevel() <= Level.FINE.intValue()) {
      DeathControl.instance.log(Level.FINE, log2.toString().trim());
    } else if (DeathControl.instance.config.getLoggingLevel() <= Level.INFO.intValue()) {
      DeathControl.instance.log(Level.INFO, log1.toString().trim());
    } 
  }
}
