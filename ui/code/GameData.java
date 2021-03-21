package ui.code;

import java.awt.Point;

public interface GameData {
  public boolean isOccupied (Point p) ;
  public boolean insideLevel (Point p) ;
  public boolean isLevelFinished () ;
  public boolean readyForGameFinish () ;
  public Point   getUBotXY () ;
}
