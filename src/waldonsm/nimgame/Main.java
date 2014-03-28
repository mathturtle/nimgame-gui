package waldonsm.nimgame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import waldonsm.nimgame.ai.TakeMaximumPossibleAI;
import waldonsm.nimgame.gui.NimGamePanel;

/**
 * The demo main class for the NimGame.
 * @author Shawn Waldon
 *
 */
public class Main implements Runnable {

	/**
	 * An example main for the NimGame
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Main());
	}
	
	/**
	 * Passed to SwingUtilities.invokeLater by main.  The actual sample code for the NimGame.
	 */
	public void run() {

		JFrame frame = new JFrame("NimGame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final NimGamePanel panel = new NimGamePanel(45,5,true,new TakeMaximumPossibleAI(),true);
		frame.add(panel);
		
		JPanel p2 = new JPanel();
		JButton button = new JButton("Done");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.playerDone();
			}
		});
		
		JButton button2 = new JButton("New Game");
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.newGame(45, 5, true, true);
			}
		});
		p2.add(button);
		p2.add(button2);
		frame.add(p2, BorderLayout.SOUTH);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
