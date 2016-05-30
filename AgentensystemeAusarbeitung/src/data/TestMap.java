package data;

public class TestMap {
	public static void main(String[] args){
		Field f = new Field( 0, 0, 0, 0, 0, false, false);
		Field a = new Field(0, 0, 0, 0, 0, false, false);
		Direction[] dirs = {Direction.NORTH,Direction.SOUTH,Direction.EAST,Direction.WEST};
		for (Direction direction : dirs) {
			
		MapAsArray map = new MapAsArray(f);
		map.addNewField(a, direction);
		map.addNewField(a, direction);
		map.addNewField(a, direction);
		map.addNewField(a, direction);
		map.addNewField(a, direction);
		print(map.getMap());
		map.addNewField(a, direction);
		print(map.getMap());
		}
	}
	
	public static void print(Field[][] map){
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if(map[i][j]== null){
					System.out.print("-");
				}else{
					System.out.print("O");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
}
