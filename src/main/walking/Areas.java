package main.walking;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;

public enum Areas {
	EGG(new Position[]{new Position(3235, 3290, 0), new Position(3233, 3290, 0), new Position(3235, 3294, 0), new Position(3233, 3294, 0)}),
	MILKING_COW(new Position[]{new Position(3256, 3271, 0), new Position(3253, 3271, 0), new Position(3253, 3277, 0), new Position(3256, 3277, 0)}),
	WHEAT_FIELD(new Position[]{new Position(3155, 3296, 0), new Position(3162, 3296, 0), new Position(3160, 3303, 0), new Position(3154, 3303, 0)}),
	LOWER_WINDMILL(new Position[]{new Position(3165, 3305, 0), new Position(3169, 3305, 0), new Position(3169, 3307, 0), new Position(3165, 3307, 0)}),
	UPPER_WINDMILL(new Position[]{new Position(3165, 3305, 2), new Position(3165, 3308, 2), new Position(3168, 3308, 2), new Position(3168, 3305, 2)}),
	LUMBRIDGE_COOK(new Position[]{new Position(3206, 3213, 0), new Position(3206, 3216, 0), new Position(3211, 3216, 0), new Position(3211, 3213, 0)}),
	BARBARIAN_VILLAGE(new Position[]{new Position(3078, 3416, 0), new Position(3078, 3425, 0), new Position(3085, 3425, 0), new Position(3085, 3416, 0)}),
	RIMMINGTON_MINE_COPPER(new Position[]{new Position(2975, 3243, 0), new Position(2975, 3250, 0), new Position(2980, 3250, 0), new Position(2980, 3243, 0)}),
	RIMMINGTON_MINE_CLAY(new Position[]{new Position(2984, 3242, 0), new Position(2984, 3238, 0), new Position(2988, 3238, 0), new Position(2988, 3242, 0)}),
	RIMMINGTON_MINE_IRON(new Position[]{new Position(2973, 3236, 0), new Position(2967, 3236, 0), new Position(2967, 3243, 0), new Position(2973, 3243, 0)}),
	DORICS_HUT(new Position[]{new Position(2950, 3452, 0), new Position(2953, 3452, 0), new Position(2953, 3449, 0), new Position(2950, 3449, 0)}),
	PORT_SARIM_DEPOSIT(new Position[]{new Position(3047, 3234, 0), new Position(3047, 3236, 0), new Position(3044, 3236, 0), new Position(3044, 3234, 0)}),
	GOBLIN_VILLAGE_HALL(new Position[]{new Position(2961, 3510, 0), new Position(2961, 3513, 0), new Position(2956, 3513, 0), new Position(2956, 3510, 0)});
	
	private Area area;
	
	private Areas(Position[] positions) {
		area = new Area(positions);
	}
	
	public Area getArea() {
		return area;
	}
	
	public boolean contains(Entity entity) {
		return area.contains(entity);
	}
	
}
