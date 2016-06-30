package data;

/**
 * class to parse json for topic and antWorld
 * cord and agent are for the gui and the rest for the world 
 */
public class Message {
	public String name;
	public String state;
	public String color;
	public int currentFood;
	public String totalFood;
	public String action;
	public Cell cell;
	
	public Coordinate cord;
	
	public AgentInfo agent;
	
	public String replyId;
}
