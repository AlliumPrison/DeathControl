package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionDescriptor;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import bone008.bukkit.deathcontrol.util.ParserUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChargeAction extends ActionDescriptor {
  boolean isPercentage;
  
  double money;
  
  double capMin = 0.0D;
  
  double capMax = Double.POSITIVE_INFINITY;
  
  public ChargeAction(List<String> args) throws DescriptorFormatException {
    Iterator<String> it = args.iterator();
    while (it.hasNext()) {
      String arg = ((String)it.next()).toLowerCase();
      if (arg.startsWith("min=")) {
        this.capMin = ParserUtil.parseDouble(arg.substring(4));
        if (this.capMin < 0.0D || this.capMin > this.capMax)
          throw new DescriptorFormatException("invalid minimum cap: " + arg.substring(4)); 
        it.remove();
        continue;
      } 
      if (arg.startsWith("max=")) {
        this.capMax = ParserUtil.parseDouble(arg.substring(4));
        if (this.capMax < 0.0D || this.capMax < this.capMin)
          throw new DescriptorFormatException("invalid maximum cap: " + arg.substring(4)); 
        it.remove();
      } 
    } 
    if (args.size() != 1)
      throw new DescriptorFormatException("no cost given!"); 
    double pctMoney = ParserUtil.parsePercentage(args.get(0), true);
    if (pctMoney != -1.0D && pctMoney <= 1.0D) {
      this.isPercentage = true;
      this.money = pctMoney;
    } else {
      this.isPercentage = false;
      this.money = ParserUtil.parseDouble(args.get(0));
      if (this.money < 0.0D)
        throw new DescriptorFormatException("invalid cost: " + (String)args.get(0)); 
    } 
  }
  
  public ActionAgent createAgent(DeathContext context) {
    return new ChargeActionAgent(context, this);
  }
  
  public List<String> toParameters() {
    List<String> ret = new ArrayList<>();
    if (this.isPercentage) {
      ret.add(String.format("%.0f%%", new Object[] { Double.valueOf(this.money * 100.0D) }));
      if (this.capMin > 0.0D)
        ret.add(String.format("min=%.2f", new Object[] { Double.valueOf(this.capMin) })); 
      if (this.capMax < Double.POSITIVE_INFINITY)
        ret.add(String.format("max=%.2f", new Object[] { Double.valueOf(this.capMax) })); 
    } else {
      ret.add(String.format("%.2f", new Object[] { Double.valueOf(this.money) }));
    } 
    return ret;
  }
}
