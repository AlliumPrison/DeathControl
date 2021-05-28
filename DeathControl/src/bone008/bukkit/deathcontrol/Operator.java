package bone008.bukkit.deathcontrol;

import java.util.Arrays;
import java.util.List;

public enum Operator {
  LESS(new String[] { "<" }),
  LEQUAL(new String[] { "<=" }),
  GEQUAL(new String[] { ">=" }),
  GREATER(new String[] { ">" }),
  EQUAL(new String[] { "=", "==" }),
  UNEQUAL(new String[] { "!=", "<>" });
  
  private final List<String> identifiers;
  
  Operator(String... identifiers) {
    this.identifiers = Arrays.asList(identifiers);
  }
  
  public boolean invokeInt(int left, int right) {
    switch (this) {
      case LESS:
        return (left < right);
      case LEQUAL:
        return (left <= right);
      case GEQUAL:
        return (left >= right);
      case GREATER:
        return (left > right);
      case null:
        return (left == right);
      case UNEQUAL:
        return (left != right);
    } 
    throw new Error();
  }
  
  public boolean invokeString(String left, String right) {
    switch (this) {
      case LESS:
        return (right.contains(left) && !left.equals(right));
      case LEQUAL:
        return right.contains(left);
      case GEQUAL:
        return left.contains(right);
      case GREATER:
        return (left.contains(right) && !left.equals(right));
      case null:
        return left.equals(right);
      case UNEQUAL:
        return !left.equals(right);
    } 
    throw new Error();
  }
  
  public String getPrimaryIdentifier() {
    return this.identifiers.get(0);
  }
  
  public static Operator parse(String input) {
    byte b;
    int i;
    Operator[] arrayOfOperator;
    for (i = (arrayOfOperator = values()).length, b = 0; b < i; ) {
      Operator o = arrayOfOperator[b];
      if (o.identifiers.contains(input))
        return o; 
      b++;
    } 
    return null;
  }
}
