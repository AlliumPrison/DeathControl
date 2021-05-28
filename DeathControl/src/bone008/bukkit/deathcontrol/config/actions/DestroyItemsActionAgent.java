package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.StoredItemStack;
import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionResult;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class DestroyItemsActionAgent extends ActionAgent {
  private final DestroyItemsAction action;
  
  private List<ItemStack> destroyedStacks = new ArrayList<>();
  
  public DestroyItemsActionAgent(DeathContext context, DestroyItemsAction action) {
    super(context, action);
    this.action = action;
  }
  
  public void preprocess() {
    List<StoredItemStack> destroyed = new ArrayList<>();
    this.action.applyActionToStacks(this.context.getItemDrops(), destroyed);
    for (StoredItemStack stored : destroyed)
      this.destroyedStacks.add(stored.itemStack); 
    int destroyedAmount = 0, droppedAmount = 0;
    for (ItemStack d : this.destroyedStacks)
      destroyedAmount += d.getAmount(); 
    for (StoredItemStack dropped : this.context.getItemDrops())
      droppedAmount += dropped.itemStack.getAmount(); 
    int totalAmount = destroyedAmount + droppedAmount;
    this.context.setVariable("items-destroyed-percent", String.format("%.0f%%", new Object[] { Double.valueOf((totalAmount == 0) ? 0.0D : (droppedAmount * 100.0D / totalAmount)) }));
  }
  
  public ActionResult execute() {
    return this.destroyedStacks.isEmpty() ? ActionResult.FAILED : ActionResult.STANDARD;
  }
  
  public void cancel() {
    Util.dropItems(this.context.getDeathLocation(), this.destroyedStacks, true);
  }
}
