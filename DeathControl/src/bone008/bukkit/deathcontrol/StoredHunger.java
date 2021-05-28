package bone008.bukkit.deathcontrol;

import org.bukkit.entity.Player;

public class StoredHunger {
  public final int foodLevel;
  
  public final float saturation;
  
  public final float exhaustion;
  
  public StoredHunger(int foodLevel, float saturation, float exhaustion) {
    this.foodLevel = foodLevel;
    this.saturation = saturation;
    this.exhaustion = exhaustion;
  }
  
  public StoredHunger(Player source) {
    this(source.getFoodLevel(), source.getSaturation(), source.getExhaustion());
  }
  
  public String toHumanString() {
    return String.format("food-level=%d, saturation=%.2f, exhaustion=%.2f", new Object[] { Integer.valueOf(this.foodLevel), Float.valueOf(this.saturation), Float.valueOf(this.exhaustion) });
  }
}
