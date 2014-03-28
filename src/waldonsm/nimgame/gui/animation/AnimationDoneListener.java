package waldonsm.nimgame.gui.animation;

public abstract class AnimationDoneListener implements Runnable {
	
	public final void run() {
		animationDone();
	}
	
	public abstract void animationDone();
}
