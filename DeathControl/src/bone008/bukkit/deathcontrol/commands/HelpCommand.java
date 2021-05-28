package bone008.bukkit.deathcontrol.commands;

import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.commandhandler.CommandContext;
import bone008.bukkit.deathcontrol.commandhandler.SubCommand;
import bone008.bukkit.deathcontrol.exceptions.CommandException;
import bone008.bukkit.deathcontrol.util.MessageUtil;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permissible;

public class HelpCommand extends SubCommand {
  public void execute(CommandContext context) throws CommandException {
    MessageUtil.sendMessage(context.sender, ChatColor.GRAY + DeathControl.instance.pdfFile.getFullName() + " by Bone008", null);
    String subCmdPrefix = ChatColor.BLUE + "/" + context.mainLabel + " ";
    for (Map.Entry<String, SubCommand> cmdEntry : (Iterable<Map.Entry<String, SubCommand>>)context.cmdHandler.commandMap.entrySet()) {
      SubCommand cmd = cmdEntry.getValue();
      String cmdName = cmdEntry.getKey();
      if (cmd.getPermission() != null && !DeathControl.instance.hasPermission((Permissible)context.sender, cmd.getPermission()))
        continue; 
      StringBuilder sb = new StringBuilder();
      if (cmd.getUsage() == null) {
        sb.append(subCmdPrefix).append(cmdName);
      } else {
        sb.append(Util.wrapPrefixed(cmd.getUsage(), subCmdPrefix));
      } 
      if (cmd.getDescription() != null) {
        sb.append('\n');
        sb.append(Util.wrapPrefixed(cmd.getDescription(), "   " + ChatColor.GRAY));
      } 
      MessageUtil.sendMessage(context.sender, sb.toString(), "> ");
    } 
  }
}
