package main.walking;

import org.osbot.rs07.api.map.Position;

public enum Positions {
	BARBARIAN_VILLAGE_TIN(new Position(3080, 3419, 0));
	
	private Position position;
	
	private Positions(Position position) {
		this.position = position;
	}
	
	public Position getPosition() {
		return position;
	}
}
