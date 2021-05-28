package bone008.bukkit.deathcontrol.commands;

import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.commandhandler.CommandContext;
import bone008.bukkit.deathcontrol.commandhandler.SubCommand;
import bone008.bukkit.deathcontrol.exceptions.CommandException;
import bone008.bukkit.deathcontrol.util.MessageUtil;
import org.bukkit.ChatColor;

public class ReloadCommand extends SubCommand {
  public void execute(CommandContext context) throws CommandException {
    DeathControl.instance.loadConfig();
    MessageUtil.sendMessage(context.sender, ChatColor.GREEN + "Reloaded all config files!");
  }
}
