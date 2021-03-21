package ui;

import ui.code.*;
import java.util.ArrayList;

public class CodeManager {

  private String	code;
  private Block		block;

  public CodeManager () {
    this.block = Block.makeEmptyBlock();
  }

  public CodeManager (String code) throws UBotSyntaxError {
    this.code = code;
    block = Block.makeBlock(code);
    System.out.println("\n\n" + block.toString());
  }

  public int getLastLineNum () {
    return block.getLastLineNum();
  }

  public boolean hasNextCommand (GameData data) {
    return block.hasNextCommand(data);
  }

  public String nextCommand (GameData data) {
    String s = null;
    int count = 0;
    while (s == null && block.hasNextCommand(data)) {
      Command command = block.nextCommand(data);
      if (command != null)
        s = getCommand(command.getCommand());
      count++;
      if (count > 4) break;
    }
    return s;
  }

  private String getCommand (String line) {
    if (line.contains("R")) {
      return "r";
    } else if (line.contains("L")) {
      return "l";
    } else if (line.contains("U")) {
      return "u";
    } else if (line.contains("D")) {
      return "d";
    } else if (line.contains("W")) {
      return "w";
    }
    return null;
  }

}
