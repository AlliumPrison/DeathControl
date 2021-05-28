package bone008.bukkit.deathcontrol.config;

import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.util.ErrorObserver;
import bone008.bukkit.deathcontrol.util.ParserUtil;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.StringUtil;

public class NewConfiguration {
  private static final boolean default_bukkitPerms = true;
  
  private static final String default_loggingLevel = "standard";
  
  private static final boolean default_allowCrossworld = true;
  
  private static final List<String> default_blacklistedWorlds = new ArrayList<>();
  
  private boolean bukkitPerms;
  
  private int loggingLevel;
  
  private boolean allowCrossworld;
  
  private Set<String> blacklistedWorlds;
  
  private Set<HandlingDescriptor> handlings = new TreeSet<>();
  
  public NewConfiguration(Configuration config) {
    ErrorObserver errors = new ErrorObserver();
    errors.setPrefix("-> ");
    this.bukkitPerms = !config.getBoolean("disable-permissions", true);
    if (config.get("logging-level") instanceof Number)
      config.set("logging-level", "standard"); 
    String rawLoggingLevel = config.getString("logging-level", "standard");
    this.loggingLevel = ParserUtil.parseLoggingLevel(rawLoggingLevel);
    if (this.loggingLevel == -1) {
      errors.addWarning("invalid logging-level: " + rawLoggingLevel, new Object[0]);
      this.loggingLevel = ParserUtil.parseLoggingLevel("standard");
    } 
    this.allowCrossworld = config.getBoolean("multi-world.allow-cross-world", true);
    List<String> rawBlacklistedWorlds = config.getStringList("multi-world.blacklisted-worlds");
    List<String> rawBlacklistedWorlds2 = config.getStringList("multi-world.disabled-worlds");
    if (rawBlacklistedWorlds == null || rawBlacklistedWorlds.isEmpty())
      rawBlacklistedWorlds = default_blacklistedWorlds; 
    if (rawBlacklistedWorlds2 == null || rawBlacklistedWorlds2.isEmpty())
      rawBlacklistedWorlds2 = default_blacklistedWorlds; 
    this.blacklistedWorlds = new HashSet<>();
    this.blacklistedWorlds.addAll(rawBlacklistedWorlds);
    this.blacklistedWorlds.addAll(rawBlacklistedWorlds2);
    ConfigurationSection handlingsSec = config.getConfigurationSection("handlings");
    if (handlingsSec != null) {
      Set<String> handlingNames = handlingsSec.getKeys(false);
      for (String name : handlingNames) {
        ErrorObserver handlingLog = new ErrorObserver();
        handlingLog.setPrefix("  ");
        ConfigurationSection hndSec = handlingsSec.getConfigurationSection(name);
        if (hndSec == null) {
          handlingLog.addWarning("Invalid format!", new Object[0]);
        } else {
          this.handlings.add(new HandlingDescriptor(name, hndSec, handlingLog));
        } 
        handlingLog.logTo(errors, "Handling " + name + ":");
      } 
    } 
    errors.log("Errors while parsing configuration:");
    DeathControl.instance.log(Level.CONFIG, "loaded " + Util.pluralNum(this.handlings.size(), "handling") + "!", true);
  }
  
  public boolean usesBukkitPerms() {
    return this.bukkitPerms;
  }
  
  public int getLoggingLevel() {
    return this.loggingLevel;
  }
  
  public boolean allowsCrossworld() {
    return this.allowCrossworld;
  }
  
  public boolean isWorldAllowed(String worldName) {
    if (this.blacklistedWorlds == null || this.blacklistedWorlds.isEmpty())
      return true; 
    return !this.blacklistedWorlds.contains(worldName);
  }
  
  public Collection<String> getBlacklistedWorlds() {
    return this.blacklistedWorlds;
  }
  
  public HandlingDescriptor getHandling(String name) {
    for (HandlingDescriptor h : this.handlings) {
      if (h.getName().equalsIgnoreCase(name))
        return h; 
    } 
    return null;
  }
  
  public Set<HandlingDescriptor> getHandlings() {
    return this.handlings;
  }
  
  public <T extends Collection<String>> T getPartialHandlingNames(String search, T result) {
    for (HandlingDescriptor h : this.handlings) {
      if (StringUtil.startsWithIgnoreCase(h.getName(), search))
        result.add(h.getName()); 
    } 
    return result;
  }
}
