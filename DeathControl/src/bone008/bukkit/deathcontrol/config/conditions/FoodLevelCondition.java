package bone008.bukkit.deathcontrol.config.conditions;

import bone008.bukkit.deathcontrol.Operator;
import bone008.bukkit.deathcontrol.config.ConditionDescriptor;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import bone008.bukkit.deathcontrol.util.ParserUtil;
import java.util.Arrays;
import java.util.List;

public class FoodLevelCondition extends ConditionDescriptor {
  private final Operator operator;
  
  private final int number;
  
  public FoodLevelCondition(List<String> args) throws DescriptorFormatException {
    if (args.size() != 2)
      throw new DescriptorFormatException("exactly 2 arguments needed"); 
    this.operator = Operator.parse(args.get(0));
    this.number = ParserUtil.parseInt(args.get(1));
    if (this.operator == null)
      throw new DescriptorFormatException("invalid operator \"" + (String)args.get(0) + "\"!"); 
    if (this.number < 0 || this.number > 20)
      throw new DescriptorFormatException("invalid food level \"" + (String)args.get(1) + "\": only numbers between 0 and 20 are allowed!"); 
  }
  
  public boolean matches(DeathContext context) {
    return this.operator.invokeInt(context.getVictim().getPlayer().getFoodLevel(), this.number);
  }
  
  public List<String> toParameters() {
    return Arrays.asList(new String[] { this.operator.getPrimaryIdentifier(), (new StringBuilder(String.valueOf(this.number))).toString() });
  }
}
