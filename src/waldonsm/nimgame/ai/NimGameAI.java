package waldonsm.nimgame.ai;

/**
 * The interface describing the AI for the NimGame.  The AI must decide based on the number
 * left and the maximum number takeable, with the boolean flag describing the goal, how many
 * things to take. 
 * @author Shawn Waldon
 *
 */
public interface NimGameAI {

	/**
	 * The method called by the GUI to ask the AI how many things it is taking.
	 * <P>
	 * NOTES: This method must return a number between (inclusive) 1 and the minimum of 
	 * maxNumTakable and numLeft.
	 *  
	 * @param numLeft the number of things left in the pile
	 * @param maxNumTakable the maximum number of things that a player can take in one turn
	 * @param lastTakenLoses true if the goal is to force the opponent to take the last thing
	 * @return the number of things that the AI chooses to take.
	 */
	int chooseNumToTake(int numLeft, int maxNumTakable, boolean lastTakenLoses);
}
