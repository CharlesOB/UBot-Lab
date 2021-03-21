package sprites;

import ui.UBotGraphicsPanel;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;

public class RoombaSprite extends MovingSprite {

  public RoombaSprite (int x, int y) {
    this(x, y, new Point(1, 0));
  }

  public RoombaSprite (int x, int y, Point vector) {
    super(x, y, vector);
  }

  public void draw (Graphics g, Point offset) {
    Point p = centerAnchor();
    g.setColor(Color.red);
    g.fillOval(p.x + offset.x - 5, p.y + offset.y - 5, 10, 10);
    g.setColor(Color.yellow);
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
  }

  public void update (UBotGraphicsPanel game) {
    super.update(game);
    Point p = new Point(x+vector.x, y+vector.y);
    if (game.isOccupied(p) || !game.insideLevel(p)) {
      int i = vector.x;
      vector.x = -vector.y;
      vector.y = i;
    } else {
      move(game, vector);
    }
  }

}
