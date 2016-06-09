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
 * interressieren
 * 
 * @author Benjamin Byl
 *
 */
@Deprecated
public class MapAsArray {
	private static final Logger log = Logger.getLogger(MapAsArray.class);

	private Cell[][] map;

	public MapAsArray(Cell f) {
		PropertyConfigurator.configure("log4j.properties");
		map = new Cell[11][11];
		Cord mid = getMid();
		map[mid.getX()][mid.getY()] = f;
	}

	public MapAsArray() {
		map = new Cell[11][11];
	}

	public Cord addNewField(Cell field, Cord cord) {
		log.info("Add new field on: "+cord);
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
		return getMid();
	}
	
	public Cell getCurrentField(Cord cord){
		if(isInRange(cord)){
			resizeMap(10,cord);
		}
		return map[cord.getX()][cord.getY()];
	}

	public List<Cord> getNeighbours(Cord cord) {
		log.info("GetNeighbours on: "+cord);
		List<Cord> list = new ArrayList<>();
		Cord test = new Cord(cord.getX()-1, cord.getY());
		if(isInRange(test)){
			resizeMap(10, cord);
			return getNeighbours(cord);
		}
		list.add((map[test.getX()][test.getY()] == null) ? test : null);
		test = new Cord(cord.getX(), cord.getY()-1);
		if(isInRange(test)){
			resizeMap(10, cord);
			return getNeighbours(cord);
		}
		list.add((map[test.getX()][test.getY()] == null) ? test : null);
		test = new Cord(cord.getX(), cord.getY()+1);
		if(isInRange(test)){
			resizeMap(10, cord);
			return getNeighbours(cord);
		}
		list.add((map[test.getX()][test.getY()] == null) ? test : null);
		test = new Cord(cord.getX()+1, cord.getY());
		if(isInRange(test)){
			resizeMap(10, cord);
			return getNeighbours(cord);
		}
		list.add((map[test.getX()][test.getY()] == null) ? test : null);
		log.info("Current Location after analysis of neighbours: "+cord);
		return list;
	}

	public int getFieldIndex(Cord cord) {
		log.info("analsysis the fieldindex: "+cord);
		int index = 0;
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j < 1; ++j) {
				Cord cordT = new Cord(cord.getX()+i, cord.getY()+j);
				if(!isInRange(cordT)){
					index += (map[cordT.getX()][cordT.getY()] != null) ? 1 : 0;
				}
			}
		}
		log.info("currentLocation after analysis of fieldindex: "+cord);
		return index;
	}

	public Cell[][] getMap() {
		return map;
	}

	private void resizeMap(int resize, Cord cord) {
		log.info("resize Map with cord: "+cord);
		Cell[][] newMap = new Cell[map.length + resize][map.length + resize];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				newMap[i + (resize / 2)][j + (resize / 2)] = map[i][j];
			}
		}
		map = newMap;
		cord.setX(cord.getX() + resize / 2);
		cord.setY(cord.getY() + resize / 2);
		log.info("resized cord: "+cord);
		
	}

	public Cord getMid() {
		return new Cord((int) (map.length / 2), (int) (map[0].length / 2));
	}

	private boolean isInRange(Cord c) {
		return !(c.getX() < map.length-1 && c.getY() < map[0].length-1 && c.getX() >= 0 && c.getY() >= 0);
	}

	public void print(Cell[][] map) {
		StringBuilder b = new StringBuilder();
		for (int i = 0 ; i < map.length; i++) {
			for (int j = 0 ; j < map[i].length ; j++) {
				Cell cell = map[j][i];
				if (cell == null) {
					b.append("-");
				}else if(cell.getStench()>0){
					b.append("S");
				}else if(cell.isRock()){
					b.append("R");
				}else if(i==getMid().getX() && j == getMid().getY() || j==getMid().getX() && i == getMid().getY()){
					b.append("H");
				}else if(cell.getFood()>0){
					b.append("F");
				}else if(cell.isTrap()){
					b.append("T");
				}else{
					b.append("O");
				}
			}
			b.append("\n\r");
		}
		log.info(b);
	}

}
