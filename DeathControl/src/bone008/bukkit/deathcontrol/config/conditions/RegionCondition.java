package bone008.bukkit.deathcontrol.config.conditions;

import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.config.ConditionDescriptor;
import bone008.bukkit.deathcontrol.config.DeathContext;
import bone008.bukkit.deathcontrol.exceptions.DescriptorFormatException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Location;

public class RegionCondition extends ConditionDescriptor {
  private String regionName;
  
  public RegionCondition(List<String> args) throws DescriptorFormatException {
    if (args.isEmpty())
      throw new DescriptorFormatException("no region given"); 
    this.regionName = args.get(0);
  }
  
  public boolean matches(DeathContext context) {
    WorldGuard wg = WorldGuard.getInstance();
    if (wg == null) {
      DeathControl.instance.log(Level.WARNING, "Region condition: WorldGuard is not installed on the server!");
      return false;
    } 
    Location deathLoc = context.getDeathLocation();
    RegionManager regionManager = wg.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(deathLoc.getWorld()));
    ProtectedRegion region = regionManager.getRegion(this.regionName);
    if (region == null) {
      DeathControl.instance.log(Level.FINE, "Region condition: WorldGuard region " + this.regionName + " unexistant in world " + deathLoc.getWorld().getName() + "!");
      return false;
    } 
    return region.contains(BlockVector3.at(deathLoc.getX(), deathLoc.getY(), deathLoc.getZ()));
  }
  
  public List<String> toParameters() {
    return Arrays.asList(new String[] { this.regionName });
  }
}
