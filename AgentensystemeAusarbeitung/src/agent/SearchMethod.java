package agent;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Predicate;

import data.Cell;
import data.Cord;
import data.MapAsArrayReloaded;
import jade.util.leap.HashSet;

public class SearchMethod {
	public static Deque<Cord> searchLikeAStar(MapAsArrayReloaded map, Cord currentLocation, Cord targetLocation,
			Predicate<Cord> decision) {
		HashSet closedList = new HashSet();
		Queue<SearchMethodNode> openList = new PriorityQueue<>();
		openList.add(new SearchMethodNode(currentLocation, getCordValue(currentLocation, targetLocation)));
		Deque<Cord> wayToBase = new LinkedList<>();
		SearchMethodNode currentNode = null;
		do {
			AbstractAgent.log.info("openList " + openList);
			currentNode = openList.remove();
			if (currentNode.getCurrentLocation().equals(targetLocation)) {
				break;
			}
			closedList.add(currentNode);
			List<Cord> neighbours = map.getNeighbours(currentNode.getCurrentLocation(), decision);
			for (Cord cord : neighbours) {
				SearchMethodNode temp = new SearchMethodNode(cord, getCordValue(cord, targetLocation));
				Cell currentField = map.getCurrentField(cord);
				if (closedList.contains(temp) || (currentField != null && currentField.isRock())) {
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

	public static Cord searchNextFieldWithDecision(MapAsArrayReloaded map, Cord currentLocation,
			Predicate<Cell> decision, Predicate<Cord> decisionForNeighbours) {
		HashSet closedList = new HashSet();
		Queue<Cord> openList = new LinkedList<>();
		openList.add(currentLocation);
		do {
			Cord currentNode = openList.poll();
			Cell currentField = map.getCurrentField(currentNode);
			if (!currentNode.equals(currentLocation)) {
				if (decision.test(currentField)) {
					return currentNode;
				}
				if (currentField != null && (currentField.isRock() || currentField.getStench() > 0)) {
					continue;
				}
			}
			closedList.add(currentNode);
			List<Cord> neighbours = map.getNeighbours(currentNode, decisionForNeighbours);
			for (Cord cord : neighbours) {
				if (closedList.contains(cord)) {
					continue;
				}
				openList.add(cord);
			}
		} while (!openList.isEmpty());
		return null;
	}

	private static int getCordValue(Cord pos, Cord target) {
		return Math.abs(target.getX() - pos.getX()) + Math.abs(target.getY() - pos.getY());
	}
}
