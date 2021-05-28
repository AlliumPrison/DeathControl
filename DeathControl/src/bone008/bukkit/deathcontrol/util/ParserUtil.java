package bone008.bukkit.deathcontrol.util;

import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtil {
  private static final Pattern REGEX_TIME_MINUTES = Pattern.compile("\\s*(\\d+)\\s*m\\s*", 2);
  
  private static final Pattern REGEX_TIME_SECONDS = Pattern.compile("\\s*(\\d+)\\s*s\\s*", 2);
  
  public static int parseLoggingLevel(String name) {
    if (name.equalsIgnoreCase("errors") || name.equalsIgnoreCase("error"))
      return Level.SEVERE.intValue(); 
    if (name.equalsIgnoreCase("warnings") || name.equalsIgnoreCase("warning"))
      return Level.WARNING.intValue(); 
    if (name.equalsIgnoreCase("standard") || name.equalsIgnoreCase("info") || name.equalsIgnoreCase("standart"))
      return Level.INFO.intValue(); 
    if (name.equalsIgnoreCase("detailed") || name.equalsIgnoreCase("detail"))
      return Level.FINE.intValue(); 
    if (name.equalsIgnoreCase("debug"))
      return Level.FINEST.intValue(); 
    return -1;
  }
  
  public static int parseTime(String input, int def) {
    if (input == null || input.isEmpty())
      return def; 
    Matcher matcher = REGEX_TIME_MINUTES.matcher(input);
    if (matcher.matches())
      return Integer.parseInt(matcher.group(1)) * 60; 
    matcher = REGEX_TIME_SECONDS.matcher(input);
    if (matcher.matches())
      return Integer.parseInt(matcher.group(1)); 
    try {
      return Integer.parseInt(input.trim());
    } catch (NumberFormatException e) {
      return def;
    } 
  }
  
  public static double parsePercentage(String input) {
    return parsePercentage(input, false);
  }
  
  public static double parsePercentage(String input, boolean forceExplicit) {
    try {
      int pctIndex = input.indexOf('%');
      if (forceExplicit && pctIndex == -1)
        return -1.0D; 
      double num = Double.parseDouble((pctIndex == -1) ? input : ((pctIndex == 0) ? input.substring(1) : input.substring(0, pctIndex)));
      if (num >= 0.0D) {
        if (pctIndex > -1)
          return num / 100.0D; 
        if (num <= 1.0D)
          return num; 
        return num / 100.0D;
      } 
    } catch (NumberFormatException numberFormatException) {}
    return -1.0D;
  }
  
  public static double parseDouble(String input) {
    try {
      double d = Double.parseDouble(input);
      if (d < 0.0D)
        d = -1.0D; 
      return d;
    } catch (NumberFormatException e) {
      return -1.0D;
    } 
  }
  
  public static int parseInt(String input) {
    try {
      int d = Integer.parseInt(input);
      if (d < 0)
        d = -1; 
      return d;
    } catch (NumberFormatException e) {
      return -1;
    } 
  }
  
  public static String parseOperationName(String input) {
    List<String> tokens = Util.tokenize(input, " ", false);
    if (tokens.isEmpty())
      return null; 
    return tokens.get(0);
  }
  
  public static List<String> parseOperationArgs(String input) {
    List<String> tokens = Util.tokenize(input, " ", false);
    if (!tokens.isEmpty())
      tokens.remove(0); 
    return tokens;
  }
}
