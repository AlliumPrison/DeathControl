package bone008.bukkit.deathcontrol.config;

import bone008.bukkit.deathcontrol.config.conditions.CauseCondition;
import bone008.bukkit.deathcontrol.config.conditions.FoodLevelCondition;
import bone008.bukkit.deathcontrol.config.conditions.PermissionCondition;
import bone008.bukkit.deathcontrol.config.conditions.RegionCondition;
import bone008.bukkit.deathcontrol.config.conditions.TypeCondition;
import bone008.bukkit.deathcontrol.config.conditions.WorldCondition;
import bone008.bukkit.deathcontrol.util.ErrorObserver;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ConditionDescriptor {
  private static final Map<String, Class<? extends ConditionDescriptor>> registeredTypes = new HashMap<>();
  
  public static void registerCondition(String name, Class<? extends ConditionDescriptor> clazz) {
    name = name.toLowerCase();
    if (registeredTypes.containsKey(name))
      throw new IllegalArgumentException("condition " + name + " is already registered"); 
    registeredTypes.put(name, clazz);
  }
  
  public static Set<String> getDescriptorNames() {
    return registeredTypes.keySet();
  }
  
  public static ConditionDescriptor createDescriptor(String name, List<String> args, ErrorObserver log) {
    name = name.toLowerCase();
    if (!registeredTypes.containsKey(name)) {
      log.addWarning("Condition \"%s\" not found!", new Object[] { name });
      return null;
    } 
    try {
      ConditionDescriptor condition = ((Class<ConditionDescriptor>)registeredTypes.get(name.toLowerCase())).getConstructor(new Class[] { List.class }).newInstance(new Object[] { args });
      condition.name = name;
      return condition;
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException) {
        log.addWarning("Condition \"%s\": %s", new Object[] { name, e.getCause().getMessage() });
      } else {
        e.printStackTrace();
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return null;
  }
  
  static {
    registerCondition("cause", (Class)CauseCondition.class);
    registerCondition("world", (Class)WorldCondition.class);
    registerCondition("permission", (Class)PermissionCondition.class);
    registerCondition("food-level", (Class)FoodLevelCondition.class);
    registerCondition("killer-type", (Class)TypeCondition.class);
    registerCondition("region", (Class)RegionCondition.class);
  }
  
  private String name = "";
  
  public List<String> toParameters() {
    return Collections.emptyList();
  }
  
  public final String getName() {
    return this.name;
  }
  
  public abstract boolean matches(DeathContext paramDeathContext);
}
