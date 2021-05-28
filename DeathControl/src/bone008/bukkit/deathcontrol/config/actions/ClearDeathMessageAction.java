package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionDescriptor;
import bone008.bukkit.deathcontrol.config.ActionResult;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import java.util.List;

public class ClearDeathMessageAction extends ActionDescriptor {
  public ClearDeathMessageAction(List<String> args) throws DescriptorFormatException {
    if (args.size() > 0)
      throw new DescriptorFormatException("action does not take any arguments"); 
  }
  
  public ActionAgent createAgent(DeathContext context) {
    return new ActionAgent(context, this) {
        public void preprocess() {
          this.context.getDeathEvent().setDeathMessage(null);
        }
        
        public ActionResult execute() {
          return null;
        }
        
        public void cancel() {}
      };
  }
}
