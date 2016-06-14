package agent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import data.Cord;
import data.MapAsArrayReloaded;

public class Methods {
	private static HashSet<Cord> vistedFields = new HashSet<>();

	public static void getRoute(MapAsArrayReloaded map, Cord a, Cord b) {
		if (!vistedFields.contains(a)) {
			vistedFields.add(a);
		}
		if (!vistedFields.contains(b)) {
			vistedFields.add(b);
		}
		List<Cord> fields = new ArrayList<>();
		fields.addAll(map.getNeighbours(a));
		fields.addAll(map.getNeighbours(b));
		for (Cord cord : fields) {

		}
	}
}
