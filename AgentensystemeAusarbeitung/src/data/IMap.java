package data;

/**
 * just offer a array[][] of {@Field}
 * @author Benjamin Byl
 *
 */
public interface IMap {
	public Field[][] getMap();
	public void addNewField(Field field, Direction dir);
}
