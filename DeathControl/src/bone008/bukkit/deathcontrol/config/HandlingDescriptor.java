package bone008.bukkit.deathcontrol.config;

import bone008.bukkit.deathcontrol.DeathContextImpl;
import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.util.ErrorObserver;
import bone008.bukkit.deathcontrol.util.ParserUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;

public class HandlingDescriptor implements Comparable<HandlingDescriptor> {
  private final String name;
  
  private final int priority;
  
  private final boolean lastHandling;
  
  private final int timeoutOnDisconnect;
  
  private final String cancelMessage;
  
  private final List<ConditionDescriptor> conditions;
  
  private final List<Boolean> expectedConditionResults;
  
  private final List<ActionDescriptor> actions;
  
  public HandlingDescriptor(String name, ConfigurationSection config, ErrorObserver log) {
    this.name = name;
    this.priority = config.getInt("priority-order", 0);
    this.lastHandling = !config.getBoolean("allow-others", false);
    this.timeoutOnDisconnect = ParserUtil.parseTime(config.getString("timeout-on-disconnect"), 30);
    this.cancelMessage = config.getString("cancel-message", null);
    List<String> rawConditions = config.getStringList("conditions");
    this.conditions = new ArrayList<>(rawConditions.size());
    this.expectedConditionResults = new ArrayList<>(rawConditions.size());
    Iterator<String> it;
    int i;
    for (i = 1, it = rawConditions.iterator(); it.hasNext(); i++) {
      String current = ((String)it.next()).trim();
      if (current.isEmpty()) {
        log.addWarning("Condition %d is empty!", new Object[] { Integer.valueOf(i) });
      } else {
        String opName = ParserUtil.parseOperationName(current);
        List<String> opArgs = ParserUtil.parseOperationArgs(current);
        boolean inverted = opName.startsWith("-");
        if (inverted)
          opName = opName.substring(1); 
        ConditionDescriptor descriptor = ConditionDescriptor.createDescriptor(opName, opArgs, log);
        if (descriptor != null) {
          this.conditions.add(descriptor);
          this.expectedConditionResults.add(Boolean.valueOf(!inverted));
        } 
      } 
    } 
    List<String> rawActions = config.getStringList("actions");
    this.actions = new ArrayList<>(rawActions.size());
    for (i = 1, it = rawActions.iterator(); it.hasNext(); i++) {
      String current = ((String)it.next()).trim();
      if (current.isEmpty()) {
        log.addWarning("Action %d is empty!", new Object[] { Integer.valueOf(i) });
      } else {
        String opName = ParserUtil.parseOperationName(current);
        List<String> opArgs = ParserUtil.parseOperationArgs(current);
        boolean required = !(!opName.equalsIgnoreCase("require") && !opName.equalsIgnoreCase("required"));
        if (required)
          opName = opArgs.remove(0); 
        ActionDescriptor descriptor = ActionDescriptor.createDescriptor(opName, opArgs, log);
        if (descriptor != null) {
          descriptor.setRequired(required);
          this.actions.add(descriptor);
        } 
      } 
    } 
  }
  
  public boolean areConditionsMet(DeathContext context) {
    DeathControl.instance.log(Level.FINEST, "@" + context.getVictim().getName() + ":  \"" + this.name + "\" is checking conditions ...");
    for (int i = 0; i < this.conditions.size(); i++) {
      ConditionDescriptor condition = this.conditions.get(i);
      try {
        boolean matched = condition.matches(context);
        if (matched != ((Boolean)this.expectedConditionResults.get(i)).booleanValue()) {
          DeathControl.instance.log(Level.FINEST, "    \"" + condition.getName() + "\" failed");
          return false;
        } 
        DeathControl.instance.log(Level.FINEST, "    \"" + condition.getName() + "\" matched");
      } catch (Throwable e) {
        DeathControl.instance.getLogger().log(Level.SEVERE, "Condition check \"" + condition.getName() + "\" threw an exception!", e);
        return false;
      } 
    } 
    return true;
  }
  
  public void assignAgents(DeathContextImpl context) {
    for (ActionDescriptor action : this.actions)
      context.assignAgent(action.createAgent((DeathContext)context)); 
  }
  
  public String getName() {
    return this.name;
  }
  
  public int getPriority() {
    return this.priority;
  }
  
  public boolean isLastHandling() {
    return this.lastHandling;
  }
  
  public int getTimeoutOnDisconnect() {
    return this.timeoutOnDisconnect;
  }
  
  public String getCancelMessage() {
    return this.cancelMessage;
  }
  
  public boolean isInverted(int conditionIndex) {
    return !((Boolean)this.expectedConditionResults.get(conditionIndex)).booleanValue();
  }
  
  public List<ConditionDescriptor> getConditions() {
    return Collections.unmodifiableList(this.conditions);
  }
  
  public List<ActionDescriptor> getActions() {
    return Collections.unmodifiableList(this.actions);
  }
  
  public int compareTo(HandlingDescriptor other) {
    if (this.priority == other.priority)
      return -1; 
    return this.priority - other.priority;
  }
  
  public String toString() {
    return this.name;
  }
}
