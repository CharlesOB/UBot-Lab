package sprites;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;

public class WallSprite extends SpriteObject {

  public WallSprite (int x, int y) {
    super(x, y);
  }

  public void draw (Graphics g, Point offset) {
    Point p = centerAnchor();
    g.setColor(Color.gray);
    g.fillRect(p.x+offset.x-MAX_WIDTH/2, p.y+offset.y-MAX_HEIGHT/2, MAX_WIDTH, MAX_HEIGHT);
  }

}
