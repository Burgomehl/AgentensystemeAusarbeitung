package agent;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Predicate;

import data.Cell;
import data.Coordinate;
import data.Map;
import data.SearchMethodNode;
import jade.util.leap.HashSet;

public class SearchMethod {
	public static Deque<Coordinate> searchLikeAStar(Map map, Coordinate currentLocation, Coordinate targetLocation,
			Predicate<Coordinate> decision) {

		HashSet closedList = new HashSet();
		Queue<SearchMethodNode> openList = new PriorityQueue<>();
		openList.add(new SearchMethodNode(currentLocation, getCordValue(currentLocation, targetLocation)));
		Deque<Coordinate> wayToBase = new LinkedList<>();
		SearchMethodNode currentNode = null;
		do {
			AbstractAgent.log.info("openList " + openList);
			currentNode = openList.remove();
			if (currentNode.getCurrentLocation().equals(targetLocation)) {
				break;
			}
			closedList.add(currentNode);
			List<Coordinate> neighbours = map.getNeighbours(currentNode.getCurrentLocation(), decision);
			for (Coordinate cord : neighbours) {
				SearchMethodNode temp = new SearchMethodNode(cord, getCordValue(cord, targetLocation));
				Cell currentField = map.getCurrentField(cord);
				if (closedList.contains(temp) || (currentField != null
						&& (currentField.isRock() || currentField.getStench() > 0 || currentField.isTrap()))) {
					continue;
				}
				if (currentField == null && !cord.equals(targetLocation)) {
					continue;
				}
				int wayFromStart = currentNode.getWayToThisNode() + 1;
				if (openList.contains(temp) && wayFromStart >= temp.getWayToThisNode()) {
					continue;
				}
				temp.setLinkToBestNode(currentNode);
				temp.setWayToThisNode(wayFromStart);
				if (openList.contains(temp)) {
					openList.remove(temp);
				}
				openList.add(temp);
			}
		} while (!openList.isEmpty());
		while (currentNode != null) {
			wayToBase.addFirst(currentNode.getCurrentLocation());
			currentNode = currentNode.getLinkToBestNode();
		}
		wayToBase.removeFirst();
		return wayToBase;
	}

	/**
	 * 
	 * @param map
	 * @param currentLocation
	 * @param decision
	 *            return condition
	 * @param decisionForNeighbours
	 *            chooses the found neighbors from the beginning
	 * @return
	 */
	public static Coordinate searchNextFieldWithDecision(Map map, Coordinate currentLocation, Predicate<Cell> decision,
			Predicate<Coordinate> decisionForNeighbours) {
		HashSet closedList = new HashSet();
		Queue<Coordinate> openList = new LinkedList<>();
		openList.add(currentLocation);
		do {
			Coordinate currentNode = openList.poll();
			Cell currentField = map.getCurrentField(currentNode);
			if (!currentNode.equals(currentLocation)) {
				if (decision.test(currentField)) {
					AbstractAgent.log.info("Would like to move to " + currentNode);
					return currentNode;
				}
				if (currentField != null && (currentField.isRock() || currentField.getStench() > 0)) {
					continue;
				}
			}
			closedList.add(currentNode);
			List<Coordinate> neighbours = map.getNeighbours(currentNode, decisionForNeighbours);
			for (Coordinate cord : neighbours) {
				if (closedList.contains(cord)) {
					continue;
				}
				Cell currentNeighbours = map.getCurrentField(cord);
				if (currentNeighbours != null && (currentNeighbours.isRock() || currentNeighbours.getStench() > 0
						|| currentNeighbours.isTrap())) {
					continue;
				}
				openList.add(cord);
			}
		} while (!openList.isEmpty());
		return null;
	}

	private static int getCordValue(Coordinate pos, Coordinate target) {
		return Math.abs(target.getX() - pos.getX()) + Math.abs(target.getY() - pos.getY());
	}
}
