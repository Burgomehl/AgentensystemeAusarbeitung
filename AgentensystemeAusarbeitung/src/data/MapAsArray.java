package data;

import java.util.ArrayList;
import java.util.List;

/**
 * Ich denke das wird die Bevorzugte variante die Karte dazustellen Unten Links
 * ist (0,0) Es wird immer von der Mitte aus begonnen die Karte zu befüllen Wenn
 * da nichts ist steht im Array Null Zu nutzen ist {@IMap} als Speicherort für
 * die Map, falls sich da was ändert. Dich sollte für die GUI nur getMap()
 * interressieren
 * 
 * @author Benjamin Byl
 *
 */
public class MapAsArray {

	private Cell[][] map;
	private Cord currentLocation;

	public MapAsArray(Cell f) {
		map = new Cell[11][11];
		Cord mid = getMid();
		currentLocation = mid;
		map[mid.getX()][mid.getY()] = f;
	}

	public MapAsArray() {
		map = new Cell[11][11];
		Cord mid = getMid();
		currentLocation = mid;
	}

	public Cord addNewField(Cell field, Cord cord) {
		if (isInRange(cord)) {
			resizeMap(10,cord);

		}
		if (map[cord.getX()][cord.getY()] == null) {
			map[cord.getX()][cord.getY()] = field;
		} else {
			System.out.println("There is already a Field");
		}
		print(map);
		return cord;
	}

	public Cord getCurrentLocation() {
		return currentLocation;
	}
	
	public Cell getCurrentField(Cord cord){
		if(isInRange(cord)){
			resizeMap(10,cord);
		}
		return map[cord.getX()][cord.getY()];
	}

	public List<Cord> getNeighbours(Cord cord) {
		List<Cord> list = new ArrayList<>();
		Cord test = new Cord(cord.getX()-1, cord.getY());
		if(isInRange(test)){
			resizeMap(10,test);
		}
		list.add((map[test.getX() - 1][test.getY()] == null) ? test : null);
		test = new Cord(cord.getX(), cord.getY()-1);
		if(isInRange(test)){
			resizeMap(10,test);
		}
		list.add((map[test.getX()][test.getY() - 1] == null) ? test : null);
		test = new Cord(cord.getX(), cord.getY()+1);
		if(isInRange(test)){
			resizeMap(10,test);
		}
		list.add((map[test.getX()][test.getY() + 1] == null) ? test : null);
		test = new Cord(cord.getX()+1, cord.getY());
		if(isInRange(test)){
			resizeMap(10, test);
		}
		list.add((map[test.getX() + 1][test.getY()] == null) ? test : null);
		return list;
	}

	public int getFieldIndex(Cord cord) {
		int index = 0;
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j < 1; ++j) {
				if(isInRange(cord)){
					resizeMap(10, cord);
				}
				Cord cordT = new Cord(cord.getX()+i, cord.getY()+j);
				index += (map[cordT.getX()][cordT.getY()] != null) ? 1 : 0;
			}
		}
		return index;
	}

	public Cell[][] getMap() {
		return map;
	}

	private void resizeMap(int resize, Cord cord) {
		Cell[][] newMap = new Cell[map.length + resize][map.length + resize];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				newMap[i + (resize / 2)][j + (resize / 2)] = map[i][j];
			}
		}
		map = newMap;
		cord.setX(cord.getX() + resize / 2);
		cord.setY(cord.getY() + resize / 2);
	}

	private Cord getMid() {
		return new Cord((int) (map.length / 2), (int) (map[0].length / 2));
	}

	private boolean isInRange(Cord c) {
		return !(c.getX() < map.length-1 && c.getY() < map[0].length-1 && c.getX() >= 0 && c.getY() >= 0);
	}

	public void print(Cell[][] map) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j] == null) {
					System.out.print("-");
				}else if(map[i][j].getStench()>0){
					System.out.print("S");
				}else if(map[i][j].isRock()){
					System.out.print("R");
				}else if(i==getMid().getX() && j == getMid().getY() || j==getMid().getX() && i == getMid().getY()){
					System.out.print("H");
				}else{
					System.out.print("O");
				}
			}
			System.out.println();
		}
		System.out.println();
	}

}
