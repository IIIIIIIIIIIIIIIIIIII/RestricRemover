package main.obj;

public enum Pickaxes {
	BRONZE("Bronze pickaxe", 1265);
	
	private String name;
	private int id;
	
	private Pickaxes(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
	public String toString() {
		return name;
	}
}
