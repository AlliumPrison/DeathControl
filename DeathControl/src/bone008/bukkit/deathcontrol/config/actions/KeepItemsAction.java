package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import java.util.List;

public class KeepItemsAction extends AbstractItemsAction {
  public KeepItemsAction(List<String> args) throws DescriptorFormatException {
    parseFilter(args, true);
  }
  
  public ActionAgent createAgent(DeathContext context) {
    return new KeepItemsActionAgent(context, this);
  }
}
