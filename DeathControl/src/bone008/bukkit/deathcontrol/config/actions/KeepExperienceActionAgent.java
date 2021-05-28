package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionResult;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.util.ExperienceUtil;
import bone008.bukkit.deathcontrol.util.Util;

public class KeepExperienceActionAgent extends ActionAgent {
  private final KeepExperienceAction action;
  
  private int stored;
  
  private int preventedFromDropping;
  
  public KeepExperienceActionAgent(DeathContext context, KeepExperienceAction action) {
    super(context, action);
    this.action = action;
  }
  
  public void preprocess() {
    this.stored = (int)Math.round(ExperienceUtil.getCurrentExp(this.context.getVictim().getPlayer()) * this.action.keepPct);
    int dropped = (int)Math.round(this.context.getDeathEvent().getDroppedExp() * (1.0D - this.action.keepPct));
    if (!this.action.dropLeftovers)
      dropped = 0; 
    this.preventedFromDropping = this.context.getDeathEvent().getDroppedExp() - dropped;
    this.context.getDeathEvent().setDroppedExp(dropped);
  }
  
  public ActionResult execute() {
    if (!this.context.getVictim().isOnline()) {
      cancel();
      return ActionResult.PLAYER_OFFLINE;
    } 
    ExperienceUtil.changeExp(this.context.getVictim().getPlayer(), this.stored);
    return null;
  }
  
  public void cancel() {
    if (this.preventedFromDropping > 0)
      Util.dropExp(this.context.getDeathLocation(), this.preventedFromDropping); 
  }
}
