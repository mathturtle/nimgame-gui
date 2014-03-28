package waldonsm.nimgame.gui.animation;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * This in the animation runner for NimGame.  It is an enum because it is assumed that there is
 * ever only one of them.  Possibly a bad design choice, but for now it works.
 * @author Shawn Waldon
 *
 */
public enum AnimationRunner implements Runnable {
	
	RUNNER;
	
	/**
	 * The thread running the animation
	 */
	private Thread animThread;
	/**
	 * The set of Animatable objects.
	 */
	private ConcurrentSkipListSet<Animatable> animSet = new ConcurrentSkipListSet<Animatable>();
	/**
	 * The list of JPanels to repaint after setting the animation to the next frame
	 */
	private CopyOnWriteArrayList<JPanel> panels = new CopyOnWriteArrayList<JPanel>();
	/**
	 * The Runnable that refreshes the JPanels (executed on Swing EDT via invokeLater
	 * after every frame of animation)
	 */
	private Runnable paintRunnable = new Runnable() {
		public void run() {
			for (JPanel panel: panels) {
				panel.repaint();
			}
		}
	};

	/**
	 * Creates and starts the animation Thread if this has not already been done.
	 */
	public static void startIfNotStarted() {
		synchronized(AnimationRunner.class) {
			if (RUNNER.animThread == null) {
				RUNNER.animThread = new Thread(RUNNER);
				RUNNER.animThread.start();
			}
		}
	}
	
	/**
	 * Adds the given JPanel to the list of panels to repaint after each animation
	 * @param panel
	 */
	public void addPanelToRepaintList(JPanel panel) {
		panels.add(panel);
	}

	/**
	 * Runs the animation, calling next on each Animatable and then using the paintRunnable
	 * repainting the Panels
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean repaintNeeded = animSet.isEmpty();
		while (!Thread.interrupted()) {
            for (Iterator<Animatable> it = animSet.iterator(); it.hasNext();) {
                Animatable a = it.next();
                if (a.isDone()) {
                    a.submitDoneRunnable();
                    it.remove();
                }
                a.next();
            }
            if (repaintNeeded) {
            	SwingUtilities.invokeLater(paintRunnable);
            }
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
		
		synchronized(AnimationRunner.class) {
			animThread = null;
		} // nothing in run method after this please
	}

	/**
	 * Returns true if the AnimationRunner is done animating everything
	 * @return true if the AnimationRunner is done animating everything
	 */
	public boolean isDoneWithAll() {
		return animSet.isEmpty();
	}
	
	/**
	 * Gets the set of Animatable objects used by the AnimationRunner
	 * @return the set of Animatable objects used by the AnimationRunner
	 */
	public Set<Animatable> getAnimatables() {
		return Collections.unmodifiableSet(animSet);
	}
	
	/**
	 * Adds the given Animatable object to the set of Animatables to animate.
	 * @param a the Animatable
	 */
	public void addAnimatable(Animatable a) {
		animSet.add(a);
	}

}
