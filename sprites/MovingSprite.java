package sprites;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;

public abstract class MovingSprite extends SpriteObject {

  protected Point	vector;

  public MovingSprite (int x, int y) {
    this(x, y, new Point(0, 0));
  }

  public MovingSprite (int x, int y, Point vector) {
    super(x, y);
    this.vector = vector;
  }

}
