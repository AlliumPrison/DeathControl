package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionDescriptor;
import bone008.bukkit.deathcontrol.config.ActionResult;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageAction extends ActionDescriptor {
  private final String message;
  
  public MessageAction(List<String> args) {
    this.message = ChatColor.translateAlternateColorCodes('&', Util.joinCollection(" ", args));
  }
  
  public ActionAgent createAgent(DeathContext context) {
    return new ActionAgent(context, this) {
        public void preprocess() {}
        
        public ActionResult execute() {
          Player victimPlayer = this.context.getVictim().getPlayer();
          if (victimPlayer == null)
            return ActionResult.PLAYER_OFFLINE; 
          this.context.getVictim().getPlayer().sendMessage(this.context.replaceVariables(MessageAction.this.message));
          return null;
        }
        
        public void cancel() {}
      };
  }
  
  public List<String> toParameters() {
    return Arrays.asList(new String[] { this.message });
  }
}
