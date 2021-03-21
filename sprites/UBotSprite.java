package sprites;

import ui.UBotGraphicsPanel;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;

public class UBotSprite extends SpriteObject {

  private boolean isAlive = true;
  private boolean killNextUpdate = false;
  private Point vector = new Point(1, 0);

  public UBotSprite (int x, int y) {
    super(x, y);
  }

  public void draw (Graphics g, Point offset) {
    Point p = centerAnchor();
    if (isAlive) {
      g.setColor(Color.green);
      g.fillOval(p.x + offset.x - 5, p.y + offset.y - 5, 10, 10);
      g.setColor(Color.blue);
      if (vector.x != 0) {
        if (vector.x > 0)
          g.fillOval(p.x + offset.x + 3, p.y + offset.y - 2, 4, 4);
        else 
          g.fillOval(p.x + offset.x - 7, p.y + offset.y - 2, 4, 4);
      } else {
        if (vector.y > 0)
          g.fillOval(p.x + offset.x - 2, p.y + offset.y + 3, 4, 4);
        else
          g.fillOval(p.x + offset.x - 2, p.y + offset.y - 7, 4, 4);
      }
    } else {
      g.setColor(Color.red);
      g.drawLine(p.x + offset.x - 5, p.y + offset.y - 5, p.x + offset.x + 5, p.y + offset.y + 5);
      g.drawLine(p.x + offset.x + 5, p.y + offset.y - 5, p.x + offset.x - 5, p.y + offset.y + 5);
    }
  }

  public boolean readyForGameFinish (UBotGraphicsPanel game) {
    return isAlive;
  }

  public boolean move (UBotGraphicsPanel game, Point vector) {
    this.vector = vector;
    if (isAlive) {
      return super.move(game, vector);
    } else 
      return false;
  }

  public void update (UBotGraphicsPanel game) {
    super.update(game);
    if (killNextUpdate)
      isAlive = false;
  }

  public void collideWith (Sprite s, UBotGraphicsPanel game) {}

  public boolean isAlive () {
    return isAlive;
  }

  public void kill () {
    killNextUpdate = true;
  }

}
