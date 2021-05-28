package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import bone008.bukkit.deathcontrol.util.ParserUtil;
import java.util.List;

public class DamageItemsAction extends AbstractItemsAction {
  double damagePct;
  
  public DamageItemsAction(List<String> args) throws DescriptorFormatException {
    if (args.isEmpty())
      throw new DescriptorFormatException("no damage percentage given"); 
    this.damagePct = ParserUtil.parsePercentage(args.remove(0));
    if (this.damagePct == -1.0D || this.damagePct > 1.0D)
      throw new DescriptorFormatException("invalid damage percentage!"); 
    parseFilter(args, false);
  }
  
  public ActionAgent createAgent(DeathContext context) {
    return new DamageItemsActionAgent(context, this);
  }
  
  public List<String> toParameters() {
    List<String> ret = super.toParameters();
    ret.add(0, String.format("%.0f%%", new Object[] { Double.valueOf(this.damagePct * 100.0D) }));
    return ret;
  }
}
