package sprites;

import ui.UBotGraphicsPanel;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;

public class SpikeGeneratorSprite extends GeneratorSprite {

  private int readyState = 0;

  public SpikeGeneratorSprite (int x, int y, int max) {
    super(x, y, max);
  }

  public SpikeGeneratorSprite (int x, int y) {
    super(x, y);
  }

  public void draw (Graphics g, Point offset) {
    Point p = centerAnchor();
    g.setColor(Color.red);
    int[] xPoints = {offset.x+p.x-5, offset.x+p.x+5, offset.x+p.x+5, offset.x+p.x-5};
    int[] yPoints = {offset.y+p.y+5, offset.y+p.y+5, offset.y+p.y-5, offset.y+p.y-5};
    g.fillPolygon(xPoints, yPoints, 4);
    g.setColor(Color.yellow);
    g.drawLine(p.x+offset.x-8, p.y+offset.y, p.x+offset.x+8, p.y+offset.y);
    g.drawLine(p.x+offset.x, p.y+offset.y-8, p.x+offset.x, p.y+offset.y+8);
    g.drawLine(p.x+offset.x-6, p.y+offset.y-6, p.x+offset.x+6, p.y+offset.y+6);
    g.drawLine(p.x+offset.x-6, p.y+offset.y+6, p.x+offset.x+6, p.y+offset.y-6);
  }

  public void update (UBotGraphicsPanel game) {
    super.update(game);
    Point[] points = {new Point(x-1, y), new Point(x+1, y), new Point(x, y+1), new Point(x, y-1)};
    Point[] vectors = {new Point(-1, 0), new Point(1, 0), new Point(0, 1), new Point(0, -1)};
    int i = 0;
    if (readyState == 0) {
      for (Point p : points) {
        if (game.insideLevel(p) && !game.isOccupied(p) && !reachedMax()) {
          game.addSpriteLater(new SpikeSprite(p.x, p.y, vectors[i]));
          spriteAdded();
        }
        i++;
      }
      readyState++;
    } else {
      readyState++;
      if (readyState > 3) readyState = 0;
    }
  }
}
