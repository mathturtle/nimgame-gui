package waldonsm.nimgame.ai;

/**
 * My stupid AI used as default.
 * @author Shawn Waldon
 *
 */
public class TakeMaximumPossibleAI implements NimGameAI {

	/**
	 * Returns the minimum of the two integer parameters--always takes the maximum that it can.
	 */
	@Override
	public int chooseNumToTake(int numLeft, int maxNumTakable,
			boolean lastTakenLoses) {
		return Math.min(numLeft, maxNumTakable);
	}
	
	

}
