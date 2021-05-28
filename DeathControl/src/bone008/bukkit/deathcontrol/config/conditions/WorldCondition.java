package bone008.bukkit.deathcontrol.config.conditions;

import bone008.bukkit.deathcontrol.config.ConditionDescriptor;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import java.util.Arrays;
import java.util.List;

public class WorldCondition extends ConditionDescriptor {
  private String worldName;
  
  public WorldCondition(List<String> args) throws DescriptorFormatException {
    if (args.isEmpty())
      throw new DescriptorFormatException("no world given"); 
    this.worldName = args.get(0);
  }
  
  public boolean matches(DeathContext context) {
    return context.getDeathLocation().getWorld().getName().equalsIgnoreCase(this.worldName);
  }
  
  public List<String> toParameters() {
    return Arrays.asList(new String[] { this.worldName });
  }
}
