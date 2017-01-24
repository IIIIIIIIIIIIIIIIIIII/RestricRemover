package main.util;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import main.obj.Ores;

public class Miner {
	
	private Script s;
	
	public Miner(Script s) {
		this.s = s;
	}
	
	@SuppressWarnings("unchecked")
	public boolean mineClosest(Ores ore) {
		if(s.getDialogues().isPendingContinuation()) {
			s.getDialogues().clickContinue();
			return false;
		}
		
		RS2Object rock = s.objects.closest(new Filter<RS2Object>() {
			@Override
			public boolean match(RS2Object object) {
				if (object != null) {
					if (object.getName().contains("Rocks")) {
						short[] modColours = object.getDefinition().getModifiedModelColors();
						if (modColours != null) {
							for (short clr : modColours) {
								if (clr == ore.getModColor()) {
									return true;
								}
							}
						}
					}
				}
				return false;
			}
		});

		if (rock != null) {
			if( (!s.myPlayer().isAnimating() && !s.myPlayer().isMoving()) ) { // ( rockHasChanged || ( !Animating && !Moving && !rockHasChanged ) )
				if(rock.interact("Mine")) {
					new ConditionalSleep(3000) { 
						@Override
						public boolean condition() throws InterruptedException {
							return s.myPlayer().isAnimating(); // check if rock is missing ore by checking modified color
						}
					}.sleep();
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean mineAt(Position position) {
		return false;
	}

	public boolean canMine(Ores ore) {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public boolean dropOre(Ores ore) {
		if(ore.toString().equals(Ores.ALL.toString())) {
			s.getInventory().dropAllExcept(item -> item.getName().endsWith("pickaxe"));
			return true;
		} else
			s.getInventory().dropAll(ore.toString());
		return false;
	}

}
