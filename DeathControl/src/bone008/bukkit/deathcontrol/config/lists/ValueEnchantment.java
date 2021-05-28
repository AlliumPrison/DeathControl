package bone008.bukkit.deathcontrol.config.lists;

import bone008.bukkit.deathcontrol.Operator;
import bone008.bukkit.deathcontrol.exceptions.ConditionFormatException;
import bone008.bukkit.deathcontrol.util.Util;
import java.util.List;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ValueEnchantment {
  private final Enchantment enchantment;
  
  private final int valueLevel;
  
  public static ValueEnchantment parseValue(String rawValue) throws ConditionFormatException {
    List<String> enchTokens = Util.tokenize(rawValue, "\\.", false);
    if (enchTokens.size() > 2)
      throw new ConditionFormatException("invalid enchantment format: " + rawValue); 
    Enchantment enchantment = enchantmentByName(enchTokens.get(0));
    if (enchantment == null)
      throw new ConditionFormatException("unknown enchantment: " + (String)enchTokens.get(0)); 
    int level = -1;
    if (enchTokens.size() == 2)
      try {
        level = Integer.parseInt(enchTokens.get(1));
      } catch (NumberFormatException e) {
        throw new ConditionFormatException("enchantment level must be a number: " + (String)enchTokens.get(1));
      }  
    return new ValueEnchantment(enchantment, level);
  }
  
  private static Enchantment enchantmentByName(String name) {
    return Enchantment.getByName(name.toUpperCase().replace('-', '_'));
  }
  
  public ValueEnchantment(Enchantment enchantment, int level) {
    this.enchantment = enchantment;
    this.valueLevel = level;
  }
  
  public ValueEnchantment validateOperator(Operator operator) throws ConditionFormatException {
    if (this.valueLevel < 1 && operator != Operator.EQUAL && operator != Operator.UNEQUAL)
      throw new ConditionFormatException("incompatible type for operator '" + operator.getPrimaryIdentifier() + "': unleveled enchantment"); 
    return this;
  }
  
  public boolean invokeOperator(ItemStack itemStack, Operator operator) {
    int currLevel = itemStack.getEnchantmentLevel(this.enchantment);
    if (this.valueLevel > 0)
      return operator.invokeInt(currLevel, this.valueLevel); 
    return (((currLevel > 0) ? true : false) == ((operator == Operator.EQUAL) ? true : false));
  }
  
  public String toString() {
    return String.valueOf(this.enchantment.getName()) + ((this.valueLevel > 0) ? ("." + this.valueLevel) : "");
  }
}
