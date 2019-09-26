// FloodIt Applet
//
// CS 201 Final Project
//
// Sarah Rittgers and Stephanie Castaneda
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;

@SuppressWarnings("serial")

public class FloodIt extends Applet implements ActionListener, 
ItemListener {

	// applet instance variables
	protected BoxCanvas c;
	protected Button redButton, blueButton, greenButton, yellowButton,
	purpleButton, pinkButton, resetButton;
	protected Choice levelChoice;
	protected Label title, steps;

	// initiate applet
	public void init () {
		setLayout(new BorderLayout());

		Panel p = new Panel();
		p.setLayout(new GridLayout(1,2));
		p.setBackground(Color.black);
		addButtons(p);
		add("South", p);

		Panel p1 = makeChoicePanel();
		add("North", p1);

		c = new BoxCanvas();
		c.setBackground(Color.black);
		add("Center", c);

		c.setBoardSize(levelChoice.getSelectedIndex());
		c.clear();
		steps.setText(c.getStepText());
	}

	// Makes north panel with choice & labels
	public Panel makeChoicePanel() {
		Panel p1 = new Panel();
		title = new Label("Flood-It!");
		p1.setBackground(Color.black);
		p1.setForeground(Color.white);
		p1.setLayout(new GridLayout(1,4));
		levelChoice = new Choice();
		levelChoice.addItem("Small");
		levelChoice.addItem("Medium");
		levelChoice.addItem("Large");
		levelChoice.setBackground(Color.white);
		levelChoice.setForeground(Color.black);
		levelChoice.addItemListener(this);
		levelChoice.select(0);
		steps = new Label("");
		p1.add(title);
		p1.add(steps);
		p1.add(levelChoice);

		setFont(new Font("TimesRoman", Font.BOLD, 28));
		return p1;
	}

	// creates and adds color buttons to bottom of applet
	public void addButtons(Panel p) {
		resetButton = new Button("reset");
		resetButton.setForeground(Color.black);
		resetButton.addActionListener(this);
		redButton = new Button("");
		redButton.setBackground(BoxCanvas.red);
		redButton.addActionListener(this);
		blueButton = new Button("");
		blueButton.setBackground(BoxCanvas.blue);
		blueButton.addActionListener(this);
		yellowButton = new Button("");
		yellowButton.setBackground(BoxCanvas.yellow);
		yellowButton.addActionListener(this);
		greenButton = new Button("");
		greenButton.setBackground(BoxCanvas.green);
		greenButton.addActionListener(this);
		purpleButton = new Button("");
		purpleButton.setBackground(BoxCanvas.purple);
		purpleButton.addActionListener(this);
		pinkButton = new Button("");
		pinkButton.setBackground(BoxCanvas.pink);
		pinkButton.addActionListener(this);
		p.add(redButton);
		p.add(blueButton);
		p.add(greenButton);
		p.add(pinkButton); 
		p.add(yellowButton);
		p.add(purpleButton);
		p.add(resetButton);
	}

	// handle events
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == resetButton) {
			c.clear();
		} else {
			if(c.steps < c.maxSteps && !c.won){
				if (e.getSource() == redButton) {
					c.changeFloodColor(0);
				} else if (e.getSource() == blueButton) {
					c.changeFloodColor(1);
				} else if (e.getSource() == greenButton) {
					c.changeFloodColor(2);
				} else if (e.getSource() == pinkButton) {
					c.changeFloodColor(3);
				} else if (e.getSource() == yellowButton) {
					c.changeFloodColor(4);
				} else { // purple button
					c.changeFloodColor(5);
				}
				c.flood();
			}
		}

		steps.setText(c.getStepText());;

	}

	// handle change in level choice selector
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == levelChoice) {
			int level = levelChoice.getSelectedIndex();
			c.setBoardSize(level);
			c.clear();
			steps.setText(c.getStepText());
		}  
	}
}

@SuppressWarnings("serial")

class BoxCanvas extends Canvas {

	// constants
	protected static Color red = new Color(250, 50, 0);
	protected static Color blue = new Color(0, 200, 250);
	protected static Color yellow = new Color(244, 217, 66);
	protected static Color green = new Color(50, 225, 50);
	protected static Color purple = new Color(107, 66, 244);
	protected static Color pink = new Color(244, 66, 182); 
	protected static int border = 20;

	// instance variables representing the game go here
	protected int n;
	protected int[][] board;
	protected int maxSteps;
	protected int steps;
	protected int floodColor;
	protected boolean won;

	// draw the boxes
	public void paint(Graphics g) {
		if (!won && steps < maxSteps){
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					int k = board[i][j];
					getColor(k, g);
					Dimension d = getSize(); // size of canvas
					int size= (int) ((d.getHeight()-2*border)/n);//regardless of board level, the board itself will remain the same size
					int x = i * size + border;
					int y = j * size + border;
					g.fillRect(x, y, size, size);
				}
			}
		} else if (won && steps <= maxSteps) {
			g.setColor(Color.white);
			g.drawString("You Won!", 120, 120);
		} else if (steps >= maxSteps) {
			g.setColor(Color.white);
			g.drawString("You Lost!", 120, 120);
		}
	}

	// sets graphic color to corresponding integer
	public void getColor(int i, Graphics g) {
		if (i == 0) {
			g.setColor(red); 
		} else if (i == 1) {
			g.setColor(blue); 
		} else if (i == 2) {
			g.setColor(green); 
		} else if (i == 3) {
			g.setColor(pink); 
		} else if (i == 4) {
			g.setColor(yellow); 
		} else {
			g.setColor(purple); 
		}
	}

	// clears the array with random colors and resets instance 
	// variables
	public void clear() {
		board = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				int k = ThreadLocalRandom.current().nextInt(0, 6);
				// to prevent boxes next to origin from being same color
				if ((i == 0 && j == 1) || (i == 1 && j == 0)) {
					while (board[0][0] == k) {
						k = ThreadLocalRandom.current().nextInt(0, 6);
					}
				}
				board[i][j] = k;
			}
		}
		steps = 0;
		won = false;
		repaint();
	}  

	//flood the grid
	public void flood() {		
		int[] origin = {0,0};//new int[] {0,0};
		int currentCol = board[0][0];
		floodHelper(origin, currentCol, floodColor);
		if (currentCol != floodColor) {
			steps += 1;
		}
		checkIfFlooded();
		repaint();
	}

	// helper function, recursively floods grid
	// uses Flood Fill algorithm
	public void floodHelper(int[] coords, int currentCol, 
			int floodCol) {

		int row = coords[0];
		int col = coords[1];

		if (currentCol == floodCol) {
			return;
		} else if (board[row][col] != currentCol) {//don't do anything if not flooded
			return;
		} else {
			board[row][col] = floodCol;

			if (row - 1 >= 0) { // left box
				int[] coordsUp = {row - 1, col};
				floodHelper(coordsUp, currentCol, floodCol);
			}
			if (col - 1 >= 0) { // top box
				int[] coordsLeft = {row, col - 1};
				floodHelper(coordsLeft, currentCol, floodCol);
			} 
			if (row + 1 < n) { // right box
				int[] coordsDown = {row + 1, col};
				floodHelper(coordsDown, currentCol, floodCol);
			}
			if (col + 1 < n) { // bottom box
				int[] coordsRight = {row, col + 1};
				floodHelper(coordsRight, currentCol, floodCol);
			}
		}
	}

	// iterate through array & return true if array is all 
	// same color
	public void checkIfFlooded() {

		int color = board[0][0];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (board[i][j] != color) {
					won = false;
					return;
				} 
			}
		}
		won = true;
	}

	// sets n to appropriate size
	public void setBoardSize(int s) {
		if (s == 0) { // small
			n = 12;
			maxSteps = 22;
		} else if (s == 1) { // medium
			n = 17;
			maxSteps = 30;
		} else { // large
			n = 22;
			maxSteps = 36;
		}
	}

	// changes the flood color to the given parameter
	public void changeFloodColor(int c) {
		floodColor = c;
	}

	// returns string of number of steps used out of total steps
	public String getStepText() {
		return ("Steps: " + steps + "/" + maxSteps);
	}

}

