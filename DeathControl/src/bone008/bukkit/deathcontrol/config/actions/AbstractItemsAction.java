package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.StoredItemStack;
import bone008.bukkit.deathcontrol.config.ActionDescriptor;
import bone008.bukkit.deathcontrol.config.lists.ListItem;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import bone008.bukkit.deathcontrol.util.ParserUtil;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractItemsAction extends ActionDescriptor {
  private String filterName = null;
  
  private List<ListItem> itemsFilter = null;
  
  private boolean filterInverted = false;
  
  private double affectedPct = 1.0D;
  
  private boolean percentageAllowed = false;
  
  protected void parseFilter(List<String> args, boolean allowPercentage) throws DescriptorFormatException {
    this.percentageAllowed = allowPercentage;
    String itemsInput = null;
    if (allowPercentage) {
      switch (args.size()) {
        case 2:
          if (tryParsePct(args.get(1))) {
            itemsInput = args.get(0);
            break;
          } 
          if (tryParsePct(args.get(0))) {
            itemsInput = args.get(1);
            break;
          } 
          throw new DescriptorFormatException("invalid percentage: " + (String)args.get(1));
        case 1:
          if (!tryParsePct(args.get(0))) {
            this.affectedPct = 1.0D;
            itemsInput = args.get(0);
          } 
          break;
        case 0:
          break;
        default:
          throw new DescriptorFormatException("too many arguments");
      } 
    } else if (!args.isEmpty()) {
      itemsInput = args.get(0);
    } 
    if (itemsInput != null) {
      if (itemsInput.startsWith("!")) {
        this.filterInverted = true;
        itemsInput = itemsInput.substring(1);
      } 
      this.itemsFilter = DeathControl.instance.itemLists.getList(itemsInput);
      if (this.itemsFilter == null)
        throw new DescriptorFormatException("invalid item list: " + itemsInput); 
      this.filterName = itemsInput;
    } 
  }
  
  private boolean tryParsePct(String input) throws DescriptorFormatException {
    this.affectedPct = ParserUtil.parsePercentage(input);
    if (this.affectedPct == -1.0D)
      return false; 
    if (this.affectedPct > 1.0D)
      throw new DescriptorFormatException("invalid percentage: " + input); 
    return true;
  }
  
  public boolean isValidItem(ItemStack itemStack) {
    if (this.itemsFilter == null)
      return true; 
    boolean contained = false;
    for (ListItem item : this.itemsFilter) {
      if (item.matches(itemStack)) {
        contained = true;
        break;
      } 
    } 
    return contained ^ this.filterInverted;
  }
  
  public int calculateAffectedAmount(int oldAmount) {
    return calculatePercentageAmount(oldAmount, this.affectedPct);
  }
  
  public int calculatePercentageAmount(int oldAmount, double percentage) {
    double newAmount = oldAmount * percentage;
    int intAmount = (int)newAmount;
    if (newAmount > intAmount && Util.getRandom().nextDouble() < newAmount - intAmount)
      intAmount++; 
    return intAmount;
  }
  
  public void applyActionToStacks(Collection<StoredItemStack> all, Collection<StoredItemStack> affected) {
    Iterator<StoredItemStack> it = all.iterator();
    while (it.hasNext()) {
      StoredItemStack current = it.next();
      if (!isValidItem(current.itemStack))
        continue; 
      int keptAmount = calculateAffectedAmount(current.itemStack.getAmount());
      if (keptAmount == current.itemStack.getAmount()) {
        affected.add(current);
        it.remove();
        continue;
      } 
      if (keptAmount > 0) {
        StoredItemStack kept = current.clone();
        kept.itemStack.setAmount(keptAmount);
        affected.add(kept);
        current.itemStack.setAmount(current.itemStack.getAmount() - keptAmount);
      } 
    } 
  }
  
  public List<String> toParameters() {
    List<String> ret = new ArrayList<>();
    if (this.filterName != null)
      ret.add(this.filterName); 
    if (this.percentageAllowed)
      ret.add(String.format("%.0f%%", new Object[] { Double.valueOf(this.affectedPct * 100.0D) })); 
    return ret;
  }
}
