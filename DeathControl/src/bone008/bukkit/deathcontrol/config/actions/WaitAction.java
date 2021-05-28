package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionDescriptor;
import bone008.bukkit.deathcontrol.config.ActionResult;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import bone008.bukkit.deathcontrol.util.ParserUtil;
import java.util.Arrays;
import java.util.List;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WaitAction extends ActionDescriptor {
  private final boolean isCommand;
  
  private final int time;
  
  private class WaitAgent extends ActionAgent {
    private BukkitRunnable task = null;
    
    public void preprocess() {}
    
    public ActionResult execute() {
      this.task = new BukkitRunnable() {
          public void run() {
            WaitAction.WaitAgent.this.context.continueExecution(ActionResult.BLOCK_TIMER);
          }
        };
      this.task.runTaskLater((Plugin)DeathControl.instance, WaitAction.this.time * 20L);
      return ActionResult.BLOCK_TIMER;
    }
    
    public void cancel() {
      try {
        if (this.task != null)
          this.task.cancel(); 
      } catch (IllegalStateException illegalStateException) {}
    }
    
    public WaitAgent(DeathContext context) {
      super(context, WaitAction.this);
    }
  }
  
  private class WaitAgentCmd extends ActionAgent {
    private BukkitRunnable task = null;
    
    public void preprocess() {}
    
    public ActionResult execute() {
      if (WaitAction.this.time > -1) {
        this.task = new BukkitRunnable() {
            public void run() {
              WaitAction.WaitAgentCmd.this.context.cancel();
            }
          };
        this.task.runTaskLater((Plugin)DeathControl.instance, WaitAction.this.time * 20L);
      } 
      return ActionResult.BLOCK_COMMAND;
    }
    
    public void cancel() {
      try {
        if (this.task != null)
          this.task.cancel(); 
      } catch (IllegalStateException illegalStateException) {}
    }
    
    public WaitAgentCmd(DeathContext context) {
      super(context, WaitAction.this);
    }
  }
  
  public WaitAction(List<String> args) throws DescriptorFormatException {
    if (args.isEmpty())
      throw new DescriptorFormatException("no wait time given"); 
    this.isCommand = ((String)args.get(0)).equalsIgnoreCase("command");
    if (this.isCommand && args.size() < 2) {
      this.time = -1;
    } else {
      this.time = ParserUtil.parseTime(args.get(this.isCommand ? 1 : 0), -1);
    } 
    if (!this.isCommand && this.time < 0)
      throw new DescriptorFormatException("can't wait indefinitely"); 
  }
  
  public ActionAgent createAgent(DeathContext context) {
    if (this.isCommand)
      return new WaitAgentCmd(context); 
    return new WaitAgent(context);
  }
  
  public List<String> toParameters() {
    if (this.isCommand)
      return Arrays.asList(new String[] { "command", String.valueOf(this.time) + "s" }); 
    return Arrays.asList(new String[] { String.valueOf(this.time) + "s" });
  }
}
