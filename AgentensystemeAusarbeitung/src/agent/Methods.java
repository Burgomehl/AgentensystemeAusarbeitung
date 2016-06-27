package agent;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

import data.LinkedCord;
import data.MapAsArrayReloaded;

public class Methods {
	private static HashSet<LinkedCord> closedList = new HashSet<>();
	private static PriorityQueue<LinkedCord> openList;

	public static void getRoute(MapAsArrayReloaded map, LinkedCord start, LinkedCord target) {
		openList = new PriorityQueue<LinkedCord>();
		do{
			LinkedCord currentCord = openList.poll();
		}while(openList.isEmpty());
		
	}

	private static int getCordValue(LinkedCord pos, LinkedCord target) {
		return target.getX() - pos.getX() + target.getY() - pos.getY();
	}
}
