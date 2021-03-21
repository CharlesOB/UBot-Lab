package ui.code;

public class Command {

  private String	command;

  public static boolean isCommand (String str) {
    return str.trim() != "";
  }

  public Command (String command) {
    this.command = command;
  }

  public String getCommand () {
    return command;
  }

}
