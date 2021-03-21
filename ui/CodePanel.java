package ui;

import ui.code.*;
import java.awt.*;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

public class CodePanel extends JPanel implements CodeHolder, ComponentListener {

  private int	width, height;

  private ArrayList<Fragment>	script;
  private ArrayList<Fragment>	pieces;

  private Image		offscreenImage;
  private Graphics	offscr;

  public CodePanel (int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void paint (Graphics g) {
    offscreenImage = createImage(width, height);
    offscr = offscreenImage.getGraphics();


  }

  public String nextCommand (UBotGraphicsPanel game) {
    return "w";
  }

  public boolean hasNextCommand () {
    return false;
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

}
