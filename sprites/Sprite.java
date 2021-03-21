package sprites;

import ui.UBotGraphicsPanel;
import java.awt.*;

public interface Sprite {

  public static final int MAX_WIDTH = 20, MAX_HEIGHT = 20;

  public void draw (Graphics g, Point offset) ;
  // 0 <= fraction < 1. This draw uses a fraction that tells the Sprite how far between the main frames the time is. If fraction is 0.5, the Sprite should draw itself 0.5 of the way from its last location to its current location.
  public void draw (Graphics g, Point offset, double fraction) ;
  public void update (UBotGraphicsPanel game) ;
  public boolean canBeSteppedOn () ;
  public Point getXY () ;
  public void setXY (Point p) ;
  public boolean readyForGameFinish (UBotGraphicsPanel game) ;
  public boolean move (UBotGraphicsPanel game, Point vector) ;
  public boolean move (Point vector) ;
  public void collideWith (Sprite s, UBotGraphicsPanel game) ; // Collide means that they occupy the same space.
  public boolean touches (Sprite s) ;
  public boolean touches (Point p) ;
  public void touch (Sprite s, UBotGraphicsPanel game) ; // Touch means they occupy adjacent spaces.
}
