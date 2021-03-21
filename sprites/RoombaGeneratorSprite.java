package sprites;

import ui.UBotGraphicsPanel;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;

public class RoombaGeneratorSprite extends GeneratorSprite {

  public RoombaGeneratorSprite (int x, int y, int max) {
    super(x, y, max);
  }

  public RoombaGeneratorSprite (int x, int y) {
    super(x, y);
  }

  public void draw (Graphics g, Point offset) {
    Point p = centerAnchor();
    g.setColor(Color.red);
    int[] xPoints = {offset.x+p.x-5, offset.x+p.x+5, offset.x+p.x+5, offset.x+p.x-5};
    int[] yPoints = {offset.y+p.y+5, offset.y+p.y+5, offset.y+p.y-5, offset.y+p.y-5};
    g.fillPolygon(xPoints, yPoints, 4);
  }

  public void update (UBotGraphicsPanel game) {
    super.update(game);
    Point[] points = {new Point(x-1, y), new Point(x+1, y), new Point(x, y+1), new Point(x, y-1)};
    Point[] vectors = {new Point(-1, 0), new Point(1, 0), new Point(0, 1), new Point(0, -1)};
    int i = 0;
    for (Point p : points) {
      if (game.insideLevel(p) && !game.isOccupied(p) && !reachedMax()) {
        game.addSpriteLater(new RoombaSprite(p.x, p.y, vectors[i]));
        spriteAdded();
      }
      i++;
    }
  }
}
