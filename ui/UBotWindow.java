package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.FileSystem;
import java.io.IOException;

public class UBotWindow extends JFrame implements ActionListener {

  private int		width = 500, height = 300;

  private JButton	runButton, resetButton;
  private JTextArea	codeArea;
  private JScrollPane	scroll;
  private JMenuBar	menuBar;
  private JMenu		gameSpeedMenu, uBotMenu, levelMenu;
  private ButtonGroup	delayGroup;
  private JRadioButtonMenuItem	delay100, delay200, delay500, delay1000;
  private JCheckBoxMenuItem	seeUBot;
  private UBotGraphicsPanel	game;

  public UBotWindow () {
    setSize(width, height);
    centerWindow(this);
    setTitle("UBot Lab");
    setResizable(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    initGameParts();
  }

  private void initGameParts () {
    initComponents();
  }

  private void initComponents () {
    runButton = new JButton("Run Code");
    resetButton = new JButton("Reset");
    codeArea = new JTextArea(15, 20);
    scroll = new JScrollPane(codeArea);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    codeArea.setEditable(true);
    codeArea.setLineWrap(true);
    codeArea.setWrapStyleWord(true);
    game = new UBotGraphicsPanel(width / 2, height, codeArea);

    JPanel codePanel = new JPanel();
    codePanel.setLayout(new GridLayout(2, 1));
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(2, 1));
    buttonPanel.add(runButton);
    buttonPanel.add(resetButton);
    codePanel.add(scroll);
    codePanel.add(buttonPanel);

    setLayout(new GridLayout(1, 2));
    add(game);
    add(codePanel);

    runButton.addActionListener(this);
    resetButton.addActionListener(this);

    menuBar = new JMenuBar();

    gameSpeedMenu = new JMenu("Game Speed");
    menuBar.add(gameSpeedMenu);

    delayGroup = new ButtonGroup();
    delay1000 = new JRadioButtonMenuItem("Slow");
    delay500 = new JRadioButtonMenuItem("Normal");
    delay500.setSelected(true);
    delay200 = new JRadioButtonMenuItem("Fast");
    delay100 = new JRadioButtonMenuItem("Super Fast");
    delayGroup.add(delay1000);
    delayGroup.add(delay500);
    delayGroup.add(delay200);
    delayGroup.add(delay100);
    delay1000.addActionListener(this);
    delay500.addActionListener(this);
    delay200.addActionListener(this);
    delay100.addActionListener(this);
    gameSpeedMenu.add(delay1000);
    gameSpeedMenu.add(delay500);
    gameSpeedMenu.add(delay200);
    gameSpeedMenu.add(delay100);

    int numLevels = 20;
    Path path = FileSystems.getDefault().getPath("levels", "levelData.txt");
    String dataString = "";
    try {
      dataString = new String(Files.readAllBytes(path));
      numLevels = Integer.parseInt(dataString.substring(1, 3));
    } catch (IOException e) {
      System.out.println("Error in fetching file:");
      System.out.println(e);
    }
    

    levelMenu = new JMenu("Level Select");
    for (int i = 0; i <= numLevels; i++) {
      JMenuItem item = new JMenuItem("Level " + (new Integer(i)).toString());
      item.addActionListener(this);
      item.setActionCommand((new Integer(i).toString()));
      levelMenu.add(item);
    }

    menuBar.add(levelMenu);

    uBotMenu = new JMenu("UBot");
    menuBar.add(uBotMenu);
    seeUBot = new JCheckBoxMenuItem("Show UBot", true);
    seeUBot.addActionListener(this);
    uBotMenu.add(seeUBot);

    this.setJMenuBar(menuBar);
  }

  public void actionPerformed (ActionEvent e) {
    if (e.getSource() == runButton)
      game.runCode(codeArea.getText());
    else if (e.getSource() == resetButton) {
      game.stop();
      game.resetLevel();
      game.repaint();
    } else if (e.getSource() instanceof JRadioButtonMenuItem) {      
      String s = e.getActionCommand();
      int delay = 1000;
      if (s == "Normal") delay = 500;
      else if (s == "Fast") delay = 200;
      else if (s == "Super Fast") delay = 100;
      game.setDelay(delay);
    } else if (e.getSource() == seeUBot) {
      game.stop();
      game.setUBotVisibility(seeUBot.getState());
      game.resetLevel();
      game.repaint();
    } else if (e.getSource() instanceof JMenuItem) {
      game.stop();
      game.resetLevel(Integer.parseInt(e.getActionCommand()));
      game.repaint();
    }
  }

  private void centerWindow (Window w) {
    Toolkit tk = Toolkit.getDefaultToolkit();
    Dimension d = tk.getScreenSize();
    setLocation((d.width - w.getWidth()) / 2,
              (d.height - w.getHeight()) / 2);
  }

}
