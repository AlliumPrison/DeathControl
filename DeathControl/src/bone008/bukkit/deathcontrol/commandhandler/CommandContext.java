package bone008.bukkit.deathcontrol.commandhandler;

import bone008.bukkit.deathcontrol.exceptions.CommandException;
import bone008.bukkit.deathcontrol.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;

public class CommandContext {
  private static final Message MSG_PLAYER_CONTEXT = Message.CMDCONTEXT_PLAYER_CONTEXT;
  
  private static final Message MSG_NOT_ENOUGH_ARGUMENTS = Message.CMDCONTEXT_ARGUMENT_MISSING;
  
  private static final Message MSG_NUMBER_EXPECTED = Message.CMDCONTEXT_NUMBER_EXPECTED;
  
  private static final Message MSG_INVALID_PLAYER = Message.CMDCONTEXT_INVALID_PLAYER;
  
  public final CommandSender sender;
  
  public final Command mainCmd;
  
  public final String mainLabel;
  
  public final CommandHandler cmdHandler;
  
  private String[] args;
  
  public CommandContext(CommandSender sender, Command mainCmd, String mainLabel, CommandHandler cmdHandler, String[] args) {
    this.sender = sender;
    this.mainCmd = mainCmd;
    this.mainLabel = mainLabel;
    this.cmdHandler = cmdHandler;
    this.args = args;
  }
  
  public Player getPlayerSender() throws CommandException {
    if (this.sender instanceof Player)
      return (Player)this.sender; 
    throw new CommandException(MSG_PLAYER_CONTEXT);
  }
  
  public Conversable getConversableSender() throws CommandException {
    if (this.sender instanceof Conversable)
      return (Conversable)this.sender; 
    throw new CommandException(MSG_PLAYER_CONTEXT);
  }
  
  public int argsCount() {
    return this.args.length;
  }
  
  public void ensureArgs(int len) throws CommandException {
    if (this.args.length < len)
      throw new CommandException(MSG_NOT_ENOUGH_ARGUMENTS); 
  }
  
  public void translate(int x) {
    if (x < 0)
      throw new IllegalArgumentException("cannot shift backwards"); 
    String[] newArgs = new String[this.args.length - x];
    System.arraycopy(this.args, x, newArgs, 0, newArgs.length);
    this.args = newArgs;
  }
  
  public String getStringArg(int pos) throws CommandException {
    if (this.args.length > pos)
      return this.args[pos]; 
    throw new CommandException(MSG_NOT_ENOUGH_ARGUMENTS);
  }
  
  public String getStringArg(int pos, String def) {
    if (this.args.length > pos)
      return this.args[pos]; 
    return def;
  }
  
  public int getIntArg(int pos) throws CommandException {
    try {
      return Integer.parseInt(getStringArg(pos));
    } catch (NumberFormatException ex) {
      throw new CommandException(MSG_NUMBER_EXPECTED);
    } 
  }
  
  public int getIntArg(int pos, int def) throws CommandException {
    try {
      return Integer.parseInt(getStringArg(pos, Integer.toString(def)));
    } catch (NumberFormatException ex) {
      throw new CommandException(MSG_NUMBER_EXPECTED);
    } 
  }
  
  public double getDoubleArg(int pos) throws CommandException {
    try {
      return Double.parseDouble(getStringArg(pos));
    } catch (NumberFormatException ex) {
      throw new CommandException(MSG_NUMBER_EXPECTED);
    } 
  }
  
  public Player getPlayerArg(int pos) throws CommandException {
    Player ply = Bukkit.getPlayer(getStringArg(pos));
    if (ply == null)
      throw new CommandException(MSG_INVALID_PLAYER); 
    return ply;
  }
  
  public String getChainedArgs(int startPos, boolean require) throws CommandException {
    if (startPos >= this.args.length) {
      if (require)
        throw new CommandException(MSG_NOT_ENOUGH_ARGUMENTS); 
      return null;
    } 
    StringBuilder sb = new StringBuilder();
    for (int i = startPos; i < this.args.length; i++) {
      if (i > startPos)
        sb.append(' '); 
      sb.append(this.args[i]);
    } 
    return sb.toString();
  }
}
