package bone008.bukkit.deathcontrol.util;

import java.util.Arrays;
import org.bukkit.entity.Player;

public final class ExperienceUtil {
  private static final int hardMaxLevel = 100000;
  
  private static int[] xpRequiredForNextLevel;
  
  private static int[] xpTotalToReachLevel;
  
  static {
    initLookupTables(25);
  }
  
  private static void initLookupTables(int maxLevel) {
    xpRequiredForNextLevel = new int[maxLevel];
    xpTotalToReachLevel = new int[maxLevel];
    xpTotalToReachLevel[0] = 0;
    int incr = 17;
    for (int i = 1; i < xpTotalToReachLevel.length; i++) {
      xpRequiredForNextLevel[i - 1] = incr;
      xpTotalToReachLevel[i] = xpTotalToReachLevel[i - 1] + incr;
      if (i >= 30) {
        incr += 7;
      } else if (i >= 16) {
        incr += 3;
      } 
    } 
    xpRequiredForNextLevel[xpRequiredForNextLevel.length - 1] = incr;
  }
  
  private static int calculateLevelForExp(int exp) {
    int level = 0;
    int curExp = 7;
    int incr = 10;
    while (curExp <= exp) {
      curExp += incr;
      level++;
      incr += (level % 2 == 0) ? 3 : 4;
    } 
    return level;
  }
  
  public static void changeExp(Player player, int amt) {
    setExp(player, getCurrentExp(player) + amt);
  }
  
  public static void setExp(Player player, int xp) {
    if (xp < 0)
      xp = 0; 
    int curLvl = player.getLevel();
    int newLvl = getLevelForExp(xp);
    if (curLvl != newLvl)
      player.setLevel(newLvl); 
    float pct = (xp - getXpForLevel(newLvl)) / xpRequiredForNextLevel[newLvl];
    player.setExp(pct);
    player.setTotalExperience(xp);
  }
  
  public static int getCurrentExp(Player player) {
    int lvl = player.getLevel();
    int cur = getXpForLevel(lvl) + Math.round(xpRequiredForNextLevel[lvl] * player.getExp());
    return cur;
  }
  
  public static boolean hasExp(Player player, int amt) {
    return (getCurrentExp(player) >= amt);
  }
  
  public static int getLevelForExp(int exp) {
    if (exp <= 0)
      return 0; 
    if (exp > xpTotalToReachLevel[xpTotalToReachLevel.length - 1]) {
      int newMax = calculateLevelForExp(exp) * 2;
      if (newMax > 100000)
        throw new IllegalArgumentException("Level for exp " + exp + " > hard max level " + 100000); 
      initLookupTables(newMax);
    } 
    int pos = Arrays.binarySearch(xpTotalToReachLevel, exp);
    return (pos < 0) ? (-pos - 2) : pos;
  }
  
  public static int getXpForLevel(int level) {
    if (level > 100000)
      throw new IllegalArgumentException("Level " + level + " > hard max level " + 100000); 
    if (level >= xpTotalToReachLevel.length)
      initLookupTables(level * 2); 
    return xpTotalToReachLevel[level];
  }
}
