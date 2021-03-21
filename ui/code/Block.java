package ui.code;

import java.util.LinkedList;

public abstract class Block {

  private static final String[]	blockKeywords = {"LOOP", "TIMES", "IF"};
  private static final String		endKeyword = "END";

  public abstract boolean hasNextCommand (GameData data) ;
  public abstract Command nextCommand (GameData data) ;
  public abstract void reset () ;
  public abstract int getLastLineNum () ;

  public static Block makeBlock (String code) {
    return makeBlock(code.split("\n"), 0);
  }

  public static Block makeBlock (String[] codeLines, int beginLine) throws UBotSyntaxError {
    if (codeLines.length == 0)
      return new EmptyBlock(0);
    int numOpening = 0;
    int numClosing = 0;
    for (String line : codeLines) {
      if (hasBlockKeyword(line)) numOpening++;
      if (hasEndKeyword(line)) numClosing++;
    }
    if (numOpening != numClosing) {
      throw new UBotSyntaxError("Number of ENDs does not match number of blocks!");
    }
    
    LinkedList<String> cumCode = new LinkedList<String>();
    LinkedList<Block> blocks = new LinkedList<Block>();
    int cumCodeStart = beginLine;
    for (int i = 0; i < codeLines.length; i++) {
      if (hasBlockKeyword(codeLines[i])) {
        if (cumCode.size() > 0)
          blocks.add(new RegularBlock(cumCode.toArray(new String[cumCode.size()]), cumCodeStart));
        cumCode = new LinkedList<String>();
        String blockHead = codeLines[i].trim();
        LinkedList<String> enclosedCode = new LinkedList<String>();
        int stackCount = 1;
        int j = i;
        while (stackCount > 0 && j < codeLines.length) {
          j++;
          enclosedCode.add(codeLines[j-1]);
          if (hasBlockKeyword(codeLines[j])) {
            stackCount++;
          } else if (hasEndKeyword(codeLines[j])) {
            stackCount--;
          }
        }
        enclosedCode.remove(); // The first element will be the original block keyword.
        Block enclosedBlock = makeBlock(enclosedCode.toArray(new String[enclosedCode.size()]), beginLine+i+1);
        if (blockHead.equals("LOOP")) {
          blocks.add(new LoopBlock(enclosedBlock));
        } else if (blockHead.endsWith(" TIMES")) {
          int times = getTimes(blockHead);
          blocks.add(new TimesBlock(enclosedBlock, times));
        } else if (blockHead.startsWith("IF ")) {
          Condition cond = Condition.makeCondition(blockHead.substring(3).trim());
          blocks.add(new IfBlock(enclosedBlock, cond));
        } else {
          blocks.add(enclosedBlock);
        }
        i = j;
        cumCodeStart = beginLine + i + 1;
      } else if (!hasEndKeyword(codeLines[i])) {
        cumCode.add(codeLines[i]);
      } else {
        cumCode.add(codeLines[i]);
      }
    }
    if (cumCode.size() > 0)
      blocks.add(new RegularBlock(cumCode.toArray(new String[cumCode.size()]), cumCodeStart));
    if (blocks.size() == 1)
      return blocks.element();
    return new EncasingBlock(blocks.toArray(new Block[blocks.size()]));
  }

  public static boolean hasBlockKeyword (String str) {
    for (String s : blockKeywords) {
      if (str.contains(s)) return true;
    }
    return false;
  }

  public static boolean hasEndKeyword (String str) {
    return str.contains(endKeyword);
  }
  
  public static Block makeEmptyBlock () {
    return new EmptyBlock(0);
  }

  private static int getTimes (String line) {
    line = line.trim();
    int index = line.indexOf(" TIMES");
    try {
      return Integer.parseInt(line.substring(0, index));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

}


// This Block does nothing at all. It contains no code. It is empty.
class EmptyBlock extends Block {
  private int line;
  public EmptyBlock (int line) {
    this.line = line;
  }
  public boolean hasNextCommand (GameData data) { return false; }
  public Command nextCommand (GameData data)    { return null; }
  public void reset () {}
  public int getLastLineNum () { return line; }
  public String toString () { return "EmptyBlock"; }
}


// This Block does nothing special. It just runs the code in order, then is done.
class RegularBlock extends Block {

  private int		firstLine;
  private String[]	codeLines;
  private int		lineNum = 0, lastLineNum = 0;
  private boolean	done = false;

  public RegularBlock (String[] codeLines, int firstLine) {
    this.codeLines = codeLines;
    this.firstLine = firstLine;
  }

  public RegularBlock (String code, int firstLine) {
    this(code.split("\n"), firstLine);
  }

  public boolean hasNextCommand (GameData data) {
    updateLineNum();
    return !done;
  }

  public Command nextCommand (GameData data) {
    Command command = new Command(codeLines[lineNum]);
    lastLineNum = lineNum;
    lineNum++;
    updateLineNum();
    return command;
  }

  public void reset () {
    lineNum = 0;
    lastLineNum = 0;
    done = false;
  }

  public int getLastLineNum () {
    return firstLine + lastLineNum;
  }

  private void updateLineNum () {
    if (!done && lineNum < codeLines.length) {
      if (!Command.isCommand(codeLines[lineNum])) {
        if (lineNum+1 < codeLines.length) {
          lineNum++;
          updateLineNum();
        } else {
          done = true;
        }
      }
    } else if (lineNum >= codeLines.length) {
      done = true;
      lineNum = codeLines.length - 1;
    }
  }
  
  public String toString () {
    String result = "RegBlock-|";
    for (String s : codeLines) {
      result += s.trim() + "--";
    }
    result += "|RB";
    return result;
  }

}


// This Block can only hold other Blocks. It performs them in sequential order.
class EncasingBlock extends Block {

  private Block[]	blocks;
  private int		blockIndex = 0;
  private boolean	done = false;

  public EncasingBlock (Block[] blocks) {
    this.blocks = blocks;
  }

  public boolean hasNextCommand (GameData data) {
    updateBlockIndex(data);
    return !done;
  }

  public Command nextCommand (GameData data) {
    updateBlockIndex(data);
    return blocks[blockIndex].nextCommand(data);
  }

  public void reset () {
    blockIndex = 0;
    done = false;
    for (Block b : blocks) {
      b.reset();
    }
  }

  public int getLastLineNum () {
    return blocks[blockIndex].getLastLineNum();
  }

  private void updateBlockIndex (GameData data) {
    if (!done) {
      if (!blocks[blockIndex].hasNextCommand(data)) {
        if (blockIndex+1 < blocks.length) {
          blockIndex++;
          updateBlockIndex(data);
        } else {
          done = true;
        }
      }    
    }
  }

  public String toString () {
    String result = "EncasingBlock~/";
    for (Block b : blocks) {
      result += b.toString() + "__";
    }
    result += "/EB";
    return result;
  }

}


// This Block is an infinite loop. It holds one Block and repeats it forever.
class LoopBlock extends Block {

  private Block block;

  public LoopBlock (Block block) {
    this.block = block;
  }

  public boolean hasNextCommand (GameData data) {
    return true;
  }

  public Command nextCommand (GameData data) {
    if (!block.hasNextCommand(data)) {
      block.reset();
    }
    return block.nextCommand(data);
  }

  public void reset () {
    block.reset();
  }

  public int getLastLineNum () {
    return block.getLastLineNum();
  }

  public String toString () {
    return "LoopBlock~/" + block.toString() + "/LB";
  }

}


// This Block is a Times loop that holds one Block and repeats it a certain number of times.
class TimesBlock extends Block {

  private int totalTimes, numTimes = 0;
  private Block block;
  private boolean done = false;

  public TimesBlock (Block block, int times) {
    this.block = block;
    totalTimes = times;
  }

  public boolean hasNextCommand (GameData data) {
    updateNumTimes(data);
    return !done;
  }

  public Command nextCommand (GameData data) {
    updateNumTimes(data);
    return block.nextCommand(data);
  }

  public void reset () {
    numTimes = 0;
    done = false;
    block.reset();
  }

  public int getLastLineNum () {
    return block.getLastLineNum();
  }

  private void updateNumTimes (GameData data) {
    if (!done) {
      if (!block.hasNextCommand(data)) {
        if (numTimes+1 < totalTimes) {
          numTimes++;
          block.reset();
          updateNumTimes(data);
        } else {
          done = true;
        }
      }
    }
  }
  
  public String toString () {
    return "TimesBlock" + totalTimes + "~/" + block.toString() + "/TB";
  }

}


// This Block is an if statement that takes a condition as a parameter and runs the block when the condition is true.
class IfBlock extends Block {

  private Condition condition;
  private Block block;
  private boolean needsEval = true;
  private boolean evalResult;
  
  public IfBlock (Block block, Condition condition) {
    this.block = block;
    this.condition = condition;
  }
  
  public boolean hasNextCommand (GameData data) {
    if (needsEval) {
      eval(data);
      needsEval = false;
    }
    if (evalResult)
      return block.hasNextCommand(data);
    else
      return false;
  }
  
  public Command nextCommand (GameData data) {
    if (needsEval) {
      eval(data);
      needsEval = false;
    }
    if (evalResult)
      return block.nextCommand(data);
    else
      return null;
  }
  
  public void reset () {
    needsEval = true;
    block.reset();
  }
  
  public int getLastLineNum () {
    return block.getLastLineNum();
  }
  
  private void eval (GameData data) {
    evalResult = condition.evaluate(data);
  }
  
  public String toString () {
    return "IfBlock(" + condition.toString() + ")~/" + block.toString() + "/IB";
  }
}
