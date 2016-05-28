package data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import jade.util.leap.HashSet;


/**
 * Ich denke die wird es nicht werden. Aber die {@MapAsArray}
 * @author Benjamin Byl
 *
 */
public class Map {
	private Field rootField;
	private int fieldSize;
	private HashMap<String, Field> fields;

	public Map() {
		fieldSize = 0;
		fields = new HashMap<>();
	}

	public Field addField() {
		Field field = new Field(null, Direction.EAST, 0, 0, 0, 0, 0, false, false);
		fields.put(field.getRow()+""+field.getCol(), field);
		linkFields(field,null);
		++fieldSize;
		return null;
	}
	
	private void linkFields(Field field,IFunctionForLambda<Field> a){
		fields.get((field.getRow()+1)+""+field.getCol());
		fields.get((field.getRow()+1)+""+(field.getCol()+1));
		fields.get(field.getRow()+""+(field.getCol()+1));
		fields.get(field.getRow()+""+field.getCol());
	}

	public boolean isSafe(Field field) {
		return field.getStenchItensity() == 0;
	}

	public void analyseStench(Field field, IFunctionForLambda<Field> test) {
		if (field.getStenchItensity() == 3) {
			field.setNorth(setFieldAsTrap(field.getNorth(), field, Direction.NORTH));
			field.setSouth(setFieldAsTrap(field.getSouth(), field, Direction.SOUTH));
			field.setEast(setFieldAsTrap(field.getNorth(), field, Direction.EAST));
			field.setWest(setFieldAsTrap(field.getNorth(), field, Direction.WEST));
		} else if (field.getStenchItensity() == 2) {

		}
	}

	private Field setFieldAsTrap(Field fieldToTrap, Field theCurrentField, Direction dir) {
		if (fieldToTrap == null) {
			return new Field(theCurrentField, dir, 0, 0, 1, theCurrentField.getRow() + 1, theCurrentField.getCol(),
					false, true);
		}
		return fieldToTrap;
	}

	/**
	 * @return List of food on the map ordered by distance to the base
	 */
	public Queue<Field> findFoodOrderedByDistance() {
		return findSomethingOrderedByDistance(rootField, (a)->a.getFood()>0);
	}
	
	public Queue<Field> findRocksOrderedByDistance() {
		return findSomethingOrderedByDistance(rootField, (a)->a.isRock());
	}
	
	public Queue<Field> findTrapsOrderedByDistance() {
		return findSomethingOrderedByDistance(rootField, (a)->a.isTrap());
	}

	/**
	 * @param currentField
	 * @return List of food on the map ordered by distance to the given location
	 */
	private Queue<Field> findSomethingOrderedByDistance(Field currentField, IBool func) {
		Queue<Field> fieldWithSomething = new LinkedList<>();
		HashSet allreadyVisited = new HashSet();
		Queue<Field> toVisit = new LinkedList<>();
		toVisit.add(rootField);
		while (!toVisit.isEmpty()) {
			Field field = toVisit.poll();
			allreadyVisited.add(field);
			addTestedFieldToToVisit(toVisit, rootField.getNorth());
			addTestedFieldToToVisit(toVisit, rootField.getEast());
			addTestedFieldToToVisit(toVisit, rootField.getSouth());
			addTestedFieldToToVisit(toVisit, rootField.getWest());
			 if (func.function(field)) {
				 fieldWithSomething.add(field);
			 }
		}
		return fieldWithSomething;
	}

	private void addTestedFieldToToVisit(Queue<Field> toVisit, Field field) {
		if (field != null) {
			toVisit.add(field);
		}
	}
}
