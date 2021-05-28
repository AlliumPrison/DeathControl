package bone008.bukkit.deathcontrol.commandhandler;

import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.exceptions.CommandException;
import bone008.bukkit.deathcontrol.util.DPermission;
import bone008.bukkit.deathcontrol.util.MessageUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.permissions.Permissible;
import org.bukkit.util.StringUtil;

public class CommandHandler implements TabExecutor {
  public final Map<String, SubCommand> commandMap = new TreeMap<>();
  
  public final Map<String, SubCommand> aliasesMap = new TreeMap<>();
  
  private String msgOnUndefined = null;
  
  private DPermission basePermissionNode = null;
  
  public void addSubCommand(String name, SubCommand cmd, String... aliases) {
    if (name == null || name.trim().isEmpty())
      throw new IllegalArgumentException("invalid name"); 
    this.commandMap.put(name, cmd);
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = aliases).length, b = 0; b < i; ) {
      String alias = arrayOfString[b];
      this.aliasesMap.put(alias, cmd);
      b++;
    } 
  }
  
  public void setMessageOnUndefinedCommand(String msg) {
    msg = (msg == null) ? null : msg.trim();
    if (msg.isEmpty())
      msg = null; 
    this.msgOnUndefined = msg;
  }
  
  public void setBasePermissionNode(DPermission node) {
    this.basePermissionNode = node;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length == 1) {
      List<String> matches = new ArrayList<>();
      String token = args[0].toLowerCase();
      for (Map.Entry<String, SubCommand> cmdEntry : this.commandMap.entrySet()) {
        if (StringUtil.startsWithIgnoreCase(cmdEntry.getKey(), token) && (cmd.getPermission() == null || DeathControl.instance.hasPermission((Permissible)sender, ((SubCommand)cmdEntry.getValue()).getPermission())))
          matches.add(cmdEntry.getKey()); 
      } 
      for (Map.Entry<String, SubCommand> cmdEntry : this.aliasesMap.entrySet()) {
        if (StringUtil.startsWithIgnoreCase(cmdEntry.getKey(), token) && (cmd.getPermission() == null || DeathControl.instance.hasPermission((Permissible)sender, ((SubCommand)cmdEntry.getValue()).getPermission())))
          matches.add(cmdEntry.getKey()); 
      } 
      return matches;
    } 
    if (args.length > 1) {
      SubCommand subCmd = getCmdByName(args[0]);
      if (subCmd != null)
        try {
          return subCmd.tabComplete(new CommandContext(sender, cmd, label, this, getSubArgs(args)));
        } catch (CommandException e) {
          MessageUtil.sendMessage(sender, "Error on tab-completion: " + e.getMessage(), true);
        }  
    } 
    return Collections.emptyList();
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    boolean success = false;
    if (args.length > 0)
      success = handleCommand(sender, cmd, label, args[0], getSubArgs(args)); 
    if (!success) {
      if (this.msgOnUndefined == null)
        return false; 
      sender.sendMessage(this.msgOnUndefined);
    } 
    return true;
  }
  
  private boolean handleCommand(CommandSender sender, Command mainCmd, String mainLabel, String cmdName, String[] args) {
    SubCommand cmd = getCmdByName(cmdName);
    if (cmd == null)
      return false; 
    try {
      cmd.checkPermission(sender, this.basePermissionNode);
      cmd.checkPermission(sender, cmd.getPermission());
      cmd.execute(new CommandContext(sender, mainCmd, mainLabel, this, args));
    } catch (CommandException e) {
      if (e.getTranslatableMessage() == null) {
        MessageUtil.sendMessage(sender, e.getMessage(), true);
      } else {
        MessageUtil.sendMessage(sender, e.getTranslatableMessage(), new String[0]);
      } 
    } 
    return true;
  }
  
  private SubCommand getCmdByName(String cmdName) {
    SubCommand cmd = this.commandMap.get(cmdName.toLowerCase());
    if (cmd == null)
      cmd = this.aliasesMap.get(cmdName); 
    return cmd;
  }
  
  private String[] getSubArgs(String[] args) {
    String[] subArgs = new String[args.length - 1];
    System.arraycopy(args, 1, subArgs, 0, subArgs.length);
    return subArgs;
  }
}
