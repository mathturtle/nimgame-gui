/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package waldonsm.nimgame.gui.animation;

/**
 * My implementation of John Conway's Game Of Life.
 * @author Shawn Waldon
 */
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;


class GameOfLife implements Animatable {

	private static final Random gen = new Random();

	// Guarded by "this"
	private int[][] field;
	private final int boxSize;
	private final int x, y;
	private final int randIndex;

	private final Color color;

	/**
	 * Creates a new GameOfLife board with the given height and width
	 * @param h the height of the board (in cells)
	 * @param w the width of the board (in cells)
	 */
	public GameOfLife(int h, int w) {
		if (h <= 0 || w <= 0)
			throw new IllegalArgumentException();
		field = new int[h+2][w+2];
		boxSize = 1;
		x = y = 0;
		color = Color.orange;
		randIndex = gen.nextInt();
	}

	/**
	 * Creates a new GameOfLife board with the given height, width, size of cell, 
	 * x and y coordinates, and color.
	 * @param h the height of the board (in cells)
	 * @param w the width of the board (in cells)
	 * @param size the size of each cell (in pixels wide/high)
	 * @param x the x position of the board
	 * @param y the y position of the board
	 * @param c the Color to draw the GameOfLife cells
	 */
	public GameOfLife(int h, int w, int size, int x, int y, Color c) {
		if (h <= 0 || w <= 0) {
			throw new IllegalArgumentException();
		}
		field = new int[h+2][w+2];
		boxSize = size;
		this.x = x;
		this.y = y;
		color = c;
		randIndex = gen.nextInt();
	}

	/**
	 * Adds a cell to the board at the given position.
	 * @param h the vertical position on the board
	 * @param w the horizontal position on the board
	 */
	public void addCell(int h, int w) {
		synchronized(this) {
			if (h > field.length-2 || h < 1 || w > field[0].length-2 || w < 1)
				throw new IllegalArgumentException();
			field[h][w] = 1;
			//                for (int[] i : field)
				//                System.out.println(Arrays.toString(i));
			//                System.out.println("\n\n");
		}
	}

	/**
	 * Returns true if the given position is a cell
	 * @param h the vertical position on the board
	 * @param w the horizontal position on the board
	 * @return true if the given position is a cell
	 */
	public boolean isCell(int h, int w) {
		synchronized(this) {
			if (h > field.length-2 || h < 1 || w > field[0].length-2 || w < 1)
				throw new IllegalArgumentException();
			return field[h][w] == 1;
		}
	}

	/**
	 * Draws the GameOfLife board to the given Graphics2D
	 */
	public void draw(Graphics2D g) {
		int[][] fieldCopy;
		synchronized(this) {
			fieldCopy = getCopyOfField();
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(color);
		for (int i = 0; i <fieldCopy.length; i++) {
			for (int j = 0; j <fieldCopy[i].length; j++) {
				if (fieldCopy[i][j] == 1)
					g2.fillRect(x + j * boxSize, y + i * boxSize, boxSize, boxSize);
			}
		}
		//                for (int[] i : fieldCopy)
		//                System.out.println(Arrays.toString(i));
		//                System.out.println("\n\n");
	}

	/**
	 * Must be called from within a synchronized block, used by draw to avoid a large 
	 * synchronized block.
	 * @return
	 */
	private int[][] getCopyOfField() {
		int[][] result = new int[field.length][field[0].length];
		for (int i = 0; i < result.length; i++) {
			System.arraycopy(field[i], 0, result[i], 0, field[i].length);
			//                for (int j = 0; j < result[i].length; j++) {
			//                    result[i][j] = field[i][j];
			//                }
		}
		return result;
	}

	/**
	 * Advances the GameOfLife board to the next iteration
	 */
	public void next() {
		int[][] newField;
		synchronized(this) {
			newField = new int[field.length][field[0].length];
		}
		for (int i = 1; i < newField.length-1; i++) {
			for (int j = 1; j < newField[i].length-1; j++) {
				//                            System.out.println(willBeCell(i,j));q
				if (willBeCell(i,j)) {
					newField[i][j] = 1;
				}
			}
		}
		//                for (int[] i : newField)
		//                System.out.println(Arrays.toString(i));
		//                System.out.println("\n\n");
		synchronized(this) {
			field = newField;
		}
	}

	/**
	 * Returns true if the given position will be a cell in the next generation
	 * @param h the vertical position on the board
	 * @param w the horizontal position on the board
	 * @return true if the given position will be a cell in the next generation
	 */
	private boolean willBeCell(int h, int w) {
		int surroundCount = getSurroundCount(h,w);
		//                System.out.println(surroundCount);
		if (isCell(h,w)) {
			return surroundCount > 1 && surroundCount < 4;
		} else {
			return surroundCount == 3;
		}
	}

	/**
	 * Returns the count of the surrounding squares that are cells
	 * @param h the vertical position on the board
	 * @param w the horizontal position on the board
	 * @return the count of the surrounding squares that are cells
	 */
	private int getSurroundCount(int h, int w) {
		synchronized(this) {
			//                System.out.println(field[h-1][w-1] + field[h-1][w] + field[h-1][w+1] + field[h][w-1] +
			//				field[h][w+1] + field[h+1][w-1] + field[h+1][w] + field[h+1][w+1]);
			return field[h-1][w-1] + field[h-1][w] + field[h-1][w+1] + field[h][w-1] +
			field[h][w+1] + field[h+1][w-1] + field[h+1][w] + field[h+1][w+1];
		}
	}

	/**
	 * Returns the size that each cell is drawn to the screen.
	 * @return the size that each cell is drawn to the screen
	 */
	public int getBoxSize() {
		return boxSize;
	}

	/**
	 * Returns the x position of the GameOfLifeBoard on the screen
	 * @return the x position of the GameOfLifeBoard on the screen
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the y position of the GameOfLifeBoard on the screen
	 * @return the y position of the GameOfLifeBoard on the screen
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns the color that the GameOfLifeBoard will be drawn to the screen
	 * @return the color that the GameOfLifeBoard will be drawn to the screen
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * The Game Of Life is NEVER done... returns false
	 */
	public boolean isDone() {
		return false;
	}

	/**
	 * Compares this to other Animatables.
	 */
	public int compareTo(Animatable other) {
		if (other instanceof GameOfLife) {
			GameOfLife o = (GameOfLife) other;
			if (x > o.x)
				return 1;
			else if (x < o.x)
				return -1;
			else if (y > o.y)
				return 1;
			else if (y < o.y)
				return -1;
			else if (boxSize > o.boxSize)
				return 1;
			else if (boxSize < o.boxSize)
				return -1;
			else
				return (randIndex > o.randIndex) ? 1 : (randIndex < o.randIndex) ? -1 : 0;
		} else {
			return getClass().getName().compareTo(other.getClass().getName());
		}
	}

	/**
	 * Does nothing since this class is never used by itself (package-private, used by Explosion)
	 */
	@Override
	public void submitDoneRunnable() {
		// does nothing
	}
}
