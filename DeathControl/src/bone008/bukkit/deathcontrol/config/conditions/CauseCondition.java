package bone008.bukkit.deathcontrol.config.conditions;

import bone008.bukkit.deathcontrol.DeathCause;
import bone008.bukkit.deathcontrol.config.ConditionDescriptor;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.entity.EntityDamageEvent;

public class CauseCondition extends ConditionDescriptor {
  private List<DeathCause> causes = new ArrayList<>();
  
  public CauseCondition(List<String> args) throws DescriptorFormatException {
    if (args.isEmpty())
      throw new DescriptorFormatException("no causes given"); 
    for (String arg : args) {
      DeathCause cause = DeathCause.parseCause(arg);
      if (cause == null)
        throw new DescriptorFormatException("invalid death cause: " + arg); 
      this.causes.add(cause);
    } 
  }
  
  public boolean matches(DeathContext context) {
    EntityDamageEvent lastDamage = context.getVictim().getPlayer().getLastDamageCause();
    for (DeathCause cause : this.causes) {
      if (cause.appliesTo(lastDamage))
        return true; 
    } 
    return false;
  }
  
  public List<String> toParameters() {
    List<String> ret = new ArrayList<>(this.causes.size());
    for (DeathCause dc : this.causes)
      ret.add(dc.toHumanString()); 
    return ret;
  }
}
