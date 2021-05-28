package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionDescriptor;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.config.lists.BasicListItem;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import bone008.bukkit.deathcontrol.exceptions.FormatException;
import bone008.bukkit.deathcontrol.util.ParserUtil;
import java.util.Arrays;
import java.util.List;

public class DestroyItemAction extends ActionDescriptor {
  BasicListItem item;
  
  int amount = 1;
  
  public DestroyItemAction(List<String> args) throws DescriptorFormatException {
    if (args.size() > 2)
      throw new DescriptorFormatException("too many arguments"); 
    if (args.size() == 0)
      throw new DescriptorFormatException("no item given"); 
    if (args.size() == 2) {
      this.amount = ParserUtil.parseInt(args.get(1));
      if (this.amount <= 0)
        throw new DescriptorFormatException("invalid amount: " + (String)args.get(1)); 
    } 
    try {
      this.item = BasicListItem.parse(args.get(0));
    } catch (FormatException e) {
      throw new DescriptorFormatException(e.getMessage());
    } 
  }
  
  public ActionAgent createAgent(DeathContext context) {
    return new DestroyItemActionAgent(context, this);
  }
  
  public List<String> toParameters() {
    return Arrays.asList(new String[] { this.item.toHumanString().toString(), this.amount });
  }
}
