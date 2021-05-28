package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionDescriptor;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import bone008.bukkit.deathcontrol.util.ParserUtil;
import java.util.Arrays;
import java.util.List;

public class KeepExperienceAction extends ActionDescriptor {
  double keepPct = 1.0D;
  
  boolean dropLeftovers;
  
  public KeepExperienceAction(List<String> args) throws DescriptorFormatException {
    if (args.size() > 0) {
      this.keepPct = ParserUtil.parsePercentage(args.get(0));
      if (this.keepPct == -1.0D || this.keepPct > 1.0D)
        throw new DescriptorFormatException("invalid percentage: " + (String)args.get(0)); 
    } 
    this.dropLeftovers = (args.size() > 1 && (((String)args.get(1)).equalsIgnoreCase("drop-leftovers") || Boolean.parseBoolean(args.get(1))));
  }
  
  public ActionAgent createAgent(DeathContext context) {
    return new KeepExperienceActionAgent(context, this);
  }
  
  public List<String> toParameters() {
    return Arrays.asList(new String[] { String.format("%.0f%%", new Object[] { Double.valueOf(this.keepPct * 100.0D) }), this.dropLeftovers ? "drop-leftovers" : "" });
  }
}
