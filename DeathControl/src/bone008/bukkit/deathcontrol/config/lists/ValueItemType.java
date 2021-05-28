package bone008.bukkit.deathcontrol.config.lists;

import bone008.bukkit.deathcontrol.Operator;
import bone008.bukkit.deathcontrol.exceptions.ConditionFormatException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ValueItemType {
  private final String humanName;
  
  private final Set<Material> materials;
  
  private enum Types {
    WEAPON((String)new Material[] { Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.BOW }),
    SWORD((String)new Material[] { Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD }),
    PICKAXE((String)new Material[] { Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE }),
    SHOVEL((String)new Material[] { Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL }),
    AXE((String)new Material[] { Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE }),
    HOE((String)new Material[] { Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE }),
    TOOL((String)new Material[] { 
        Material.SHEARS, Material.FLINT_AND_STEEL, Material.BUCKET, Material.COMPASS, Material.MAP, Material.CLOCK, Material.FISHING_ROD, Material.CARROT_ON_A_STICK, Material.WOODEN_SWORD, Material.STONE_SWORD, 
        Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, 
        Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.WOODEN_HOE, Material.STONE_HOE, 
        Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE }),
    HELMET((String)new Material[] { Material.LEATHER_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.CHAINMAIL_HELMET, Material.GOLDEN_HELMET }),
    CHESTPLATE((String)new Material[] { Material.LEATHER_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.GOLDEN_CHESTPLATE }),
    PANTS((String)new Material[] { Material.LEATHER_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.GOLDEN_LEGGINGS }),
    BOOTS((String)new Material[] { Material.LEATHER_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.CHAINMAIL_BOOTS, Material.GOLDEN_BOOTS }),
    ARMOR((String)new Material[] { 
        Material.LEATHER_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.CHAINMAIL_HELMET, Material.GOLDEN_HELMET, Material.LEATHER_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.GOLDEN_CHESTPLATE, 
        Material.LEATHER_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.LEATHER_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.CHAINMAIL_BOOTS, Material.GOLDEN_BOOTS });
    
    public final Set<Material> materials;
    
    Types(Material... mats) {
      this.materials = Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(mats)));
    }
    
    public static Types parse(String input) {
      try {
        return valueOf(input.toUpperCase());
      } catch (IllegalArgumentException e) {
        return null;
      } 
    }
  }
  
  public static ValueItemType parseValue(String rawValue) throws ConditionFormatException {
    String humanName;
    Set<Material> materials;
    Types parsedType = Types.parse(rawValue);
    if (parsedType == null) {
      Material mat = Material.matchMaterial(rawValue);
      if (mat == null)
        throw new ConditionFormatException("could not find material or type named '" + rawValue + "'"); 
      humanName = mat.toString().toLowerCase();
      materials = EnumSet.of(mat);
    } else {
      humanName = parsedType.toString().toLowerCase();
      materials = parsedType.materials;
    } 
    return new ValueItemType(humanName, materials);
  }
  
  public ValueItemType(String humanName, Set<Material> materials) {
    this.humanName = humanName;
    this.materials = materials;
  }
  
  public ValueItemType validateOperator(Operator operator) throws ConditionFormatException {
    if (operator != Operator.EQUAL && operator != Operator.UNEQUAL)
      throw new ConditionFormatException("incompatible type for operator '" + operator.getPrimaryIdentifier() + "': item type"); 
    return this;
  }
  
  public boolean invokeOperator(ItemStack itemStack, Operator operator) {
    switch (operator) {
      case null:
        return this.materials.contains(itemStack.getType());
      case UNEQUAL:
        return !this.materials.contains(itemStack.getType());
    } 
    throw new IllegalArgumentException("invalid operator for item type condition: " + operator.getPrimaryIdentifier());
  }
  
  public String toString() {
    return this.humanName;
  }
}
