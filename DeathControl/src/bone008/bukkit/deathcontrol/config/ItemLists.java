package bone008.bukkit.deathcontrol.config;

import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.config.lists.ListItem;
import bone008.bukkit.deathcontrol.config.lists.ListsParser;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemLists {
  private final Map<String, List<ListItem>> lists;
  
  public ItemLists(DeathControl plugin, File f) {
    this.lists = (new ListsParser(f)).parse();
  }
  
  public List<ListItem> getList(String name) {
    return this.lists.get(name);
  }
  
  public Set<String> getListNames() {
    return this.lists.keySet();
  }
  
  public int getListsAmount() {
    return this.lists.size();
  }
}
