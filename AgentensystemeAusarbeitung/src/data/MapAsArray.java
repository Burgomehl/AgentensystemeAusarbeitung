package data;


/**
 * Ich denke das wird die Bevorzugte variante die Karte dazustellen 
 * Unten Links ist (0,0)
 * Es wird immer von der Mitte aus begonnen die Karte zu befüllen
 * Wenn da nichts ist steht im Array Null
 * Zu nutzen ist {@IMap} als Speicherort für die Map, falls sich da was ändert. Dich sollte für die GUI nur getMap() interressieren
 * @author Benjamin Byl
 *
 */
public class MapAsArray implements IMap {

	private Field[][] map;
	private Cord currentLocation;

	public MapAsArray(Field f) {
		map = new Field[11][11];
		Cord mid = getMid();
		currentLocation = mid;
		map[mid.getX()][mid.getY()] = f;
	}
	
	public MapAsArray(){
		map = new Field[11][11];
		Cord mid = getMid();
		currentLocation = mid;
	}

	public void addNewField(Field field, Direction dir) {
		if (dir != null) {
			switch (dir) {
			case NORTH:
				currentLocation.setY(currentLocation.getY() + 1);
				break;
			case EAST:
				currentLocation.setX(currentLocation.getX() + 1);
				break;
			case WEST:
				currentLocation.setX(currentLocation.getX() - 1);
				break;
			case SOUTH:
				currentLocation.setY(currentLocation.getY() - 1);
				break;
			}
			if (isInRange(currentLocation)) {
				resizeMap(10);

			}
			if (map[currentLocation.getX()][currentLocation.getY()] == null) {
				map[currentLocation.getX()][currentLocation.getY()] = field;
			}else{
				System.out.println("There is already a Field");
			}
		}
	}
	
	public Field[][] getMap(){
		return map;
	}

	private void resizeMap(int resize) {
		Field[][] newMap = new Field[map.length + resize][map.length + resize];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				newMap[i + (resize / 2)][j + (resize / 2)] = map[i][j];
			}
		}
		map = newMap;
		currentLocation.setX(currentLocation.getX() + resize / 2);
		currentLocation.setY(currentLocation.getY() + resize / 2);
	}

	private Cord getMid() {
		return new Cord((int) (map.length / 2), (int) (map[0].length / 2));
	}

	private boolean isInRange(Cord c) {
		return !(c.getX() < map.length && c.getY() < map[0].length && c.getX() >= 0 && c.getY() >= 0);
	}

}
