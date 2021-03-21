package ui.code;

import java.awt.Point;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.LinkedList;

public abstract class Condition {

  public abstract boolean evaluate (GameData data) ;

  public static Condition makeCondition (String condition) throws UBotSyntaxError {
    condition = condition.trim();
    if (condition.contains("(") || condition.contains(")")) {
      int numOpening = 0;
      int numClosing = 0;
      for (int i = 0; i < condition.length(); i++) {
        char c = condition.charAt(i);
        if (c == '(')
          numOpening++;
        else if (c == ')')
          numClosing++;
      }
      if (numOpening != numClosing) {
        throw new UBotSyntaxError ("Mismatched parentheses in `"+condition+"`.");
      }
      int startIndex = condition.indexOf("(");
      String beginning = condition.substring(0, startIndex);
      int stackCount = 0;
      String enclosedCond = "";
      int i = startIndex;
      do {
        char c = condition.charAt(i);
        enclosedCond += String.valueOf(c);
        if (c == '(')
          stackCount++;
        else if (c == ')')
          stackCount--;
        i++;
      } while (stackCount > 0 && i < condition.length());
      enclosedCond = enclosedCond.substring(1, enclosedCond.length()-1);
      Condition enclosed = makeCondition(enclosedCond);
      String ending = condition.substring(i);
      if (beginning.equals("") && ending.equals("")) {
        return enclosed;
      } else if (!beginning.equals("")) {
        beginning = beginning.trim();
        Condition cond1 = null;
        String operator;
        String condString = null;
        int cutIndex = beginning.lastIndexOf(" ");
        if (cutIndex != -1) {
          operator = beginning.substring(cutIndex+1);
          condString = beginning.substring(0, cutIndex);
          while (operator.equals("NOT")) {
            enclosed = new NotCondition(enclosed);
            cutIndex = condString.trim().lastIndexOf(" ");
            if (cutIndex == -1) { // The operator must be NOT.
              enclosed = new NotCondition(enclosed);
              operator = "";
            } else {
              operator = condString.substring(cutIndex+1);
              condString = condString.substring(0, cutIndex);
            }
          }
          if (!operator.equals(""))
            cond1 = makeCondition(makeCondition(condString), operator, enclosed);
          else
            cond1 = enclosed;
        } else { // The operator must be NOT.
          enclosed = new NotCondition(enclosed);
          cond1 = enclosed;
        }
        if (ending.equals(""))
          return cond1;
        else {
          ending = ending.trim();
          cutIndex = ending.indexOf(" ");
          operator = ending.substring(0, cutIndex);
          condString = ending.substring(cutIndex+1);
          return makeCondition(cond1, operator, makeCondition(condString));
        }        
      } else {
        ending = ending.trim();
        int cutIndex = ending.indexOf(" ");
        String operator = ending.substring(0, cutIndex);
        String condString = ending.substring(cutIndex+1);
        return makeCondition(enclosed, operator, makeCondition(condString));
      }
    } else {
      if (condition.contains(" OR ")) {
        String[] parts = condition.split(" OR ");
        Condition[] conds = new Condition[parts.length];
        int i = 0;
        for (String part : parts) {
          conds[i] = makeCondition(part);
          i++;
        }
        return new OrCondition(conds);
      } else if (condition.contains(" AND ")) {
        String[] parts = condition.split(" AND ");
        Condition[] conds = new Condition[parts.length];
        int i = 0;
        for (String part : parts) {
          conds[i] = makeCondition(part);
          i++;
        }
        return new AndCondition(conds);
      } else if (condition.startsWith("NOT ")) {
        return new NotCondition(makeCondition(condition.substring(4)));
      } else
        return new SingleCondition(condition);
    }
  }

  private static Condition makeCondition (String cond1, String operator, String cond2) {
    if (operator.equals("OR"))
      return new OrCondition(makeCondition(cond1), makeCondition(cond2));
    else
      return new AndCondition(makeCondition(cond1), makeCondition(cond2));
  }

  private static Condition makeCondition (Condition cond1, String operator, Condition cond2) {
    if (operator.equals("OR"))
      return new OrCondition(cond1, cond2);
    else
      return new AndCondition(cond1, cond2);
  }

}


// This Condition is the base condition. It knows how to evaluate all of the singleton conditions.
class SingleCondition extends Condition {
  
  private static final String[] conditions = {"TRUE", "FALSE", "CAN MOVE R", "CAN MOVE L", "CAN MOVE U", "CAN MOVE D"};
  
  private String condition;
  
  public SingleCondition (String condition) throws UBotSyntaxError {
    if (!isCondition(condition))
      throw new UBotSyntaxError("Unrecognized condition `" + condition + "`.");
    this.condition = condition.trim();
  }
  
  // Evaluates the condition. Default for unrecognised conditions is false.
  public boolean evaluate (GameData data) {
    if (condition.equals("TRUE"))
      return true;
    else if (condition.equals("FALSE"))
      return false;
    else if (condition.contains("CAN MOVE ")) {
      Point p = data.getUBotXY();
      Point newPoint;
      if (condition.endsWith("R"))
        newPoint = new Point(p.x+1, p.y);
      else if (condition.endsWith("L"))
        newPoint = new Point(p.x-1, p.y);
      else if (condition.endsWith("U"))
        newPoint = new Point(p.x, p.y-1);
      else
        newPoint = new Point(p.x, p.y+1);
      return data.insideLevel(newPoint) && !data.isOccupied(newPoint);
    } else
      return false;
  }
  
  public String toString () {
    return "\"" + condition + "\"";
  }
  
  private static boolean isCondition (String s) {
    if (s == null) return false;
    for (String cond : conditions) {
      if (s.equals(cond)) return true;
    }
    return false;
  }
}


// This Condition is to take two (or more) conditions and perform boolean AND.
class AndCondition extends Condition {

  private Condition[] conds;

  public AndCondition (Condition a, Condition b) {
    conds = new Condition[2];
    conds[0] = a; conds[1] = b;
  }
  
  public AndCondition (Condition[] conds) {
    this.conds = conds;
  }
  
  public boolean evaluate (GameData data) {
    for (Condition c : conds) {
      if (!c.evaluate(data))
        return false;
    }
    return true;
  }
  
  public String toString () {
    String result = "and-|";
    for (Condition c : conds) {
      result += c.toString() + "--";
    }
    result += "|a";
    return result;
  }

}


// This condition is to take two (or more) conditions and perform boolean OR.
class OrCondition extends Condition {
  
  private Condition[] conds;
  
  public OrCondition (Condition a, Condition b) {
    conds = new Condition[2];
    conds[0] = a; conds[1] = b;
  }
  
  public OrCondition (Condition[] conds) {
    this.conds = conds;
  }
  
  public boolean evaluate (GameData data) {
    for (Condition c : conds) {
      if (c.evaluate(data))
        return true;
    }
    return false;
  }

  public String toString () {
    String result = "or-|";
    for (Condition c : conds) {
      result += c.toString() + "--";
    }
    result += "|o";
    return result;
  }
  
}


// This condition returns the opposite of its parameter.
class NotCondition extends Condition {
  
  private Condition a;
  
  public NotCondition (Condition a) {
    this.a = a;
  }
  
  public boolean evaluate (GameData data) {
    return !a.evaluate(data);
  }
  
  public String toString () {
    return "not-|" + a.toString() + "|n";
  }
}
