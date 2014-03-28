package waldonsm.nimgame.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import waldonsm.nimgame.ai.NimGameAI;
import waldonsm.nimgame.gui.animation.Animatable;
import waldonsm.nimgame.gui.animation.AnimationDoneListener;
import waldonsm.nimgame.gui.animation.AnimationRunner;
import waldonsm.nimgame.gui.animation.Explosion;

/**
 * This class is the main part of the NimGame GUI.  This panel is where the drawing gets done,
 * as well as containing the model for the game (a boolean array).  The only pieces the GUI
 * requires other than this panel, is some way for the user to say they are done before they
 * have taken their max number of pieces, and a way to create a new game, both implemented by
 * calling methods on this panel.  
 * @author Shawn Waldon
 *
 */
public class NimGamePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String IMAGE_FILENAME= "bomb.png";
	
	private BufferedImage image;
	private AnimationDoneListener playerDone;
	private AnimationDoneListener aiDone;
	
	private NimGameAI aiBot;
	private int numLeft, maxNumTakable;
	private boolean lastTakenLoses;
	private boolean[] isTaken;
	private int numTakenThisTurn = 0;
	private boolean playerTurn;

	/**
	 * Creates a new NimGamePanel with the given parameters.  Most of these can be changed with
	 * the newGame() method, but the NimGameAI cannot be changed after the panel is created.
	 * @param numThings the number of things in the pile to start with
	 * @param maxNumTakable the maximum number of things that a player may take in one turn
	 * @param lastTakenLoses true if the player who takes the last thing from the pile loses
	 * @param bot the NimGameAI bot that will control the AI for this NimGamePanel.
	 * @param playerGoesFirst true if the player should have his/her turn first, false if the AI goes first.
	 */
	public NimGamePanel(int numThings, int maxNumTakable, boolean lastTakenLoses, NimGameAI bot, boolean playerGoesFirst) {
		aiBot = bot;
		numLeft = numThings;
		isTaken = new boolean[numThings];
		this.maxNumTakable = maxNumTakable;
		this.lastTakenLoses = lastTakenLoses;
		playerTurn = playerGoesFirst;
		playerDone = new PlayerAnimationDoneListener();
		aiDone = new AIAnimationDoneListener();
		Dimension dim = new Dimension(500,(numThings/10.0 > 10.0)?(numThings/10+1)*50:500);
		setSize(dim);
		setPreferredSize(dim);
		setBackground(Color.WHITE);
		addMouseListener(new NimGamePanelMouseListener());
		AnimationRunner.startIfNotStarted();
		AnimationRunner.RUNNER.addPanelToRepaintList(this);
		try {
			image = ImageIO.read(new File(IMAGE_FILENAME));
		} catch (IOException e) {
			// makes a default image
			image = new BufferedImage(50,50, BufferedImage.TYPE_4BYTE_ABGR);
			image.createGraphics().fillOval(10, 10, 30, 30);
		}
	}
	
	/**
	 * Resets the panel for a new game.  This method resets most of the parameters from the
	 * constructor.
	 * @param numThings the initial number of things in the pile
	 * @param maxNumTakable the maximum number of things that can be taken in one turn
	 * @param lastTakenLoses true if the player who takes the last thing from the pile should lose
	 * @param playerGoesFirst true if the player goes first, false if the AI goes first
	 */
	public void newGame(int numThings, int maxNumTakable, boolean lastTakenLoses, boolean playerGoesFirst) {
		numLeft = numThings;
		isTaken = new boolean[numThings];
		this.maxNumTakable = maxNumTakable;
		this.lastTakenLoses = lastTakenLoses;
		playerTurn = playerGoesFirst;
		numTakenThisTurn = 0;
		repaint();
	}
	
	/**
	 * Called by the Done button to indicate that the player is done with their current turn.
	 * <P>
	 * This method checks to see if the player has taken any items this turn, and only if they
	 * have it gives the AI its turn.
	 */
	public void playerDone() {
		if (playerTurn && numTakenThisTurn > 0) {
			playerTurn = false;
			SwingUtilities.invokeLater(playerDone);
		}
	}
	
	/**
	 * Paints the NimGamePanel with the things and the animations currently happening.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		for (int i = 0; i < isTaken.length; i++) {
			if (!isTaken[i]) {
//				g2.fillRect(i%10*50, getHeight() - i/10*50 - 50, 50, 50);
				int x = (50 - image.getWidth())/2 + i%10*50;
				int y = (50 - image.getHeight())/2 + getHeight() - i/10*50-50;
				g2.drawImage(image, x, y, this);
			}
		}
		for (Animatable a: AnimationRunner.RUNNER.getAnimatables()) {
			a.draw(g2);
		}
	}
	
	/**
	 * This class is the MouseListener for the NimGamePanel.  It causes the thing that is
	 * clicked on to be taken by the player, initializing the animation.
	 * 
	 * @author Shawn Waldon
	 *
	 */
	private class NimGamePanelMouseListener extends MouseAdapter {
		/**
		 * If the player clicked on a thing, it causes the thing to be taken and the 
		 * animation to be played.
		 */
		public void mouseReleased(MouseEvent e) {
			if (!playerTurn)
				return;
			int x = e.getX();
			int y = e.getY();
			if (x < NimGamePanel.this.getWidth() && x >= 0 && 
					y < NimGamePanel.this.getHeight() && y >= 0) {
				y = NimGamePanel.this.getHeight() - y;
				// calculates the i in the array
				int i = x / 50 + (y / 50 * 10);
				if (i < isTaken.length && !isTaken[i] && numTakenThisTurn < maxNumTakable) {
					// takes the item
					isTaken[i] = true;
					numTakenThisTurn++;
					numLeft--;
					repaint();

					// animation
					int animX = (50 - image.getWidth())/2 + i%10*50 - 7;
					int animY = (50 - image.getHeight())/2 + getHeight() - i/10*50-50 - 5;
					AnimationRunner.RUNNER.addAnimatable(new Explosion(animX,animY,2,playerDone));

					// if the player took the maximum number of things, then their turn is over
					if (numTakenThisTurn == maxNumTakable) {
						playerTurn = false;
					}
				}
			}
		}
	}
	
	/**
	 * The AnimationDoneListener for the player's turn animation.  This is where the AI is called
	 * and where the AI animation is launched.
	 * 
	 * @author Shawn Waldon
	 *
	 */
	private class PlayerAnimationDoneListener extends AnimationDoneListener {

		/*
		 * If it is still the player's turn, or the animation is not done, return.
		 * Otherwise, check for win/lose, call the AI and animate the AI's choices.
		 */
		@Override
		public void animationDone() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			if (playerTurn) {
				return;
			}
			if (numLeft == 0) {
				if (lastTakenLoses) {
					JOptionPane.showMessageDialog(NimGamePanel.this, "You Lose");
				} else {
					JOptionPane.showMessageDialog(NimGamePanel.this, "You Win");
				}
				return;
			}
			numTakenThisTurn = 0;
			// get number taken by AI
			int aiTakes = aiBot.chooseNumToTake(numLeft, maxNumTakable, lastTakenLoses);
			// test for illegal value
			if (aiTakes > numLeft || aiTakes > maxNumTakable || aiTakes <= 0) {
				throw new IllegalStateException("AI just attempted to take illegal value: " + aiTakes);
			}
			// get things not taken
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < isTaken.length; i++) {
				if (!isTaken[i])
					list.add(i);
			}
			while (aiTakes > 0) {
				// AI taking a piece randomly
				int taken = list.remove((int) (Math.random() * list.size()));
				isTaken[taken] = true;
				numLeft--;
				aiTakes--;

				// animation
				int animX = (50 - image.getWidth())/2 + taken%10*50 -7;
				int animY = (50 - image.getHeight())/2 + getHeight() - taken/10*50-50 - 5;
				AnimationRunner.RUNNER.addAnimatable(new Explosion(animX,animY,2,aiDone));
			}
			repaint();
		}
		
	}
	
	/**
	 * The AnimationDoneListener for when the AI is done and the player should be given
	 * control.
	 * @author Shawn Waldon
	 *
	 */
	private class AIAnimationDoneListener extends AnimationDoneListener {
		
		/**
		 * If animation still occurring OR the player's turn, then return, otherwise set to
		 * the player's turn and check for win/lose.
		 */
		@Override
		public void animationDone() {
			
			// all executed on EDT so this will work
			if (playerTurn)
				return;
			playerTurn = true;
			if (numLeft == 0) {
				if (lastTakenLoses) {
					JOptionPane.showMessageDialog(NimGamePanel.this, "You Win");
				} else {
					JOptionPane.showMessageDialog(NimGamePanel.this, "You Lose");
				}
				return;
			}
		}
	}
}
