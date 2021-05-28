package bone008.bukkit.deathcontrol;

import bone008.bukkit.deathcontrol.commandhandler.CommandHandler;
import bone008.bukkit.deathcontrol.commandhandler.SubCommand;
import bone008.bukkit.deathcontrol.commands.BackCommand;
import bone008.bukkit.deathcontrol.commands.CancelCommand;
import bone008.bukkit.deathcontrol.commands.ConfigCommand;
import bone008.bukkit.deathcontrol.commands.HelpCommand;
import bone008.bukkit.deathcontrol.commands.ReloadCommand;
import bone008.bukkit.deathcontrol.config.ItemLists;
import bone008.bukkit.deathcontrol.config.NewConfiguration;
import bone008.bukkit.deathcontrol.exceptions.ResourceNotFoundError;
import bone008.bukkit.deathcontrol.util.DPermission;
import bone008.bukkit.deathcontrol.util.EconomyUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathControl extends JavaPlugin {
  public static final DPermission PERMISSION_USE = new DPermission("deathcontrol.use", false);
  
  public static final DPermission PERMISSION_FREE = new DPermission("deathcontrol.free", true);
  
  public static final DPermission PERMISSION_CROSSWORLD = new DPermission("deathcontrol.crossworld", true);
  
  public static final DPermission PERMISSION_NOLIMITS = new DPermission("deathcontrol.nolimits", true);
  
  public static final DPermission PERMISSION_INFO = new DPermission("deathcontrol.info", true);
  
  public static final DPermission PERMISSION_ADMIN = new DPermission("deathcontrol.admin", true);
  
  public static DeathControl instance;
  
  private File messagesFile = null;
  
  public NewConfiguration config;
  
  public ItemLists itemLists;
  
  public YamlConfiguration messagesData;
  
  public PluginDescriptionFile pdfFile;
  
  private Map<UUID, DeathContextImpl> activeDeaths = new HashMap<>();
  
  public DeathControl() {
    instance = this;
  }
  
  public void onDisable() {
    if (!this.activeDeaths.isEmpty())
      for (DeathContextImpl context : new ArrayList(this.activeDeaths.values()))
        context.cancel();  
    instance = null;
  }
  
  public void onEnable() {
    this.messagesFile = new File(getDataFolder(), "messages.yml");
    this.pdfFile = getDescription();
    if (this.pdfFile.getVersion().toLowerCase().contains("dev"))
      getLogger().warning("Keep in mind you are running a developer version of DeathControl!"); 
    loadConfig();
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(new BukkitDeathHandler(), (Plugin)this);
    pm.registerEvents(new BukkitReconnectHandler(), (Plugin)this);
    pm.registerEvents(new BukkitRuleNotifHandler(), (Plugin)this);
    BukkitRuleNotifHandler.warnAll();
    CommandHandler deathCmd = new CommandHandler();
    deathCmd.addSubCommand("help", (SubCommand)new HelpCommand(), new String[] { "?" });
    deathCmd.addSubCommand("back", (SubCommand)new BackCommand(), new String[] { "restore" });
    deathCmd.addSubCommand("cancel", (SubCommand)new CancelCommand(), new String[] { "drop", "expire" });
    deathCmd.addSubCommand("reload", (SubCommand)new ReloadCommand(), new String[0]);
    deathCmd.addSubCommand("config", (SubCommand)new ConfigCommand(), new String[] { "info" });
    getCommand("death").setExecutor((CommandExecutor)deathCmd);
    EconomyUtil.init();
  }
  
  public void loadConfig() {
    writeDefault("config.yml", "config.yml", false);
    writeDefault("lists.txt", "lists.txt", false);
    writeDefault("messages.yml", "messages.yml", false);
    reloadConfig();
    checkConfigIntegrity();
    FileConfiguration cfg = getConfig();
    cfg.options().copyDefaults(true);
    cfg.options().copyHeader(true);
    this.itemLists = new ItemLists(this, new File(getDataFolder(), "lists.txt"));
    this.config = new NewConfiguration((Configuration)cfg);
    this.messagesData = YamlConfiguration.loadConfiguration(this.messagesFile);
    checkMessagesIntegrity();
    log(Level.CONFIG, "is now using " + (this.config.usesBukkitPerms() ? "bukkit permissions" : "the OP-system") + "!");
  }
  
  public boolean writeDefault(String resourceName, String destination, boolean force) {
    boolean ret = false;
    File file = new File(getDataFolder(), destination);
    if (!force && file.exists())
      return false; 
    InputStream in = getClass().getResourceAsStream("/resources/" + resourceName);
    if (in == null)
      throw new ResourceNotFoundError(resourceName); 
    FileOutputStream out = null;
    try {
      getDataFolder().mkdirs();
      file.delete();
      file.createNewFile();
      out = new FileOutputStream(file);
      byte[] buffer = new byte[8192];
      int remaining = 0;
      while ((remaining = in.read(buffer)) > 0)
        out.write(buffer, 0, remaining); 
      log(Level.INFO, "default file " + resourceName + " created/updated!", true);
      ret = true;
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (in != null)
          in.close(); 
      } catch (IOException iOException) {}
      try {
        if (out != null)
          out.close(); 
      } catch (IOException iOException) {}
    } 
    return ret;
  }
  
  private void checkConfigIntegrity() {
    FileConfiguration c = getConfig();
    if (c.isSet("DeathCauses") || c.isSet("use-bukkit-permissions") || c.isSet("multi-world.limited-worlds") || !c.isSet("handlings") || !c.isSet("multi-world.disabled-worlds") || !c.isSet("disable-permissions")) {
      log(Level.WARNING, "Your config.yml file is deprecated. It will now be updated.");
      File origFile = new File(getDataFolder(), "config.yml");
      File backupFile = new File(getDataFolder(), "config-old-backup.yml");
      backupFile.delete();
      if (origFile.renameTo(backupFile)) {
        log(Level.INFO, "Your old config was saved to \"config-old-backup.yml\"!");
      } else {
        log(Level.WARNING, "Unable to backup old config.yml file!");
      } 
      writeDefault("config.yml", "config.yml", true);
    } 
  }
  
  private void checkMessagesIntegrity() {
    InputStream messageDefaultsStream = getClass().getResourceAsStream("/resources/messages.yml");
    if (messageDefaultsStream == null)
      return; 
    boolean needsUpdate = false, needsBackup = false;
    YamlConfiguration defaultMessages = YamlConfiguration.loadConfiguration(new InputStreamReader(messageDefaultsStream));
    for (String msgKey : defaultMessages.getKeys(true)) {
      if (this.messagesData.isSet(msgKey)) {
        Object defaultVal = this.messagesData.get(msgKey);
        if (!(defaultVal instanceof org.bukkit.configuration.ConfigurationSection) && !defaultVal.equals(defaultMessages.get(msgKey)))
          needsBackup = true; 
        continue;
      } 
      needsUpdate = true;
    } 
    if (needsUpdate) {
      log(Level.WARNING, "Your messages.yml file is out of date. It will now be updated.");
      if (needsBackup) {
        log(Level.INFO, "Creating backup of your old messages ...");
        File backupFile = new File(getDataFolder(), "messages-old-backup.yml");
        if (!this.messagesFile.renameTo(backupFile)) {
          log(Level.WARNING, "Unable to backup old messages.yml file! Automatic updating failed!");
          return;
        } 
        log(Level.INFO, "Old messages have been backed up to " + backupFile.getPath());
      } 
      writeDefault("messages.yml", "messages.yml", true);
      this.messagesData = YamlConfiguration.loadConfiguration(this.messagesFile);
    } 
  }
  
  public void addActiveDeath(UUID playerUid, DeathContextImpl context) {
    this.activeDeaths.put(playerUid, context);
  }
  
  public DeathContextImpl getActiveDeath(UUID playerUid) {
    return this.activeDeaths.get(playerUid);
  }
  
  public void clearActiveDeath(UUID playerUid) {
    this.activeDeaths.remove(playerUid);
  }
  
  public boolean hasPermission(Permissible who, DPermission perm) {
    if (perm == null)
      return true; 
    if (who == null)
      return false; 
    if (this.config.usesBukkitPerms())
      return who.hasPermission(perm.node); 
    if (!perm.opOnly)
      return true; 
    if (who instanceof org.bukkit.permissions.ServerOperator)
      return who.isOp(); 
    log(Level.WARNING, "Could not check permission " + perm.node + " for " + who.toString() + ": unsupported type! Denying access ...");
    return false;
  }
  
  public void log(String msg) {
    log(Level.INFO, msg);
  }
  
  public void log(Level lvl, String msg) {
    log(lvl, msg, false);
  }
  
  public void log(Level lvl, String msg, boolean overrideLevel) {
    if (!overrideLevel && this.config != null && lvl.intValue() < this.config.getLoggingLevel())
      return; 
    if (lvl.intValue() < Level.INFO.intValue())
      lvl = Level.INFO; 
    String[] lines = msg.split("\n");
    byte b;
    int i;
    String[] arrayOfString1;
    for (i = (arrayOfString1 = lines).length, b = 0; b < i; ) {
      String line = arrayOfString1[b];
      getLogger().log(lvl, line);
      b++;
    } 
  }
}
