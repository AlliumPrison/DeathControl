package bone008.bukkit.deathcontrol.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public final class Util {
  private static final Random rand = new Random();
  
  public static Player getPlayerAttackerFromEvent(EntityDamageEvent event) {
    Entity attacker = getAttackerFromEvent(event);
    if (attacker instanceof Player)
      return (Player)attacker; 
    return null;
  }
  
  public static Entity getAttackerFromEvent(EntityDamageEvent event) {
    if (!(event instanceof EntityDamageByEntityEvent))
      return null; 
    Entity damager = ((EntityDamageByEntityEvent)event).getDamager();
    if (damager instanceof Projectile && ((Projectile)damager).getShooter() instanceof Entity)
      damager = (Entity)((Projectile)damager).getShooter(); 
    return damager;
  }
  
  public static void dropItem(Location l, ItemStack i, boolean naturally) {
    if (l == null || i == null || i.getType() == Material.AIR || i.getAmount() < 1)
      return; 
    World w = l.getWorld();
    if (!w.isChunkLoaded(l.getChunk()))
      w.loadChunk(l.getChunk()); 
    if (naturally) {
      l.getWorld().dropItemNaturally(l, i);
    } else {
      l.getWorld().dropItem(l, i);
    } 
  }
  
  public static void dropItems(Location l, Iterable<ItemStack> items, boolean naturally) {
    if (items == null)
      return; 
    for (ItemStack i : items)
      dropItem(l, i, naturally); 
  }
  
  public static void dropItems(Location l, Map<?, ItemStack> items, boolean naturally) {
    if (items == null)
      return; 
    dropItems(l, items.values(), naturally);
  }
  
  public static void dropExp(Location l, int amount) {
    ExperienceOrb orb = (ExperienceOrb)l.getWorld().spawn(l, ExperienceOrb.class);
    orb.setExperience(amount);
  }
  
  public static CharSequence wrapPrefixed(String str, String prefix) {
    String[] lines = str.split("\n");
    StringBuilder output = new StringBuilder();
    for (int i = 0; i < lines.length; i++) {
      if (output.length() > 0)
        output.append('\n'); 
      output.append(prefix).append(lines[i]);
    } 
    return output;
  }
  
  public static String joinCollection(String delimiter, Collection<?> collection) {
    if (collection == null)
      return null; 
    StringBuilder ret = new StringBuilder();
    Iterator<?> it = collection.iterator();
    while (it.hasNext()) {
      ret.append(it.next());
      if (it.hasNext())
        ret.append(delimiter); 
    } 
    return ret.toString();
  }
  
  public static <T> T replaceValue(T obj, T search, T repl) {
    if (obj == null)
      return (search == null) ? repl : null; 
    if (obj.equals(search))
      return repl; 
    return obj;
  }
  
  public static List<String> tokenize(String input, String delimiterRegex, boolean allowEmpty) {
    String[] rawTokens = input.trim().split(delimiterRegex);
    List<String> tokens = new ArrayList<>(rawTokens.length);
    byte b;
    int i;
    String[] arrayOfString1;
    for (i = (arrayOfString1 = rawTokens).length, b = 0; b < i; ) {
      String token = arrayOfString1[b];
      token = token.trim();
      if (allowEmpty || !token.isEmpty())
        tokens.add(token); 
      b++;
    } 
    return tokens;
  }
  
  public static String pluralNum(int num, String word) {
    return String.valueOf(num) + " " + word + ((num == 1) ? "" : "s");
  }
  
  public static String pluralNum(int num, String word, String wordPlural) {
    return String.valueOf(num) + " " + ((num == 1) ? word : wordPlural);
  }
  
  public static Random getRandom() {
    return rand;
  }
}
