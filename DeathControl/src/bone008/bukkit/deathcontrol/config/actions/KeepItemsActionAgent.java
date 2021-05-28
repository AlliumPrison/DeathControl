package bone008.bukkit.deathcontrol.config.actions;

import bone008.bukkit.deathcontrol.StoredItemStack;
import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionResult;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class KeepItemsActionAgent extends ActionAgent {
  private final KeepItemsAction action;
  
  private List<StoredItemStack> keptItems = new ArrayList<>();
  
  public KeepItemsActionAgent(DeathContext context, KeepItemsAction action) {
    super(context, action);
    this.action = action;
  }
  
  public void preprocess() {
    this.action.applyActionToStacks(this.context.getItemDrops(), this.keptItems);
    int keptAmount = 0, droppedAmount = 0;
    for (StoredItemStack kept : this.keptItems)
      keptAmount += kept.itemStack.getAmount(); 
    for (StoredItemStack dropped : this.context.getItemDrops())
      droppedAmount += dropped.itemStack.getAmount(); 
    int totalAmount = keptAmount + droppedAmount;
    if (totalAmount == 0) {
      this.context.setVariable("items-kept-percent", "0%");
      this.context.setVariable("items-dropped-percent", "0%");
    } else {
      this.context.setVariable("items-kept-percent", String.format("%.0f%%", new Object[] { Double.valueOf(keptAmount * 100.0D / totalAmount) }));
      this.context.setVariable("items-dropped-percent", String.format("%.0f%%", new Object[] { Double.valueOf(droppedAmount * 100.0D / totalAmount) }));
    } 
  }
  
  public ActionResult execute() {
    if (this.keptItems.isEmpty())
      return ActionResult.FAILED; 
    Player victimPlayer = this.context.getVictim().getPlayer();
    if (victimPlayer == null) {
      cancel();
      return ActionResult.PLAYER_OFFLINE;
    } 
    PlayerInventory inv = victimPlayer.getInventory();
    for (StoredItemStack stored : this.keptItems) {
      if (inv.getItem(stored.slot) == null) {
        inv.setItem(stored.slot, stored.itemStack);
        continue;
      } 
      HashMap<Integer, ItemStack> leftovers = inv.addItem(new ItemStack[] { stored.itemStack });
      if (leftovers.size() > 0)
        Util.dropItems(victimPlayer.getLocation(), leftovers, false); 
    } 
    return null;
  }
  
  public void cancel() {
    for (StoredItemStack stored : this.keptItems)
      Util.dropItem(this.context.getDeathLocation(), stored.itemStack, true); 
  }
}
