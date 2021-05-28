package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.StoredItemStack;
import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionResult;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class DamageItemsActionAgent extends ActionAgent {
  private final DamageItemsAction action;
  
  private static class ItemStackEntry {
    final ItemStack stack;
    
    final short durabilityChanged;
    
    public ItemStackEntry(ItemStack stack, short durabilityChanged) {
      this.stack = stack;
      this.durabilityChanged = durabilityChanged;
    }
  }
  
  private List<ItemStackEntry> damagedStacks = new ArrayList<>();
  
  private List<ItemStack> removedStacks = new ArrayList<>();
  
  public DamageItemsActionAgent(DeathContext context, DamageItemsAction action) {
    super(context, action);
    this.action = action;
  }
  
  public void preprocess() {
    for (Iterator<StoredItemStack> it = this.context.getItemDrops().iterator(); it.hasNext(); ) {
      StoredItemStack dropped = it.next();
      int maxDurability = dropped.itemStack.getType().getMaxDurability();
      if (maxDurability > 0 && this.action.isValidItem(dropped.itemStack)) {
        int usesLeft = maxDurability - dropped.itemStack.getDurability();
        int newUsesLeft = this.action.calculatePercentageAmount(usesLeft, 1.0D - this.action.damagePct);
        if (newUsesLeft > 0) {
          dropped.itemStack.setDurability((short)(maxDurability - newUsesLeft));
          this.damagedStacks.add(new ItemStackEntry(dropped.itemStack, (short)(usesLeft - newUsesLeft)));
          continue;
        } 
        it.remove();
        this.removedStacks.add(dropped.itemStack);
      } 
    } 
    this.context.setVariable("items-damaged", Integer.valueOf(((Integer)this.context.getVariable("items-damaged")).intValue() + this.damagedStacks.size() + this.removedStacks.size()));
  }
  
  public ActionResult execute() {
    return (this.damagedStacks.isEmpty() && this.removedStacks.isEmpty()) ? ActionResult.FAILED : ActionResult.STANDARD;
  }
  
  public void cancel() {
    for (ItemStackEntry e : this.damagedStacks)
      e.stack.setDurability((short)(e.stack.getDurability() - e.durabilityChanged)); 
    Util.dropItems(this.context.getDeathLocation(), this.removedStacks, true);
  }
}
