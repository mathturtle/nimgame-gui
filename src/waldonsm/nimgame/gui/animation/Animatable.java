package waldonsm.nimgame.gui.animation;

import java.awt.Graphics2D;

/**
 * Represents an Animatable object.  i.e. some animation.
 * @author Shawn
 *
 */
public interface Animatable extends Comparable<Animatable> {

	/**
	 * Returns true if the animation is finished
	 * @return
	 */
	boolean isDone();

	/**
	 * Advances the animation to its next frame.
	 */
	void next();

	/**
	 * After the animation's isDone method returns true, this method will be called to
	 * allow the animation to do any cleanup that is required after it completes
	 */
	void submitDoneRunnable();

	/**
	 * Draws the animation's current frame to the given graphics object 
	 * @param g2
	 */
	void draw(Graphics2D g2);

}
