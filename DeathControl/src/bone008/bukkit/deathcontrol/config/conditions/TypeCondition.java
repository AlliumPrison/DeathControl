package bone008.bukkit.deathcontrol.config.conditions;

import bone008.bukkit.deathcontrol.config.ConditionDescriptor;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class TypeCondition extends ConditionDescriptor {
  private enum SpecialType {
    MONSTER, PROJECTILE, TAMED_WOLF;
  }
  
  private Set<EntityType> basicTypes = EnumSet.noneOf(EntityType.class);
  
  private Set<SpecialType> specialTypes = EnumSet.noneOf(SpecialType.class);
  
  public TypeCondition(List<String> args) throws DescriptorFormatException {
    if (args.isEmpty())
      throw new DescriptorFormatException("no type given"); 
    for (String input : args) {
      String inputEnum = input.toUpperCase().replace('-', '_');
      try {
        this.specialTypes.add(SpecialType.valueOf(inputEnum));
      } catch (IllegalArgumentException e) {
        EntityType basicType = EntityType.fromName(input);
        if (basicType == null)
          try {
            basicType = EntityType.valueOf(inputEnum);
          } catch (IllegalArgumentException e2) {
            throw new DescriptorFormatException("invalid type: " + input);
          }  
        this.basicTypes.add(basicType);
      } 
    } 
  }
  
  public boolean matches(DeathContext context) {
    EntityDamageEvent dmgEvent = context.getVictim().getPlayer().getLastDamageCause();
    EntityDamageByEntityEvent dmgBEEvent = (dmgEvent instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent)dmgEvent : null;
    if (dmgBEEvent != null) {
      Entity damager = dmgBEEvent.getDamager();
      for (EntityType etype : this.basicTypes) {
        if (etype == damager.getType())
          return true; 
        if (damager instanceof Projectile && ((Projectile)damager).getShooter() instanceof Entity) {
          damager = (Entity)((Projectile)damager).getShooter();
          if (damager != null && etype == damager.getType())
            return true; 
        } 
      } 
    } 
    for (SpecialType special : this.specialTypes) {
      boolean match = false;
      switch (special) {
        case null:
          match = Util.getAttackerFromEvent((EntityDamageEvent)dmgBEEvent) instanceof org.bukkit.entity.Monster;
          break;
        case PROJECTILE:
          match = (dmgBEEvent != null && dmgBEEvent.getDamager() instanceof Projectile);
          break;
        case TAMED_WOLF:
          match = (dmgBEEvent != null && dmgBEEvent.getDamager() instanceof Wolf && ((Wolf)dmgBEEvent.getDamager()).isTamed());
          break;
        default:
          throw new IllegalStateException("unknown special enum member: " + special);
      } 
      if (match)
        return true; 
    } 
    return false;
  }
  
  public List<String> toParameters() {
    List<String> ret = new ArrayList<>();
    for (EntityType t : this.basicTypes)
      ret.add(t.toString().toLowerCase().replace('_', '-')); 
    for (SpecialType t : this.specialTypes)
      ret.add(t.toString().toLowerCase().replace('_', '-')); 
    return ret;
  }
}
