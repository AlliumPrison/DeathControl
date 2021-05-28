package bone008.bukkit.deathcontrol.config.lists;

import bone008.bukkit.deathcontrol.exceptions.FormatException;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BasicListItem extends ListItem {
  private Material type;
  
  private int data = 0;
  
  private boolean hasData = false;
  
  public BasicListItem(Material mat, Integer data) {
    this.type = mat;
    if (data != null) {
      this.data = data.intValue();
      this.hasData = true;
    } 
  }
  
  public boolean matches(ItemStack itemStack) {
    if (itemStack.getType() == this.type) {
      if (this.hasData)
        return (this.data == itemStack.getDurability()); 
      return true;
    } 
    return false;
  }
  
  public String toString() {
    return "BasicListItem@" + this.type + ":" + this.data + "/" + this.hasData;
  }
  
  public CharSequence toHumanString() {
    return String.valueOf(this.type.toString()) + ChatColor.ITALIC + '#' + this.type.getId() + (this.hasData ? (":" + this.data) : "");
  }
  
  public static BasicListItem parse(String input) throws FormatException {
    List<String> tokens = Util.tokenize(input, ":", true);
    if (tokens.size() > 2 || tokens.size() < 1)
      throw new FormatException("invalid formatting of item '" + input + "'"); 
    Material mat = null;
    mat = Material.getMaterial(tokens.get(0));
    if (mat == null)
      throw new FormatException("could not find material '" + (String)tokens.get(0) + "'"); 
    Integer data = null;
    try {
      if (tokens.size() == 2)
        data = Integer.valueOf(Integer.parseInt(tokens.get(1))); 
      BasicListItem item = new BasicListItem(mat, data);
      return item;
    } catch (NumberFormatException e) {
      throw new FormatException("data value '" + (String)tokens.get(1) + "' must be a number!");
    } 
  }
  
  public static int compare(BasicListItem o1, BasicListItem o2) {
    if (o1.type == o2.type) {
      if (o1.hasData) {
        if (o2.hasData)
          return o1.data - o2.data; 
        return 1;
      } 
      if (o2.hasData) {
        if (o1.hasData)
          return o1.data - o2.data; 
        return -1;
      } 
      return 0;
    } 
    return o1.type.compareTo((Enum)o2.type);
  }
}
