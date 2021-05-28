package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.StoredItemStack;
import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionResult;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class DestroyItemActionAgent extends ActionAgent {
  private final DestroyItemAction action;
  
  private ActionResult result = null;
  
  private List<ItemStack> destroyedStacks = new ArrayList<>();
  
  public DestroyItemActionAgent(DeathContext context, DestroyItemAction action) {
    super(context, action);
    this.action = action;
  }
  
  public void preprocess() {
    Map<StoredItemStack, Integer> subtractStacks = new HashMap<>();
    int amountLeft = this.action.amount;
    for (StoredItemStack drop : this.context.getItemDrops()) {
      if (this.action.item.matches(drop.itemStack)) {
        int subtracted = Math.min(amountLeft, drop.itemStack.getAmount());
        subtractStacks.put(drop, Integer.valueOf(subtracted));
        amountLeft -= subtracted;
      } 
      if (amountLeft <= 0)
        break; 
    } 
    if (amountLeft > 0) {
      this.result = ActionResult.FAILED;
      return;
    } 
    for (Map.Entry<StoredItemStack, Integer> e : subtractStacks.entrySet()) {
      StoredItemStack stack = e.getKey();
      int amount = ((Integer)e.getValue()).intValue();
      if (amount >= stack.itemStack.getAmount()) {
        this.context.getItemDrops().remove(stack);
      } else {
        stack.itemStack.setAmount(stack.itemStack.getAmount() - amount);
      } 
      ItemStack destroyed = stack.itemStack.clone();
      destroyed.setAmount(amount);
      this.destroyedStacks.add(destroyed);
    } 
  }
  
  public ActionResult execute() {
    return this.result;
  }
  
  public void cancel() {
    Util.dropItems(this.context.getDeathLocation(), this.destroyedStacks, true);
  }
}
