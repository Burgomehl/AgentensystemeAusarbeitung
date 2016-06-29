package data;

public class SearchMethodNode implements Comparable<SearchMethodNode>{
	private Cord currentLocation;
	private SearchMethodNode linkToBestNode;
	private int wayToThisNode;
	private int value;

	public SearchMethodNode(Cord currentLocation, int value) {
		super();
		this.currentLocation = currentLocation;
		this.value = value;
	}

	public Cord getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Cord currentLocation) {
		this.currentLocation = currentLocation;
	}

	public int getWayToThisNode() {
		return wayToThisNode;
	}

	public void setWayToThisNode(int wayToThisNode) {
		this.wayToThisNode = wayToThisNode;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		return currentLocation.hashCode();
	}
	
	@Override
	public String toString() {
		return currentLocation.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			if (obj instanceof Cord) {
				Cord temp = (Cord) obj;
				return this.getCurrentLocation().equals(temp);
			} else if (obj instanceof SearchMethodNode) {
				SearchMethodNode temp = (SearchMethodNode) obj;
				return temp.getCurrentLocation().equals(this.getCurrentLocation());
			}
		}
		return false;
	}

	public SearchMethodNode getLinkToBestNode() {
		return linkToBestNode;
	}

	public void setLinkToBestNode(SearchMethodNode linkToBestNode) {
		this.linkToBestNode = linkToBestNode;
	}

	@Override
	public int compareTo(SearchMethodNode o) {
		return Integer.compare(this.getValue(), o.getValue());
	}

}
