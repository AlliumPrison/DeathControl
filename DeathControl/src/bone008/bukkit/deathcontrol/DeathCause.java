package bone008.bukkit.deathcontrol;

import bone008.bukkit.deathcontrol.util.Util;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

public enum DeathCause {
  CONTACT(
    
    "cactus"),
  DROWNING("drowning"),
  EXPLOSION("explosion"),
  FALL("fall"),
  FALLING_BLOCK("fallingblock"),
  FIRE("fire"),
  FIRE_TICK("firetick"),
  LAVA("lava"),
  LIGHTNING("lightning"),
  MAGIC("magic"),
  MOB("mob"),
  MONSTER("monster"),
  PLAYER("pvp"),
  POISON("poison"),
  STARVATION("starvation"),
  SUFFOCATION("suffocation"),
  SUICIDE("suicide"),
  THORNS("thorns"),
  VOID("void"),
  WITHER("wither"),
  UNKNOWN("unknown");
  
  private final String name;
  
  DeathCause(String name) {
    this.name = name;
  }
  
  public boolean appliesTo(EntityDamageEvent event) {
    Entity attacker;
    byte b;
    int i;
    DeathCause[] arrayOfDeathCause;
    if (event == null)
      return (this == UNKNOWN); 
    EntityDamageEvent.DamageCause cause = event.getCause();
    switch (this) {
      case null:
      case DROWNING:
      case FALL:
      case FALLING_BLOCK:
      case FIRE_TICK:
      case LAVA:
      case LIGHTNING:
      case MAGIC:
      case POISON:
      case STARVATION:
      case SUFFOCATION:
      case SUICIDE:
      case THORNS:
      case VOID:
      case WITHER:
        return (cause == EntityDamageEvent.DamageCause.valueOf(name()));
      case EXPLOSION:
        return !(cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && cause != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION);
      case FIRE:
        return !(cause != EntityDamageEvent.DamageCause.FIRE && cause != EntityDamageEvent.DamageCause.FIRE_TICK);
      case MOB:
        attacker = Util.getAttackerFromEvent(event);
        return (attacker instanceof org.bukkit.entity.LivingEntity && !(attacker instanceof org.bukkit.entity.HumanEntity));
      case MONSTER:
        attacker = Util.getAttackerFromEvent(event);
        return attacker instanceof org.bukkit.entity.Monster;
      case PLAYER:
        return (Util.getPlayerAttackerFromEvent(event) != null);
      case UNKNOWN:
        for (i = (arrayOfDeathCause = values()).length, b = 0; b < i; ) {
          DeathCause dc = arrayOfDeathCause[b];
          if (dc != UNKNOWN)
            if (dc.appliesTo(event))
              return false;  
          b++;
        } 
        return true;
    } 
    throw new Error("unimplemented death cause: " + this);
  }
  
  public String toHumanString() {
    return this.name;
  }
  
  public String toMsgPath() {
    return "cause-reasons." + toHumanString();
  }
  
  public static DeathCause parseCause(String name) {
    byte b;
    int i;
    DeathCause[] arrayOfDeathCause;
    for (i = (arrayOfDeathCause = values()).length, b = 0; b < i; ) {
      DeathCause dc = arrayOfDeathCause[b];
      if (dc.name.equalsIgnoreCase(name))
        return dc; 
      b++;
    } 
    return null;
  }
}
