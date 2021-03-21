package sprites;

import ui.UBotGraphicsPanel;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;

public class SpikeSprite extends RoombaSprite {

  public SpikeSprite (int x, int y) {
    super(x, y);
  }

  public SpikeSprite (int x, int y, Point vector) {
    super(x, y, vector);
  }

  public void draw (Graphics g, Point offset) {
    super.draw(g, offset);
    Point p = centerAnchor();
    g.setColor(Color.yellow);
    g.drawLine(p.x+offset.x-24, p.y+offset.y, p.x+offset.x+24, p.y+offset.y);
    g.drawLine(p.x+offset.x, p.y+offset.y-24, p.x+offset.x, p.y+offset.y+24);
    g.drawLine(p.x+offset.x-18, p.y+offset.y-18, p.x+offset.x+18, p.y+offset.y+18);
    g.drawLine(p.x+offset.x-18, p.y+offset.y+18, p.x+offset.x+18, p.y+offset.y-18);
    
  }

  public void touch (Sprite s, UBotGraphicsPanel game) {
    if (s instanceof UBotSprite)
      ((UBotSprite) s).kill();
  }

}
