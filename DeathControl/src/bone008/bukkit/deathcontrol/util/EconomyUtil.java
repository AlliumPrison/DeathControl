package bone008.bukkit.deathcontrol.util;

import bone008.bukkit.deathcontrol.DeathControl;
import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class EconomyUtil {
  private static Economy vaultEconomy = null;
  
  public static void init() {
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)DeathControl.instance, new Runnable() {
          public void run() {
            try {
              EconomyUtil.setupVault();
            } catch (NoClassDefFoundError noClassDefFoundError) {}
          }
        });
  }
  
  private static void setupVault() {
    RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
    if (economyProvider != null)
      vaultEconomy = (Economy)economyProvider.getProvider(); 
  }
  
  public static double calcCost(OfflinePlayer player, double percentage) {
    double currBalance;
    if (player == null)
      throw new IllegalArgumentException("null argument"); 
    if (percentage < 0.0D || percentage > 1.0D)
      throw new IllegalArgumentException("percentage out of range"); 
    if (vaultEconomy != null) {
      currBalance = vaultEconomy.getBalance(player);
    } else {
      logNotice(player);
      return 0.0D;
    } 
    return currBalance * percentage;
  }
  
  public static boolean canAfford(OfflinePlayer player, double cost) {
    if (player == null)
      throw new IllegalArgumentException("null argument"); 
    if (cost <= 0.0D)
      return true; 
    if (vaultEconomy != null)
      return vaultEconomy.has(player, cost); 
    logNotice(player);
    return true;
  }
  
  public static boolean payCost(OfflinePlayer player, double cost) {
    if (player == null)
      throw new IllegalArgumentException("null argument"); 
    if (cost <= 0.0D)
      return true; 
    if (vaultEconomy != null)
      return vaultEconomy.withdrawPlayer(player, cost).transactionSuccess(); 
    logNotice(player);
    return true;
  }
  
  public static String formatMoney(double cost) {
    if (vaultEconomy != null)
      return vaultEconomy.format(cost); 
    return Double.toString(cost);
  }
  
  private static void logNotice(OfflinePlayer player) {
    DeathControl.instance.log(Level.WARNING, "Couldn't calculate money for " + player.getName() + " because no economy management plugin was found!");
  }
}
