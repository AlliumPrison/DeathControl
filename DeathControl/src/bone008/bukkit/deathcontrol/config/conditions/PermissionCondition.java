package bone008.bukkit.deathcontrol.config.conditions;

import bone008.bukkit.deathcontrol.config.ConditionDescriptor;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PermissionCondition extends ConditionDescriptor {
  private boolean isKiller;
  
  private String permNode;
  
  public PermissionCondition(List<String> args) throws DescriptorFormatException {
    if (args.size() < 2)
      throw new DescriptorFormatException("not enough arguments"); 
    if (args.size() > 2)
      throw new DescriptorFormatException("too many arguments"); 
    if (((String)args.get(0)).equalsIgnoreCase("victim")) {
      this.isKiller = false;
    } else if (((String)args.get(0)).equalsIgnoreCase("killer")) {
      this.isKiller = true;
    } else {
      throw new DescriptorFormatException("invalid subject: only \"victim\" or \"killer\" is allowed!");
    } 
    this.permNode = args.get(1);
  }
  
  public boolean matches(DeathContext context) {
    if (this.isKiller) {
      Player killer = Util.getPlayerAttackerFromEvent(context.getVictim().getPlayer().getLastDamageCause());
      return (killer != null && killer.hasPermission(this.permNode));
    } 
    return context.getVictim().getPlayer().hasPermission(this.permNode);
  }
  
  public List<String> toParameters() {
    return Arrays.asList(new String[] { ChatColor.ITALIC + (this.isKiller ? "killer" : "victim") + ChatColor.RESET, this.permNode });
  }
}
