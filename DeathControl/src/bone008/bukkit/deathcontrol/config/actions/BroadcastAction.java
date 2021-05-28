package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionDescriptor;
import bone008.bukkit.deathcontrol.config.ActionResult;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class BroadcastAction extends ActionDescriptor {
  private final String message;
  
  public BroadcastAction(List<String> args) {
    this.message = ChatColor.translateAlternateColorCodes('&', Util.joinCollection(" ", args));
  }
  
  public ActionAgent createAgent(DeathContext context) {
    return new ActionAgent(context, this) {
        public void preprocess() {}
        
        public ActionResult execute() {
          Bukkit.broadcastMessage(this.context.replaceVariables(BroadcastAction.this.message));
          return null;
        }
        
        public void cancel() {}
      };
  }
  
  public List<String> toParameters() {
    return Arrays.asList(new String[] { this.message });
  }
}
