package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionResult;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.util.EconomyUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permissible;

public class ChargeActionAgent extends ActionAgent {
  private final ChargeAction action;
  
  public ChargeActionAgent(DeathContext context, ChargeAction action) {
    super(context, action);
    this.action = action;
  }
  
  public void preprocess() {}
  
  public ActionResult execute() {
    double cost;
    OfflinePlayer victim = this.context.getVictim();
    if (victim.getPlayer() != null && DeathControl.instance.hasPermission((Permissible)victim.getPlayer(), DeathControl.PERMISSION_FREE))
      return ActionResult.STANDARD; 
    if (this.action.isPercentage) {
      cost = EconomyUtil.calcCost(victim, this.action.money);
      if (cost > this.action.capMax)
        cost = this.action.capMax; 
      if (cost < this.action.capMin)
        cost = this.action.capMin; 
    } else {
      cost = this.action.money;
    } 
    this.context.setVariable("money-paid", EconomyUtil.formatMoney(cost));
    this.context.setVariable("money-paid-raw", Double.valueOf(cost));
    return EconomyUtil.payCost(victim, cost) ? ActionResult.STANDARD : ActionResult.FAILED;
  }
  
  public void cancel() {}
}
