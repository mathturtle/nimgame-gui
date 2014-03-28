/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package waldonsm.nimgame.gui.animation;


import java.awt.Color;

import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

/**
 * My implementation of an Explosion for the CS2440 explosion lab redone with thread safety
 * @author Shawn Waldon
 */
public class Explosion implements Animatable {

	private final GameOfLife model;

	/**
	 * Guarded by "this"
	 */
	private int turn;
	private final Runnable doneRunnable;

	/**
	 * Creates a new Explosion of with the given pixel size at the origin with the 
	 * given doneRunnable.
	 *  
	 * @param size the size of each pixel of the explosion
	 * @param run the runnable to call for cleanup
	 */
	public Explosion(int size, Runnable run) {
		Color color = new Color((float)1.0,(float)(Math.random()/2+.25),(float)Math.random()/2);
		model = new GameOfLife(25,25,size, 0, 0, color);
		setUp();
		doneRunnable = run;
	}

	/**
	 * Creates a new Explosion of the given pixel size at the specified coordinates with
	 * the given done runnable
	 * @param x the x position
	 * @param y the y position
	 * @param size the size of each pixel of the explosion
	 * @param run the runnable to call for cleanup
	 */
	public Explosion(int x, int y, int size, Runnable run) {
		Color color = new Color((float)1.0,(float)(Math.random()/2+.25),(float)Math.random()/2);
		model = new GameOfLife(25,25,size, x, y, color);
		setUp();
		doneRunnable = run;
	}


	/**
	 * sets up the initial state of the Explosion
	 */
	private void setUp() {
		addCross(13,10);
		addCross(10,13);
		addCross(13,16);
		addCross(16,13);
	}

	/**
	 * Adds a plus to the GameOfLife underlying model centered at the given coordinates
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void addCross(int x, int y) {
		model.addCell(y, x);
		model.addCell(y, x+1);
		model.addCell(y, x-1);
		model.addCell(y+1, x);
		model.addCell(y-1, x);
	}

	/**
	 * Draws the Explosion
	 */
	public void draw(Graphics2D g) {
		synchronized(this) {
			if (turn == 37)
				return;
		}
		model.draw(g);
	}

	/**
	 * Calls next on the underlying model, then does some processing
	 */
	public void next() {
		synchronized(this) {
			if (turn == 37)
				return;
			turn++;
		}
		model.next();
		processTurn();
	}

	/**
	 * Returns true if the Explosion is done
	 */
	public boolean isDone() {
		synchronized (this) {
			return turn == 37;
		}
	}

	/**
	 * Does some processing on the model is the turn is 25 or 32
	 */
	private void processTurn() {
		boolean twentyfive, thirtyTwo;
		synchronized (this) {
			twentyfive = (turn == 25);
			thirtyTwo = (turn == 32);
		}
		if (twentyfive) {
			addCross(5,13);
			addCross(13,5);
			addCross(21,13);
			addCross(13,21);
		} else if (thirtyTwo) {
			model.addCell(5, 13);
			model.addCell(13, 5);
			model.addCell(21, 13);
			model.addCell(13, 21);
		}
	}

	/**
	 * Compares the Animatable to some other Animatable.
	 */
	public int compareTo(Animatable other) {
		if (other instanceof Explosion) {
			return model.compareTo(((Explosion)other).model);
		} else {
			return getClass().getName().compareTo(other.getClass().getName());
		}
	}

	/**
	 * Submits the done runnable via SwingUtilities.invokeLater
	 */
	@Override
	public void submitDoneRunnable() {
		SwingUtilities.invokeLater(doneRunnable);
	}
}
