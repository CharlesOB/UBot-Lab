package sprites;

import ui.UBotGraphicsPanel;
import java.awt.*;

public class CheckpointSprite extends SpriteObject {

  public static int UBOT = 1;
  public static int ANY = 2;

  protected boolean clicked = false;
  protected int who;	// which sprites can trip the checkpoint.

  private boolean clickNextUpdate = false;

  public CheckpointSprite (int x, int y) {
    this(x, y, UBOT);
  }

  public CheckpointSprite (int x, int y, int who) {
    super(x, y);
    this.who = who;
  }

  public void draw (Graphics g, Point offset) {
    Point p = centerAnchor();
    if (!clicked) {
      g.setColor(Color.red);
    } else {
      g.setColor(Color.green);
    }
    g.fillOval(p.x + offset.x - 3, p.y + offset.y - 3, 6, 6);
  }

  public void update (UBotGraphicsPanel game) {
    if (clickNextUpdate) {
      clicked = true;
      clickNextUpdate = false;
    }
  }

  public boolean canBeSteppedOn () {
    return true;
  }

  public boolean readyForGameFinish (UBotGraphicsPanel game) {
    return clicked;
  }

  public void collideWith (Sprite s, UBotGraphicsPanel game) {
    if (who == UBOT) {
      if (s instanceof UBotSprite) {
        clickNextUpdate = true;
      }
    } else {
      clickNextUpdate = true;
    }
  }

}
