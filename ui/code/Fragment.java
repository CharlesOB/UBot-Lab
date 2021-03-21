package ui.code;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;

public interface Fragment {
  public void draw (Graphics g) ;
  public void setXY (Point p) ;
  public Point getXY () ;
  public Dimension getSize () ;
  public Rectangle getBounds () ;
  public boolean inside () ;
}
