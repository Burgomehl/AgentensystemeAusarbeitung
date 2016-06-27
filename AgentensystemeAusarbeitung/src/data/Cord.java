package data;

import java.util.Objects;

public class Cord {
	int x;
	int y;

	public Cord(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "{Cord:[X:" + getX() + "],[Y:" + getY() + "]}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Cord) {
			Cord temp = (Cord) obj;
			return this.getX() == temp.getX() && this.getY() == temp.getY();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getX(),getY());
	}
}