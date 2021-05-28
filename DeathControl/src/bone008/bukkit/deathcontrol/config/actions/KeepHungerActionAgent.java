package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionResult;
import bone008.bukkit.deathcontrol.config.DeathContext;
import org.bukkit.entity.Player;

public class KeepHungerActionAgent extends ActionAgent {
  private static final int MAX_FOOD_LEVEL = 20;
  
  private final KeepHungerAction action;
  
  private int foodLevel;
  
  private float saturation;
  
  private float exhaustion;
  
  public KeepHungerActionAgent(DeathContext context, KeepHungerAction action) {
    super(context, action);
    this.action = action;
  }
  
  public void preprocess() {
    Player p = this.context.getVictim().getPlayer();
    this.foodLevel = p.getFoodLevel();
    this.saturation = p.getSaturation();
    this.exhaustion = p.getExhaustion();
    this.foodLevel = 20 - (int)Math.round((20 - this.foodLevel) * this.action.keepPct);
  }
  
  public ActionResult execute() {
    if (!this.context.getVictim().isOnline())
      return ActionResult.PLAYER_OFFLINE; 
    Player p = this.context.getVictim().getPlayer();
    p.setFoodLevel(this.foodLevel);
    p.setSaturation(this.saturation);
    p.setExhaustion(this.exhaustion);
    return null;
  }
  
  public void cancel() {}
}
