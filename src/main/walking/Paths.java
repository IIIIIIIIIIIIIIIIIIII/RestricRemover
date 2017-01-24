package main.walking;

import java.util.ArrayList;
import java.util.Arrays;

import org.osbot.rs07.api.map.Position;

public enum Paths {
	EAST_TO_WEST_MINE(new ArrayList<Position>(Arrays.asList(new Position(3186, 3373, 0), new Position(3287, 3364, 0), new Position(3274, 3371, 0), new Position(3271, 3358, 0), new Position(3273, 3349, 0), 
			new Position(3265, 3340, 0), new Position(3257, 3339, 0), new Position(3247, 3337, 0), new Position(3236, 3337, 0), new Position(3226, 3343, 0), new Position(3224, 3353, 0), 
		    new Position(3215, 3357, 0), new Position(3209, 3362, 0), new Position(3193, 3370, 0))));
	
	private ArrayList<Position> path;
	
	private Paths(ArrayList<Position> path) {
		this.path = path;
	}
	
	public ArrayList<Position> getPath() {
		return path;
	}
}
