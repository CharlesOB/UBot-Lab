package sprites;

import ui.UBotGraphicsPanel;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;

public abstract class GeneratorSprite extends SpriteObject {

  protected int max = -1;
  protected int numGenerated = 0;

  public GeneratorSprite (int x, int y) {
    super(x, y);
  }

  public GeneratorSprite (int x, int y, int max) {
    super(x, y);
    this.max = max;
  }

  public abstract void draw (Graphics g, Point offset) ;

  protected boolean reachedMax () {
    if (max < 0)
      return false;
    else
      return numGenerated >= max;
  }  

  protected void spriteAdded () {
    numGenerated++;
  }

}
