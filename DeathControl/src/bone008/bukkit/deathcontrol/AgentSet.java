package bone008.bukkit.deathcontrol;

import bone008.bukkit.deathcontrol.config.ActionAgent;
import bone008.bukkit.deathcontrol.config.ActionResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AgentSet implements Iterable<ActionAgent> {
  private boolean sealed = false;
  
  private List<ActionAgent> list = new ArrayList<>();
  
  public void add(ActionAgent agent) {
    if (this.sealed)
      throw new IllegalStateException("can't add agent to sealed set"); 
    this.list.add(agent);
  }
  
  public boolean isEmpty() {
    return this.list.isEmpty();
  }
  
  public int size() {
    return this.list.size();
  }
  
  public Iterator<ActionAgent> iterator() {
    return this.list.iterator();
  }
  
  public AgentIterator iteratorExecution() {
    return new AgentIterator();
  }
  
  public void seal() {
    this.sealed = true;
  }
  
  public class AgentIterator implements Iterator<ActionAgent> {
    private ActionResult blockedReason = null;
    
    private int nextIndex = 0;
    
    public boolean canContinue() {
      return (this.blockedReason == null && hasNext());
    }
    
    public boolean hasNext() {
      return (this.nextIndex < AgentSet.this.list.size());
    }
    
    public ActionAgent next() {
      return AgentSet.this.list.get(this.nextIndex++);
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
    
    public void blockExecution(ActionResult reason) {
      this.blockedReason = reason;
    }
    
    public boolean unblockExecution(ActionResult reason) {
      if (this.blockedReason == reason) {
        this.blockedReason = null;
        return true;
      } 
      return false;
    }
  }
}
