package data;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Ich denke das wird die Bevorzugte variante die Karte dazustellen Unten Links
 * ist (0,0) Es wird immer von der Mitte aus begonnen die Karte zu befüllen Wenn
 * da nichts ist steht im Array Null Zu nutzen ist {@IMap} als Speicherort für
 * die Map, falls sich da was ändert. Dich sollte für die GUI nur getMap()
 * interessieren
 * 
 * @author Benjamin Byl
 *
 */
public class MapAsArrayReloaded {
	private static final Logger log = Logger.getLogger(MapAsArrayReloaded.class);

	private Cell[][] map;

	public MapAsArrayReloaded(Cell f) {
		PropertyConfigurator.configure("log4j.properties");
		map = new Cell[11][11];
		Cord mid = getMid();
		map[mid.getX()][mid.getY()] = f;
	}

	public MapAsArrayReloaded() {
		map = new Cell[11][11];
	}

	private Cord getRelativePosition(Cord cord) {
		Cord newCord = new Cord(cord.getX() - getMid().getX(), cord.getY() - getMid().getY());
		log.info("converting total : " + cord + " to cord " + newCord);
		return newCord;
	}

	public Cord getTotalPosition(Cord cord) {
		Cord newCord = new Cord(getMid().getX() + cord.getX(), getMid().getY() + cord.getY());
		log.info("converting relativ : " + cord + " to total " + newCord);
		return newCord;
	}

	public Cord addNewField(Cell field, Cord cord) {
		Cord cordNew = getTotalPosition(cord);
		log.info("Add new field on: " + cordNew);
		if (isInRange(cordNew)) {
			resizeMap(10, cordNew);
		}
		if (map[cordNew.getX()][cordNew.getY()] == null) {
			map[cordNew.getX()][cordNew.getY()] = field;
		} else {
			System.out.println("There is already a Field");
		}
		print(map);
		return getRelativePosition(cordNew);
	}

	public Cell getCurrentField(Cord cord) {
		Cord cordNew = getTotalPosition(cord);
		if (isInRange(cordNew)) {
			resizeMap(10, cordNew);
		}
		return map[cordNew.getX()][cordNew.getY()];
	}

	public List<Cord> getNeighbours(Cord cord) {
		Cord cordNew = getTotalPosition(cord);
		log.info("GetNeighbours on: " + cordNew);
		List<Cord> list = new ArrayList<>();
		testCord(cordNew, list, -1, 0);
		testCord(cordNew, list, 0, -1);
		testCord(cordNew, list, 0, 1);
		testCord(cordNew, list, 1, 0);
		log.info("Current Location after analysis of neighbours: " + cordNew + " listsize: " + list.size());
		return list;
	}

	private void testCord(Cord cordNew, List<Cord> list, int x, int y) {
		Cord possibleCordinates = new Cord(cordNew.getX() + x, cordNew.getY() + y);
		if (isInRange(possibleCordinates)) {
			resizeMap(10, cordNew);
		}
		if (map[possibleCordinates.getX()][possibleCordinates.getY()] == null) {
			list.add(getRelativePosition(possibleCordinates));
		}
	}

	public int getFieldIndex(Cord cord) {
		Cord cordNew = getTotalPosition(cord);
		log.info("analsysis the fieldindex: " + cordNew);
		int index = 0;
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j < 1; ++j) {
				Cord cordT = new Cord(cordNew.getX() + i, cordNew.getY() + j);
				if (!isInRange(cordT)) {
					index += (map[cordT.getX()][cordT.getY()] != null) ? 1 : 0;
				}
			}
		}
		log.info("currentLocation after analysis of fieldindex: " + cordNew);
		return index;
	}

	public Cell[][] getMap() {
		return map;
	}

	private void resizeMap(int resize, Cord cord) {
		log.info("resize Map with cord: " + cord);
		Cell[][] newMap = new Cell[map.length + resize][map.length + resize];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				newMap[i + (resize / 2)][j + (resize / 2)] = map[i][j];
			}
		}
		map = newMap;
		cord.setX(cord.getX() + resize / 2);
		cord.setY(cord.getY() + resize / 2);
		log.info("resized cord: " + cord);

	}

	public Cord getMid() {
		return new Cord(map.length / 2, map[0].length / 2);
	}

	private boolean isInRange(Cord c) {
		return !(c.getX() < map.length - 1 && c.getY() < map[0].length - 1 && c.getX() >= 0 && c.getY() >= 0);
	}

	public void print(Cell[][] map) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				Cell cell = map[j][i];
				if (cell == null) {
					b.append("-");
				} else if (cell.getStench() > 0) {
					b.append("S");
				} else if (cell.isRock()) {
					b.append("R");
				} else if (i == getMid().getX() && j == getMid().getY()
						|| j == getMid().getX() && i == getMid().getY()) {
					b.append("H");
				} else if (cell.getFood() > 0) {
					b.append("F");
				} else if (cell.isTrap()) {
					b.append("T");
				} else {
					b.append("O");
				}
			}
			b.append("\n\r");
		}
		log.info(b);
	}

	public Cord getCurrentLocation() {
		return getRelativePosition(getMid());
	}

}
