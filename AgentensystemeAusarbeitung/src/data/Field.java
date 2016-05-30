package data;

public class Field {
	private int food;
	/**
	 * The smell of food
	 */
	private int smellItensity;
	/**
	 * The stench of traps
	 */
	private int stenchItensity;
	private long row;
	private long col;
	private boolean rock;
	private boolean trap;
	
	private Field north;
	private Field east;
	private Field south;
	private Field west;
	
	
	/**
	 * 
	 * @param food
	 * @param smellItensity
	 * @param stenchItensity
	 * @param row
	 * @param col
	 * @param rock
	 * @param trap
	 */
	public Field(int food, int smellItensity, int stenchItensity, long row, long col, boolean rock, boolean trap) {
		this.food = food;
		this.smellItensity = smellItensity;
		this.stenchItensity = stenchItensity;
		this.row = row;
		this.col = col;
		this.rock = rock;
		this.trap = trap;
	}
	
	public int getFood() {
		return food;
	}
	public void setFood(int food) {
		this.food = food;
	}
	public int getSmellItensity() {
		return smellItensity;
	}
	public void setSmellItensity(int smellItensity) {
		this.smellItensity = smellItensity;
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
	public Field getNorth() {
		return north;
	}
	public void setNorth(Field north) {
		this.north = north;
	}
	public Field getEast() {
		return east;
	}
	public void setEast(Field east) {
		this.east = east;
	}
	public Field getSouth() {
		return south;
	}
	public void setSouth(Field south) {
		this.south = south;
	}
	public Field getWest() {
		return west;
	}
	public void setWest(Field west) {
		this.west = west;
	}
	public int getStenchItensity() {
		return stenchItensity;
	}
	public long getRow() {
		return row;
	}
	public long getCol() {
		return col;
	}
}
