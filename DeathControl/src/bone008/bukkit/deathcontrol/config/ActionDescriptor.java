package bone008.bukkit.deathcontrol.config;

import bone008.bukkit.deathcontrol.config.actions.BroadcastAction;
import bone008.bukkit.deathcontrol.config.actions.ChargeAction;
import bone008.bukkit.deathcontrol.config.actions.ClearDeathMessageAction;
import bone008.bukkit.deathcontrol.config.actions.CommandAction;
import bone008.bukkit.deathcontrol.config.actions.DamageItemsAction;
import bone008.bukkit.deathcontrol.config.actions.DestroyItemAction;
import bone008.bukkit.deathcontrol.config.actions.DestroyItemsAction;
import bone008.bukkit.deathcontrol.config.actions.KeepExperienceAction;
import bone008.bukkit.deathcontrol.config.actions.KeepHungerAction;
import bone008.bukkit.deathcontrol.config.actions.KeepItemsAction;
import bone008.bukkit.deathcontrol.config.actions.MessageAction;
import bone008.bukkit.deathcontrol.config.actions.WaitAction;
import bone008.bukkit.deathcontrol.util.ErrorObserver;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ActionDescriptor {
  private static final Map<String, Class<? extends ActionDescriptor>> registeredTypes = new HashMap<>();
  
  public static void registerAction(String name, Class<? extends ActionDescriptor> clazz) {
    name = name.toLowerCase();
    if (registeredTypes.containsKey(name))
      throw new IllegalArgumentException("action " + name + " is already registered"); 
    registeredTypes.put(name, clazz);
  }
  
  public static Set<String> getDescriptorNames() {
    return registeredTypes.keySet();
  }
  
  public static ActionDescriptor createDescriptor(String name, List<String> args, ErrorObserver log) {
    name = name.toLowerCase();
    if (!registeredTypes.containsKey(name)) {
      log.addWarning("Action \"%s\" not found!", new Object[] { name });
      return null;
    } 
    try {
      ActionDescriptor action = ((Class<ActionDescriptor>)registeredTypes.get(name.toLowerCase())).getConstructor(new Class[] { List.class }).newInstance(new Object[] { args });
      action.name = name;
      return action;
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException) {
        log.addWarning("Action \"%s\": %s", new Object[] { name, e.getCause().getMessage() });
      } else {
        e.printStackTrace();
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return null;
  }
  
  static {
    registerAction("keep-items", (Class)KeepItemsAction.class);
    registerAction("keep-experience", (Class)KeepExperienceAction.class);
    registerAction("keep-hunger", (Class)KeepHungerAction.class);
    registerAction("charge", (Class)ChargeAction.class);
    registerAction("destroy-item", (Class)DestroyItemAction.class);
    registerAction("destroy-items", (Class)DestroyItemsAction.class);
    registerAction("damage-items", (Class)DamageItemsAction.class);
    registerAction("message", (Class)MessageAction.class);
    registerAction("broadcast", (Class)BroadcastAction.class);
    registerAction("command", (Class)CommandAction.class);
    registerAction("wait", (Class)WaitAction.class);
    registerAction("clear-death-message", (Class)ClearDeathMessageAction.class);
  }
  
  private String name = "";
  
  private boolean required = false;
  
  public final void setRequired(boolean required) {
    this.required = required;
  }
  
  public final boolean isRequired() {
    return this.required;
  }
  
  public final String getName() {
    return this.name;
  }
  
  public List<String> toParameters() {
    return Collections.emptyList();
  }
  
  public abstract ActionAgent createAgent(DeathContext paramDeathContext);
}
