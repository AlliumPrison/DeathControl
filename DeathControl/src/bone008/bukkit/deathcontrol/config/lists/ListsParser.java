package bone008.bukkit.deathcontrol.config.lists;

import bone008.bukkit.deathcontrol.DeathControl;
import bone008.bukkit.deathcontrol.exceptions.ConditionFormatException;
import bone008.bukkit.deathcontrol.exceptions.FormatException;
import bone008.bukkit.deathcontrol.util.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ListsParser {
  private static final String PREFIX_LIST = "$list ";
  
  private final File file;
  
  private BufferedReader reader = null;
  
  private List<ListItem> currentList = null;
  
  private String currentLine = null;
  
  private int currentNumLine = 0;
  
  private Map<String, List<ListItem>> parsedLists = new HashMap<>();
  
  public ListsParser(File file) {
    this.file = file;
  }
  
  public Map<String, List<ListItem>> parse() {
    try {
      this.reader = new BufferedReader(new FileReader(this.file));
      while (readValidLine()) {
        if (this.currentLine.startsWith("$list ")) {
          parseListStart();
          continue;
        } 
        parseListItem();
      } 
      DeathControl.instance.log(Level.CONFIG, "loaded " + this.parsedLists.size() + " list" + ((this.parsedLists.size() == 1) ? "" : "s") + "!", true);
    } catch (IOException e) {
      DeathControl.instance.log(Level.WARNING, "Could not load lists.txt!", true);
      e.printStackTrace();
    } finally {
      if (this.reader != null)
        try {
          this.reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }  
    } 
    return this.parsedLists;
  }
  
  private void parseListStart() {
    String listName = this.currentLine.substring("$list ".length()).toLowerCase().trim();
    if (listName.isEmpty()) {
      logLineWarning("found empty list name!");
    } else {
      this.currentList = new ArrayList<>();
      this.parsedLists.put(listName, this.currentList);
    } 
  }
  
  private void parseListItem() throws IOException {
    if (this.currentList == null) {
      logLineWarning("found item declaration before any list was specified!");
      return;
    } 
    if (this.currentLine.startsWith("{")) {
      parseSpecialItem();
      return;
    } 
    try {
      BasicListItem item = BasicListItem.parse(this.currentLine);
      this.currentList.add(item);
    } catch (FormatException e) {
      logLineWarning(e.getMessage());
    } 
  }
  
  private void parseSpecialItem() throws IOException {
    StringBuilder specialToken = new StringBuilder();
    specialToken.append(this.currentLine);
    int startLineNum = this.currentNumLine;
    while (specialToken.indexOf("}") == -1) {
      if (!readValidLine()) {
        logLineWarning("unterminated special item block starting at line " + startLineNum);
        return;
      } 
      specialToken.append(this.currentLine);
    } 
    int braceStart = specialToken.indexOf("{");
    int braceEnd = specialToken.indexOf("}");
    String specialItemArgs = specialToken.substring(braceStart + 1, braceEnd);
    SpecialListItem listItem = new SpecialListItem();
    try {
      for (String argToken : Util.tokenize(specialItemArgs, ",", false))
        listItem.parseCondition(argToken); 
    } catch (ConditionFormatException e) {
      logLineWarning("invalid condition: " + e.getMessage());
      return;
    } 
    this.currentList.add(listItem);
  }
  
  private boolean readValidLine() throws IOException {
    this.currentLine = this.reader.readLine();
    if (this.currentLine == null)
      return false; 
    this.currentNumLine++;
    int commentIndex = this.currentLine.indexOf('#');
    if (commentIndex > -1)
      this.currentLine = this.currentLine.substring(0, commentIndex); 
    this.currentLine = this.currentLine.trim();
    if (this.currentLine.isEmpty())
      return readValidLine(); 
    return true;
  }
  
  private void logLineWarning(String msg) {
    DeathControl.instance.log(Level.WARNING, String.format("lists.txt[%d]: %s", new Object[] { Integer.valueOf(this.currentNumLine), msg }), true);
  }
}
