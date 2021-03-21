package sprites;

import ui.UBotGraphicsPanel;
import java.awt.*;

public abstract class SpriteObject implements Sprite {

  protected int x, y;   // the current x and y
  protected int lx, ly; // the previous x and y

  public SpriteObject (int x, int y) {
    this.x = x; this.y = y;
    this.lx = x; this.ly = y;
  }

  public abstract void draw (Graphics g, Point offset) ;

  public void draw (Graphics g, Point offset, double fraction) {
    Point newOffset = new Point(offset);
    newOffset.x += (int) ((lx - x) * (1.0 - fraction) * UBotGraphicsPanel.BOX_WIDTH);
    newOffset.y += (int) ((ly - y) * (1.0 - fraction) * UBotGraphicsPanel.BOX_HEIGHT);
    draw(g, newOffset);
  }

  public void update (UBotGraphicsPanel game) {
    lx = x;
    ly = y;
  }

  public boolean canBeSteppedOn () {
    return false;
  }

  public Point getXY () {
    return new Point(x, y);
  }

  public void setXY (Point p) {
    lx = x; ly = y;
    x = p.x; y = p.y;
  }

  public boolean readyForGameFinish (UBotGraphicsPanel game) {
    return true;
  }

  public boolean move (UBotGraphicsPanel game, Point vector) {
    if (!game.insideLevel(new Point(x+vector.x, y+vector.y)))
      return false;
    if (game.isOccupied(new Point(x+vector.x, y+vector.y))) {
      return false;
    } else {
      return move(vector);
    }
  }

  public boolean move (Point vector) {
    lx = x; ly = y;
    x += vector.x; y += vector.y;
    return true;
  }

  public void collideWith (Sprite s, UBotGraphicsPanel game) {}

  public boolean touches (Sprite s) {
    return touches(s.getXY());
  }

  public boolean touches (Point p) { // All 8 adjacent squares
    return p.distance((double) x, (double) y) <= 1.5;
  }

  public void touch (Sprite s, UBotGraphicsPanel game) {}

  // Uses Center Anchor
  protected Point centerAnchor () {
    return new Point(x*UBotGraphicsPanel.BOX_WIDTH + UBotGraphicsPanel.BOX_WIDTH/2, y*UBotGraphicsPanel.BOX_HEIGHT + UBotGraphicsPanel.BOX_HEIGHT/2);
  }

}
