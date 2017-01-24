package main.obj;

public enum Resources {
	COINS("Coins", 995, "Coins"),
	BUCKET_OF_MILK("Bucket of milk", 1927, "Bucket of milk"),
	EGG("Egg", 1944, "Egg"),
	GRAIN("Grain", 1947, "Wheat"),
	POT_OF_FLOUR("Pot of flour", 1933, "Pot of flour"),
	GOBLIN_MAIL("Goblin mail", 288, "Goblin mail"),
	ORANGE_DYE("Orange dye", 1769, "Orange dye"),
	BLUE_DYE("Blue dye", 1767, "Blue dye"),
	BLUE_GOBLIN_MAIL("Blue goblin mail", 287, "Blue goblin mail"),
	ORANGE_GOBLIN_MAIL("Orange goblin mail", 286, "Orange goblin mail");
	
	private String name;
	private int id;
	private String groundObjectName;
	
	private Resources(String name, int id, String groundObjectName) {
		this.name = name;
		this.id = id;
		this.groundObjectName = groundObjectName;
	}
	
	public int getID() {
		return id;
	}
	
	public String getGroundObjectName() {
		return groundObjectName;
	}
	
	public String toString() {
		return name;
	}
}
