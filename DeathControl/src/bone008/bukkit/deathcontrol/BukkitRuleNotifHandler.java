package bone008.bukkit.deathcontrol;

import bone008.bukkit.deathcontrol.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitRuleNotifHandler implements Listener {
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (player.isOp() || DeathControl.instance.hasPermission((Permissible)player, DeathControl.PERMISSION_ADMIN))
      for (World w : Bukkit.getWorlds()) {
        if (isProblematicRuleEnabled(w))
          warn((CommandSender)player, w.getName()); 
      }  
  }
  
  public static void warnAll() {
    (new BukkitRunnable() {
        public void run() {
          for (World w : Bukkit.getWorlds()) {
            if (BukkitRuleNotifHandler.isProblematicRuleEnabled(w)) {
              BukkitRuleNotifHandler.warn((CommandSender)Bukkit.getConsoleSender(), w.getName());
              for (Player ply : Bukkit.getOnlinePlayers()) {
                if (ply.isOp() || DeathControl.instance.hasPermission((Permissible)ply, DeathControl.PERMISSION_ADMIN))
                  BukkitRuleNotifHandler.warn((CommandSender)ply, w.getName()); 
              } 
            } 
          } 
        }
      }).runTask((Plugin)DeathControl.instance);
  }
  
  private static void warn(CommandSender who, String worldName) {
    String prefix = String.valueOf(ChatColor.BOLD.toString()) + ChatColor.RED + "> " + ChatColor.RESET;
    MessageUtil.sendMessage(who, "====== WARNING ======", true);
    MessageUtil.sendMessage(who, "The gamerule \"keepInventory\" is enabled in world \"" + worldName + "\"!", prefix);
    MessageUtil.sendMessage(who, "This breaks DeathControl by overwriting its functionality.", prefix);
    MessageUtil.sendMessage(who, "Please disable the rule with the following command:\n    " + ChatColor.AQUA + "/gamerule keepInventory false", prefix);
    MessageUtil.sendMessage(who, "=====================", true);
  }
  
  public static boolean isProblematicRuleEnabled(World world) {
    return Boolean.parseBoolean(world.getGameRuleValue("keepInventory"));
  }
}
