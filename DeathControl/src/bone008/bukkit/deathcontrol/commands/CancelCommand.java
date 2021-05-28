package bone008.bukkit.deathcontrol.commands;

import bone008.bukkit.deathcontrol.DeathContextImpl;
import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.commandhandler.CommandContext;
import bone008.bukkit.deathcontrol.commandhandler.SubCommand;
import bone008.bukkit.deathcontrol.exceptions.CommandException;
import bone008.bukkit.deathcontrol.util.Message;
import org.bukkit.entity.Player;

public class CancelCommand extends SubCommand {
  public void execute(CommandContext context) throws CommandException {
    Player player = context.getPlayerSender();
    DeathContextImpl deathContext = DeathControl.instance.getActiveDeath(player.getUniqueId());
    if (deathContext != null) {
      deathContext.cancelManually();
    } else {
      throw new CommandException(Message.CMD_NOTHING_STORED);
    } 
  }
}
