package sprites;

import java.awt.Point;

public class SpriteParser {

  public static Sprite parse (String s) {
    String[] parts = s.split(" ");
    int x = Integer.parseInt(parts[1]);
    int y = Integer.parseInt(parts[2]);
    Point vector = null;
    int num = -1;
    if (parts.length == 4) {
      num = Integer.parseInt(parts[3]);
    } else if (parts.length >= 5) {
      int vx = Integer.parseInt(parts[3]);
      int vy = Integer.parseInt(parts[4]);
      vector = new Point(vx, vy);
    }
    if (parts[0].contains("UBotSprite"))
      return new UBotSprite(x, y);
    else if (parts[0].contains("WallSprite"))
      return new WallSprite(x, y);
    else if (parts[0].contains("CheckpointSprite"))
      if (num > 0)
        return new CheckpointSprite(x, y, num);
      else
        return new CheckpointSprite(x, y);
    else if (parts[0].contains("RoombaSprite"))
      if (vector != null)
        return new RoombaSprite(x, y, vector);
      else
        return new RoombaSprite(x, y);
    else if (parts[0].contains("CausticSprite"))
      if (vector != null)
        return new CausticSprite(x, y, vector);
      else
        return new CausticSprite(x, y);
    else if (parts[0].contains("SpikeSprite"))
      if (vector != null)
        return new SpikeSprite(x, y, vector);
      else
        return new SpikeSprite(x, y);
    else if (parts[0].contains("RoombaGeneratorSprite"))
      if (num >= 0)
        return new RoombaGeneratorSprite(x, y, num);
      else
        return new RoombaGeneratorSprite(x, y);
    else if (parts[0].contains("SpikeGeneratorSprite"))
      if (num >= 0)
        return new SpikeGeneratorSprite(x, y, num);
      else
        return new SpikeGeneratorSprite(x, y);
    else
      return null;
  }

}
