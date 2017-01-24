package main.obj;

public enum Ores {
	ALL("ALL", 0, 0),
	CLAY("Clay", 434, 6705),
	COPPER("Copper ore", 436, 4645),
	TIN("Tin ore", 438, 53),
	IRON("Iron ore", 440, 2576),
	SILVER("Silver ore", 442, 74),
	COAL("Coal ore", 453, 10508),
	GOLD("Gold ore", 444, 8885),
	MITHRIL("Mithril ore", 447, -22239),
	ADAMANTITE("Adamantite ore", 449, 21662),
	RUNITE("Runite ore", 451, -31437);
	
	private String name;
	private int id;
	private int modColor;
	
	private Ores(String name, int id, int modColor) {
		this.name = name;
		this.id = id;
		this.modColor = modColor;
	}
	
	public int getID() {
		return id;
	}
	
	public int getModColor() {
		return modColor;
	}
	
	public String toString() {
		return name;
	}
	
}