package bone008.bukkit.deathcontrol.commandhandler;

import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.exceptions.CommandException;
import bone008.bukkit.deathcontrol.util.DPermission;
import bone008.bukkit.deathcontrol.util.Message;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

public abstract class SubCommand {
  protected String description = null;
  
  protected String usage = null;
  
  protected DPermission permission = null;
  
  public abstract void execute(CommandContext paramCommandContext) throws CommandException;
  
  public List<String> tabComplete(CommandContext context) throws CommandException {
    return Collections.emptyList();
  }
  
  public final void checkPermission(CommandSender sender, DPermission perm) throws CommandException {
    if (perm == null)
      return; 
    if (!DeathControl.instance.hasPermission((Permissible)sender, perm))
      throw new CommandException(Message.CMDCONTEXT_NO_PERMISSION); 
  }
  
  public final String getUsage() {
    return this.usage;
  }
  
  public final String getDescription() {
    return this.description;
  }
  
  public final DPermission getPermission() {
    return this.permission;
  }
}
