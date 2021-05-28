package bone008.bukkit.deathcontrol;

import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionResult;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.util.EconomyUtil;
import bone008.bukkit.deathcontrol.util.Message;
import bone008.bukkit.deathcontrol.util.MessageUtil;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DeathContextImpl implements DeathContext {
  private PlayerDeathEvent deathEvent;
  
  private String victimName;
  
  private UUID victimUid;
  
  private Location deathLocation;
  
  private List<DeathCause> matchedDeathCauses;
  
  private List<StoredItemStack> itemDrops;
  
  private Map<String, Object> variables = new HashMap<>();
  
  private int disconnectTimeout = -1;
  
  private String cancelMessage = null;
  
  private AgentSet agents = new AgentSet();
  
  private AgentSet.AgentIterator executionIterator = null;
  
  private boolean tempBlocked = false;
  
  public DeathContextImpl(PlayerDeathEvent event) {
    Player victimp = event.getEntity();
    this.deathEvent = event;
    this.victimName = victimp.getName();
    this.victimUid = victimp.getUniqueId();
    this.deathLocation = victimp.getLocation();
    this.matchedDeathCauses = new ArrayList<>();
    byte b;
    int i;
    DeathCause[] arrayOfDeathCause;
    for (i = (arrayOfDeathCause = DeathCause.values()).length, b = 0; b < i; ) {
      DeathCause dc = arrayOfDeathCause[b];
      if (dc.appliesTo(victimp.getLastDamageCause()))
        this.matchedDeathCauses.add(dc); 
      b++;
    } 
    this.itemDrops = new ArrayList<>();
    PlayerInventory playerInv = victimp.getInventory();
    int invSize = playerInv.getSize() + (playerInv.getArmorContents()).length;
    for (int slot = 0; slot < invSize; slot++) {
      ItemStack item = playerInv.getItem(slot);
      if (item != null)
        this.itemDrops.add(new StoredItemStack(slot, item.clone())); 
    } 
    Player playerKiller = Util.getPlayerAttackerFromEvent(victimp.getLastDamageCause());
    setVariable("plugin-prefix", MessageUtil.getPluginPrefix(false));
    setVariable("death-cause", ((DeathCause)this.matchedDeathCauses.get(0)).toHumanString());
    setVariable("death-cause-formatted", Message.translatePath(((DeathCause)this.matchedDeathCauses.get(0)).toMsgPath()));
    setVariable("victim-name", victimp.getName());
    setVariable("victim-display-name", victimp.getDisplayName());
    setVariable("world", this.deathLocation.getWorld().getName());
    setVariable("killer-name", (playerKiller != null) ? playerKiller.getName() : "");
    setVariable("killer-display-name", (playerKiller != null) ? playerKiller.getDisplayName() : "");
    setVariable("death-message", this.deathEvent.getDeathMessage());
    setVariable("money-paid", EconomyUtil.formatMoney(0.0D));
    setVariable("money-paid-raw", Integer.valueOf(0));
    setVariable("items-kept-percent", "0%");
    setVariable("items-dropped-percent", "100%");
    setVariable("items-destroyed-percent", "0%");
    setVariable("last-command", "null");
    setVariable("items-damaged", Integer.valueOf(0));
  }
  
  public void assignAgent(ActionAgent agent) {
    this.agents.add(agent);
  }
  
  public void setDisconnectTimeout(int timeout) {
    this.disconnectTimeout = timeout;
  }
  
  public int getDisconnectTimeout() {
    return this.disconnectTimeout;
  }
  
  public void setCancelMessage(String cancelMessage) {
    if (cancelMessage != null)
      this.cancelMessage = cancelMessage; 
  }
  
  public String getCancelMessage() {
    return this.cancelMessage;
  }
  
  public boolean hasAgents() {
    return !this.agents.isEmpty();
  }
  
  public void preprocessAgents() {
    DeathControl.instance.log(Level.FINEST, "@" + this.victimName + ":  Preprocessing " + Util.pluralNum(this.agents.size(), "action") + " ...");
    for (ActionAgent agent : this.agents) {
      try {
        agent.preprocess();
      } catch (Throwable e) {
        DeathControl.instance.getLogger().log(Level.SEVERE, "Preprocessing action \"" + agent.getDescriptor().getName() + "\" caused an exception!", e);
      } 
    } 
  }
  
  public void executeAgents() {
    DeathControl.instance.log(Level.FINEST, "@" + this.victimName + ":  Starting execution of " + Util.pluralNum(this.agents.size(), "action") + " ...");
    this.agents.seal();
    this.executionIterator = this.agents.iteratorExecution();
    continueExecution(null);
  }
  
  public boolean isCancelled() {
    return (DeathControl.instance.getActiveDeath(this.victimUid) != this);
  }
  
  public boolean continueExecution(ActionResult reason) {
    if (this.tempBlocked)
      throw new IllegalStateException("can't continue execution from within an agent"); 
    if (isCancelled())
      return false; 
    if (this.executionIterator == null)
      throw new IllegalStateException("can't continue without having started"); 
    if (!this.executionIterator.unblockExecution(reason))
      return false; 
    while (this.executionIterator.canContinue()) {
      ActionResult result;
      ActionAgent agent = this.executionIterator.next();
      try {
        this.tempBlocked = true;
        result = agent.execute();
      } catch (Throwable e) {
        DeathControl.instance.getLogger().log(Level.SEVERE, "Executing action \"" + agent.getDescriptor().getName() + "\" caused an exception!", e);
        continue;
      } finally {
        this.tempBlocked = false;
      } 
      if (result == null)
        result = ActionResult.STANDARD; 
      DeathControl.instance.log(Level.FINEST, "@" + this.victimName + ":    " + agent.getDescriptor().getName() + " -> " + result);
      switch (result) {
        case STANDARD:
          continue;
        case PLAYER_OFFLINE:
          DeathControl.instance.log(Level.FINE, "@" + this.victimName + ":  Player was offline for action \"" + agent.getDescriptor().getName() + "\"!");
        case FAILED:
          if (agent.getDescriptor().isRequired()) {
            DeathControl.instance.log(Level.FINEST, "@" + this.victimName + ":  Cancelled because of action \"" + agent.getDescriptor().getName() + "\"!");
            cancel();
            return true;
          } 
          continue;
      } 
      this.executionIterator.blockExecution(result);
    } 
    if (!this.executionIterator.hasNext()) {
      DeathControl.instance.log(Level.FINEST, "@" + this.victimName + ":  All actions executed!");
      cancel();
    } 
    return true;
  }
  
  public void cancel() {
    doCancel(!(this.executionIterator != null && !this.executionIterator.hasNext()));
  }
  
  public void cancelManually() {
    doCancel(false);
    MessageUtil.sendMessage((CommandSender)getVictim().getPlayer(), Message.CMD_CANCELLED, new String[0]);
  }
  
  private void doCancel(boolean withMessage) {
    Iterator<ActionAgent> agentIt;
    if (this.tempBlocked)
      throw new IllegalStateException("can't cancel from within an agent"); 
    if (isCancelled())
      return; 
    DeathControl.instance.clearActiveDeath(this.victimUid);
    if (withMessage && this.cancelMessage != null && getVictim().isOnline())
      getVictim().getPlayer().sendMessage(replaceVariables(ChatColor.translateAlternateColorCodes('&', this.cancelMessage))); 
    if (this.executionIterator == null) {
      agentIt = this.agents.iterator();
    } else {
      agentIt = this.executionIterator;
    } 
    while (agentIt.hasNext()) {
      ActionAgent agent = agentIt.next();
      try {
        agent.cancel();
      } catch (Throwable e) {
        DeathControl.instance.getLogger().log(Level.SEVERE, "Cancelling action \"" + agent.getDescriptor().getName() + "\" caused an exception!", e);
      } 
    } 
  }
  
  public Location getDeathLocation() {
    return this.deathLocation;
  }
  
  public OfflinePlayer getVictim() {
    return Bukkit.getOfflinePlayer(this.victimUid);
  }
  
  public List<StoredItemStack> getItemDrops() {
    return this.itemDrops;
  }
  
  public PlayerDeathEvent getDeathEvent() {
    return this.deathEvent;
  }
  
  public Object getVariable(String name) {
    return this.variables.get(name.toLowerCase());
  }
  
  public void setVariable(String name, Object value) {
    if (value == null) {
      this.variables.remove(name.toLowerCase());
    } else {
      this.variables.put(name.toLowerCase(), value);
    } 
  }
  
  public String replaceVariables(CharSequence input) {
    if (input == null)
      return null; 
    String replaced = input.toString();
    for (Map.Entry<String, Object> var : this.variables.entrySet()) {
      String name = var.getKey();
      String value = var.getValue().toString();
      replaced = replaced.replace("%" + name + "%", value);
    } 
    return replaced;
  }
}
