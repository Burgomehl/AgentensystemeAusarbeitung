package data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Datastructur to save the found locations
 * @author Benjamin Byl
 *
 */
public class Map {
	private static final Logger log = Logger.getLogger(Map.class);

	private Cell[][] map;

	public Map(Cell f) {
		PropertyConfigurator.configure("log4j.properties");
		map = new Cell[11][11];
		Cord mid = getMid();
		map[mid.getX()][mid.getY()] = f;
	}

	public Map() {
		map = new Cell[11][11];
	}

	private Cord getRelativePosition(Cord cord) {
		Cord newCord = new Cord(cord.getX() - getMid().getX(), cord.getY() - getMid().getY());
		log.debug("converting total : " + cord + " to cord " + newCord);
		return newCord;
	}

	public Cord getTotalPosition(Cord cord) {
		Cord newCord = new Cord(getMid().getX() + cord.getX(), getMid().getY() + cord.getY());
		log.debug("converting relativ : " + cord + " to total " + newCord);
		return newCord;
	}

	public Cord addNewField(Cell field, Cord cord) {
		log.info("Add Field "+cord);
		Cord cordNew = modifyField(field, cord, a -> map[a.getX()][a.getY()] == null);
		return getRelativePosition(cordNew);
	}

	public Cord updateField(Cell field, Cord cord) {
		log.info("Update Field "+cord);
		Cord cordNew = modifyField(field, cord, a -> true);
		return getRelativePosition(cordNew);
	}

	/**
	 * In case of update the field will be set even if there is already an other field
	 * In case of "addnewfield" there will be just a update if the desired location is not set 
	 * @param field
	 * @param cord
	 * @param decision
	 * @return
	 */
	private Cord modifyField(Cell field, Cord cord, Predicate<Cord> decision) {
		Cord cordNew = getTotalPosition(cord);
		log.debug("Modify field on: " + cordNew);
		if (isInRange(cordNew)) {
			resizeMap(10, cordNew);
		}
		if (decision.test(cordNew)) {
			map[cordNew.getX()][cordNew.getY()] = field;
		}
		print(map);
		return cordNew;
	}

	/**
	 * gives a relativ location to the home field
	 * @param cord
	 * @return
	 */
	public Cell getCurrentField(Cord cord) {
		Cord cordNew = getTotalPosition(cord);
		if (isInRange(cordNew)) {
			resizeMap(10, cordNew);
		}
		return map[cordNew.getX()][cordNew.getY()];
	}
	/**
	 * @param cord
	 * @param decision
	 * @return neighbours depending on the given decision 
	 */
	public List<Cord> getNeighbours(Cord cord, Predicate<Cord> decision) {
		Cord cordNew = getTotalPosition(cord);
		log.debug("GetNeighbours on: " + cordNew);
		List<Cord> list = new ArrayList<>();
		getNeighbours(cordNew, list, -1, 0, decision);
		getNeighbours(cordNew, list, 0, -1, decision);
		getNeighbours(cordNew, list, 0, 1, decision);
		getNeighbours(cordNew, list, 1, 0, decision);

		log.debug("Current Location after analysis of neighbours: " + cordNew + " listsize: " + list.size());
		return list;
	}

	private void getNeighbours(Cord cordNew, List<Cord> list, int x, int y, Predicate<Cord> decision) {
		Cord possibleCordinates = new Cord(cordNew.getX() + x, cordNew.getY() + y);
		if (isInRange(possibleCordinates)) {
			resizeMap(10, possibleCordinates);
		}
		if (decision.test(possibleCordinates)) {
			list.add(getRelativePosition(possibleCordinates));
		}
	}

	public Cell[][] getMap() {
		return map;
	}

	private void resizeMap(int resize, Cord cord) {
		log.debug("resize Map with cord: " + cord);
		Cell[][] newMap = new Cell[map.length + resize][map.length + resize];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				newMap[i + (resize / 2)][j + (resize / 2)] = map[i][j];
			}
		}
		map = newMap;
		cord.setX(cord.getX() + resize / 2);
		cord.setY(cord.getY() + resize / 2);
		log.debug("resized cord: " + cord);

	}

	public Cord getMid() {
		return new Cord(map.length / 2, map[0].length / 2);
	}

	private boolean isInRange(Cord c) {
		return !(c.getX() < map.length - 1 && c.getY() < map[0].length - 1 && c.getX() >= 0 && c.getY() >= 0);
	}

	/**
	 * prints the map in the log, pre gui option for debug
	 * @param map
	 */
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
		log.debug(b);
	}

	public Cord getCurrentLocation() {
		return getRelativePosition(getMid());
	}

}
