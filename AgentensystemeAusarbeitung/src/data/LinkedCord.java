package data;

public class LinkedCord extends Cord {
	private LinkedCord lastCord;

	private LinkedCord(int x, int y) {
		super(x, y);
	}

	public LinkedCord getLastCord() {
		return lastCord;
	}

	public void setLastCord(LinkedCord lastCord) {
		this.lastCord = lastCord;
	}

}
