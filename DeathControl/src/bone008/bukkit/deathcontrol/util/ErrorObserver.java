package bone008.bukkit.deathcontrol.util;

import bone008.bukkit.deathcontrol.DeathControl;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ErrorObserver {
  private String prefix = null;
  
  private List<String> warnings = new ArrayList<>();
  
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
  
  public String getPrefix() {
    return this.prefix;
  }
  
  public void addWarning(String msg, Object... args) {
    this.warnings.add(parseMsg(msg, args));
  }
  
  public void log() {
    log(null);
  }
  
  public void log(String introduction) {
    if (this.warnings.isEmpty())
      return; 
    if (introduction != null)
      DeathControl.instance.log(Level.WARNING, introduction); 
    for (String msg : this.warnings)
      DeathControl.instance.log(Level.WARNING, msg); 
  }
  
  public void logTo(ErrorObserver target) {
    logTo(target, null);
  }
  
  public void logTo(ErrorObserver target, String introduction) {
    if (this.warnings.isEmpty())
      return; 
    if (introduction != null)
      target.addWarning(introduction, new Object[0]); 
    for (String msg : this.warnings)
      target.addWarning(msg, new Object[0]); 
  }
  
  private String parseMsg(String msg, Object[] args) {
    if (args.length > 0)
      msg = String.format(msg, args); 
    if (this.prefix != null)
      msg = String.valueOf(this.prefix) + msg; 
    return msg;
  }
}
