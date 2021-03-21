package sprites;

import ui.UBotGraphicsPanel;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;

class CausticSprite extends MovingSprite {

  public CausticSprite (int x, int y) {
    this(x, y, new Point(1, 0));
  }

  public CausticSprite (int x, int y, Point vector) {
    super(x, y, vector);
  }

  public boolean canBeSteppedOn () {
    return true;
  }

  public void draw (Graphics g, Point offset) {
    Point p = centerAnchor();
    g.setColor(Color.orange);
    if (vector.x == 0) {
      int dy = 5;
      if (vector.y > 0) dy = -5;
      int[] xPoints = {offset.x+p.x-5, offset.x+p.x+5, offset.x+p.x};
      int[] yPoints = {offset.y+p.y+dy, offset.y+p.y+dy, offset.y+p.y-dy};
      g.fillPolygon(xPoints, yPoints, 3);
    } else {
      int dx = 5;
      if (vector.x > 0) dx = -5;
      int[] xPoints = {offset.x+p.x+dx, offset.x+p.x+dx, offset.x+p.x-dx};
      int[] yPoints = {offset.y+p.y-5, offset.y+p.y+5, offset.y+p.y};
      g.fillPolygon(xPoints, yPoints, 3);

    }
  }

  public void update (UBotGraphicsPanel game) {
    super.update(game);
    Point p = new Point(x+vector.x, y+vector.y);
    if ((game.isOccupied(p) || !game.insideLevel(p)) && !game.getUBotXY().equals(p)) {
      vector.x *= -1;
      vector.y *= -1;
    }
    if (!move(game, vector)) {
      if (game.getUBotXY().equals(p)) {
        move(vector);
      }
    }
  }

  public void collideWith (Sprite s, UBotGraphicsPanel game) {
    if (s instanceof UBotSprite)
      ((UBotSprite) s).kill();
  }

}
