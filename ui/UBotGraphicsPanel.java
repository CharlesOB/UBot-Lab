package ui;

import sprites.*;
import ui.code.GameData;
import ui.code.UBotSyntaxError;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.FileSystem;
import java.io.IOException;

public class UBotGraphicsPanel extends JPanel implements GameData, ActionListener, ComponentListener {

  public static final int BOX_WIDTH = Sprite.MAX_WIDTH + 4;
  public static final int BOX_HEIGHT = Sprite.MAX_HEIGHT + 4;

  private int		width, height;
  private Timer		timer;
  private int		delay = 500;
  private int		fps = 30;
  private int		frameCounter;

  private JTextArea	codeArea;

  private Image	offscreenImage;
  private Graphics	offscr;

  private int			level;
  private int			levelWidth, levelHeight;
  private ArrayList<Sprite>	backgroundSprites;
  private ArrayList<Sprite>	foregroundSprites;
  private ArrayList<Sprite>	addLater;
  private UBotSprite		uBot;
  private Point			finish;
  private Point			offset;
  private boolean		finished;
  private boolean		seeUBot;

  private CodeManager		program;
  private String[]		codeLines;

  private Object		highlightTag;

  public UBotGraphicsPanel (int width, int height, JTextArea codeArea) {
    this.codeArea = codeArea;
    this.width = width;
    this.height = height;
    addComponentListener(this);
    timer = new Timer(1000 / fps, this);
    timer.setInitialDelay(1000 / fps);
    level = 0;
    seeUBot = true;
    resetLevel(level);
  }

  public void resetLevel () {
    resetLevel(this.level);
  }

  public void resetLevel (int level) {
    this.level = level;
    finished = false;
    String fileName = "level" + (new Integer(level)).toString() + ".txt";
    Path path = FileSystems.getDefault().getPath("levels", fileName);
    String levelDataString = "";
    try {
      levelDataString = new String(Files.readAllBytes(path));
    } catch (IOException e) {
      System.out.println("Error in fetching file:");
      System.out.println(e);
      if (level > 0) {
        this.level--;
        resetLevel();
        return;
      }
    }
    String[] levelData = levelDataString.split("\n");
    String[] dimensionData = levelData[0].split(" ");
    levelWidth = Integer.parseInt(dimensionData[0]);
    levelHeight = Integer.parseInt(dimensionData[1]);
    backgroundSprites = new ArrayList<Sprite>();
    foregroundSprites = new ArrayList<Sprite>();
    addLater = new ArrayList<Sprite>();
    String[] finishData = levelData[1].split(" ");
    finish = new Point(Integer.parseInt(finishData[0]), Integer.parseInt(finishData[1]));
    uBot = (UBotSprite) SpriteParser.parse(levelData[2]);
    if (seeUBot)
      addSprite(uBot);
    for (int i = 3; i < levelData.length; i++) {
      Sprite s = SpriteParser.parse(levelData[i]);
      if (s != null)
        addSprite(s);
    }
  }

  public void runCode (String code) {
    stop();
    resetLevel(level);
    try {
      program = new CodeManager(code);
    } catch (UBotSyntaxError e) {
      JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR!", JOptionPane.ERROR_MESSAGE);
      program = new CodeManager();
    }
    codeLines = code.split("\n");
    repaint();
    start();
  }

  public void actionPerformed (ActionEvent e) {
    frameCounter++;
    if (frameCounter >= delay*fps/1000) {
      update();
      manageCollisions();
      frameCounter = 0;
      if (finished)
        nextLevel();
      if (isLevelFinished())
        finished = true;
    }
    repaint();
  }

  public void componentHidden (ComponentEvent e) {}
  public void componentMoved (ComponentEvent e) {}
  public void componentShown (ComponentEvent e) {}

  public void componentResized (ComponentEvent e) {
    Dimension size = getSize();
    this.width = size.width;
    this.height = size.height;
    repaint();
  }

  public void start () {
    if (!timer.isRunning())
      timer.start();
    else
      timer.restart();
  }

  public void stop () {
    if (highlightTag != null) {
      codeArea.getHighlighter().removeHighlight(highlightTag);
      highlightTag = null;
    }
    timer.stop();
  }

  public void pause () {
    timer.stop();
  }

  public void resume () {
    start();
  }

  public void setDelay (int delay) {
    this.delay = delay;
    frameCounter = 0;
    timer.setDelay(1000/fps);
    timer.setInitialDelay(1000/fps);
  }

  public void paint (Graphics g) {
    offscreenImage = createImage(width, height);
    offscr = offscreenImage.getGraphics();

    int offsetX = width/2 - levelWidth*(Sprite.MAX_WIDTH+4)/2;
    int offsetY = height/2 - levelHeight*(Sprite.MAX_HEIGHT+4)/2;
    offset = new Point(offsetX, offsetY);

    offscr.setColor(Color.black);
    offscr.fillRect(0, 0, width, height);

    if (isLevelFinished())
      offscr.setColor(Color.green);
    else if (readyForGameFinish())
      offscr.setColor(Color.blue);
    else
      offscr.setColor(Color.red);
    offscr.fillRect(offset.x+finish.x*BOX_WIDTH, offset.y+finish.y*BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);

    offscr.setColor(Color.white);
    for (int i = 0; i <= levelWidth*(Sprite.MAX_WIDTH+4); i += (Sprite.MAX_WIDTH+4)) {
      offscr.drawLine(i+offset.x, offset.y, i+offset.x, levelHeight*(Sprite.MAX_HEIGHT+4)+offset.y);
    }
    for (int i = 0; i <= levelHeight*(Sprite.MAX_HEIGHT+4); i += (Sprite.MAX_HEIGHT+4)) {
      offscr.drawLine(offset.x, i+offset.y, offset.x+levelWidth*(Sprite.MAX_WIDTH+4), offset.y+i);
    }

    double fraction = 1000.0 * (double) frameCounter / (double) delay / fps;

    for (Sprite s : backgroundSprites) {
      s.draw(offscr, offset, fraction);
    }
    for (Sprite s : foregroundSprites) {
      s.draw(offscr, offset, fraction);
    }

    if (seeUBot)
      uBot.draw(offscr, offset, fraction);

    g.drawImage(offscreenImage, 0, 0, null);
  }

  public boolean isOccupied (Point p) {
    for (Sprite s : foregroundSprites) {
      Point sp = s.getXY();
      if (p.equals(sp))
        return true;
    }
    return false;
  }

  public void manageCollisions () {
    // Check each sprite against each other sprite only once.
    for (Sprite s1 : foregroundSprites) {
      for (Sprite s2 : backgroundSprites) {
        if (s1.getXY().equals(s2.getXY())) {
          s1.collideWith(s2, this);
          s2.collideWith(s1, this);
        } else if (s1.touches(s2)) {
          s1.touch(s2, this);
          s2.touch(s1, this);
        }
      }
    }
    for (int i = 0; i < foregroundSprites.size(); i++) {
      for (int j = i+1; j < foregroundSprites.size(); j++) {
        Sprite s1 = foregroundSprites.get(i);
        Sprite s2 = foregroundSprites.get(j);
        if (s1.getXY().equals(s2.getXY())) {
          s1.collideWith(s2, this);
          s2.collideWith(s1, this);
        } else if (s1.touches(s2)) {
          s1.touch(s2, this);
          s2.touch(s1, this);
        }
      }
    }
    for (int i = 0; i < backgroundSprites.size(); i++) {
      for (int j = i+1; j < backgroundSprites.size(); j++) {
        Sprite s1 = backgroundSprites.get(i);
        Sprite s2 = backgroundSprites.get(j);
        if (s1.getXY().equals(s2.getXY())) {
          s1.collideWith(s2, this);
          s2.collideWith(s1, this);
        } else if (s1.touches(s2)) {
          s1.touch(s2, this);
          s2.touch(s1, this);
        }
      }
    }
  }

  public ArrayList<Sprite> getTouching (Point p) {
    ArrayList<Sprite> touching = new ArrayList<Sprite>();
    for (Sprite s : backgroundSprites) {
      if (s.touches(p)) {
        touching.add(s);
      }
    }
    for (Sprite s : foregroundSprites) {
      if (s.touches(p)) {
        touching.add(s);
      }
    }
    return touching;
  }

  public boolean insideLevel (Point p) {
    return p.x >= 0 && p.x < levelWidth && p.y >= 0 && p.y < levelHeight;
  }

  public int getLevelWidth () {
    return levelWidth;
  }

  public int getLevelHeight () {
    return levelHeight;
  }

  public boolean isLevelFinished () {
    Point p = uBot.getXY();
    if (finish.x == p.x && finish.y == p.y) {
      return readyForGameFinish();
    } else
      return false;
  }

  public boolean readyForGameFinish () {
    for (Sprite s : backgroundSprites) {
      if (!s.readyForGameFinish(this))
        return false;
    }
    for (Sprite s : foregroundSprites) {
      if (!s.readyForGameFinish(this))
        return false;
    }
    return true;
  }

  private void update () {
    for (Sprite s : backgroundSprites) {
      s.update(this);
    }
    for (Sprite s : foregroundSprites) {
      s.update(this);
    }
    while (addLater.size() > 0) addSprite(addLater.remove(addLater.size()-1));
    String command = null;
    if (program.hasNextCommand(this))
      command = program.nextCommand(this);
    if (command != null && uBot.isAlive()) {
      interpret(command);
      int lineNum = program.getLastLineNum();
      int startPos = 0;
      for (int i = 0; i < lineNum && i < codeLines.length; i++) {
        startPos += codeLines[i].length() + 1; // +1 accounts for the newline character that was ommitted using 'split'.
      }
      int endPos = startPos + codeLines[lineNum].length();
      if (highlightTag == null) {
        DefaultHighlighter.DefaultHighlightPainter highlightPainter = 
          new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);
        try {
          highlightTag = codeArea.getHighlighter().addHighlight(startPos, endPos, highlightPainter);
        } catch (BadLocationException e) {}
      } else {
        try {
          codeArea.getHighlighter().changeHighlight(highlightTag, startPos, endPos);
        } catch (BadLocationException e) {}
      }
      codeArea.repaint();
    }
  }

  private void nextLevel () {
    JOptionPane.showMessageDialog(this, "Congratulations! You have finished level " + level + "!", "WIN", JOptionPane.PLAIN_MESSAGE);
    codeArea.requestFocus();
    codeArea.selectAll();
    stop();
    level++;
    resetLevel(level);
    repaint();
  }

  private void interpret (String line) {
    if (line.contains("r")) {
      uBot.move(this, new Point(1, 0));
    } else if (line.contains("l")) {
      uBot.move(this, new Point(-1, 0));
    } else if (line.contains("u")) {
      uBot.move(this, new Point(0, -1));
    } else if (line.contains("d")) {
      uBot.move(this, new Point(0, 1));
    }
  }

  private void addSprite (Sprite s) {
    if (s.canBeSteppedOn())
      backgroundSprites.add(s);
    else
      foregroundSprites.add(s);
  }

  public void addSpriteLater (Sprite s) {
    addLater.add(s);
  }

  public Point getUBotXY () {
    return uBot.getXY();
  }

  public void setUBotVisibility (boolean set) {
    seeUBot = set;
  }

}
