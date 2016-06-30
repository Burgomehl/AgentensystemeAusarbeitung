package data;

/**
 * class for json and for the map to save the location  
 *
 */
public class Cell {
	private int food;
	/**
	 * The smell of food
	 */
	private int smell;
	/**
	 * The stench of traps
	 */
	private int stench;
	private long row;
	private long col;
	private boolean rock;
	private boolean trap;
	private String type;
	
	
	/**
	 * 
	 * @param food
	 * @param smell
	 * @param stench
	 * @param row
	 * @param col
	 * @param rock
	 * @param trap
	 */
	public Cell(int food, int smell, int stench, long row, long col, boolean rock, boolean trap, String type) {
		this.food = food;
		this.smell = smell;
		this.stench = stench;
		this.row = row;
		this.col = col;
		this.rock = rock;
		this.trap = trap;
		this.type = type;
	}


	public int getFood() {
		return food;
	}


	public void setFood(int food) {
		this.food = food;
	}


	public int getSmell() {
		return smell;
	}


	public void setSmell(int smell) {
		this.smell = smell;
	}


	public int getStench() {
		return stench;
	}


	public void setStench(int stench) {
		this.stench = stench;
	}


	public long getRow() {
		return row;
	}


	public void setRow(long row) {
		this.row = row;
	}


	public long getCol() {
		return col;
	}


	public void setCol(long col) {
		this.col = col;
	}


	public boolean isRock() {
		return rock;
	}


	public void setRock(boolean rock) {
		this.rock = rock;
	}


	public boolean isTrap() {
		return trap;
	}


	public void setTrap(boolean trap) {
		this.trap = trap;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}
	

}
