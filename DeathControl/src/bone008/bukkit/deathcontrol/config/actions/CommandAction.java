package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionDescriptor;
import bone008.bukkit.deathcontrol.config.ActionResult;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandAction extends ActionDescriptor {
  private final boolean asConsole;
  
  private final String commandString;
  
  public CommandAction(List<String> args) throws DescriptorFormatException {
    if (args.size() < 2)
      throw new DescriptorFormatException("not enough arguments"); 
    String senderStr = args.remove(0);
    if (senderStr.equalsIgnoreCase("console")) {
      this.asConsole = true;
    } else if (senderStr.equalsIgnoreCase("victim")) {
      this.asConsole = false;
    } else {
      throw new DescriptorFormatException("invalid command sender: only \"victim\" or \"console\" is allowed!");
    } 
    this.commandString = Util.joinCollection(" ", args);
  }
  
  public ActionAgent createAgent(DeathContext context) {
    return new ActionAgent(context, this) {
        public void preprocess() {}
        
        public ActionResult execute() {
          Player player;
          if (CommandAction.this.asConsole) {
            ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();
          } else if (this.context.getVictim().isOnline()) {
            player = this.context.getVictim().getPlayer();
          } else {
            return ActionResult.PLAYER_OFFLINE;
          } 
          String cmd = this.context.replaceVariables(CommandAction.this.commandString);
          this.context.setVariable("last-command", cmd);
          try {
            boolean result = Bukkit.getServer().dispatchCommand((CommandSender)player, cmd);
            return result ? ActionResult.STANDARD : ActionResult.FAILED;
          } catch (CommandException e) {
            DeathControl.instance.getLogger().log(Level.SEVERE, "Executing the command \"" + cmd + "\" threw an exception!", (Throwable)e);
            return ActionResult.FAILED;
          } 
        }
        
        public void cancel() {}
      };
  }
  
  public List<String> toParameters() {
    return Arrays.asList(new String[] { ChatColor.ITALIC + (this.asConsole ? "console" : "victim") + ChatColor.RESET, this.commandString });
  }
}
