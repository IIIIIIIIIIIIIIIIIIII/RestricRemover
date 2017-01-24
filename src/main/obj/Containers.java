package main.obj;

public enum Containers {
	BUCKET("Bucket", 1925),
	POT("Pot", 1931);
	
	private String name;
	private int id;
	
	private Containers(String name, int id) {
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
