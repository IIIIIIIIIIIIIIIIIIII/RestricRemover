package main.nodes;

import java.util.List;

import org.osbot.rs07.api.Quests.Quest;
import org.osbot.rs07.api.filter.NameFilter;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import main.obj.Containers;
import main.obj.Pickaxes;
import main.obj.Resources;
import main.walking.Areas;

public class CooksAssistant extends Node {

	public CooksAssistant(Script s) {
		super(s);
	}

	@Override
	public boolean validate() throws InterruptedException {
		if(!s.getQuests().isComplete(Quest.COOKS_ASSISTANT) && s.getInventory().onlyContains(Resources.EGG.toString(), Containers.POT.toString(), Containers.BUCKET.toString(), 
				Resources.BUCKET_OF_MILK.toString(), Resources.POT_OF_FLOUR.toString(), Resources.GRAIN.toString(), Pickaxes.BRONZE.toString())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean execute() throws InterruptedException {
		
		if(s.getConfigs().get(29) == 1)
			turnInQuest();
		else if(!s.getInventory().contains(Resources.EGG.toString()))
			getEgg();
		else if(!s.getInventory().contains(Resources.BUCKET_OF_MILK.toString()))
			getBucketMilk();
		else if(!s.getInventory().contains(Resources.POT_OF_FLOUR.toString()))
			getPotFlour();
		else
			turnInQuest();
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private void getEgg() {
		if(!Areas.EGG.contains(s.myPlayer()))
			s.getWalking().webWalk(Areas.EGG.getArea());
		else {
			List<GroundItem> items = s.getGroundItems().filter(s.getGroundItems().getAll(), new NameFilter<GroundItem>(Resources.EGG.toString()));
			int i = Script.random(0, items.size()-1); // changed from item.size() -> item.size() - 1
			if(items.get(i) != null) {
				if(items.get(i).interact("Take")) {
					new ConditionalSleep(10000) { 
						@Override
						public boolean condition() throws InterruptedException {
							return s.getInventory().contains(Resources.EGG.toString());
						}
					}.sleep();
				}
			}
		}
	}
	
	private void getBucketMilk() {
		if(!Areas.MILKING_COW.contains(s.myPlayer()))
			s.getWalking().webWalk(Areas.MILKING_COW.getArea());
		else {
			RS2Object cow = s.getObjects().closest("Dairy cow");
			if(cow.interact("Milk")) {
				new ConditionalSleep(10000) {
					@Override
					public boolean condition() throws InterruptedException {
						return s.getInventory().contains(Resources.BUCKET_OF_MILK.toString());
					}
				}.sleep();
			}
		}
	}
	
	private void getPotFlour() {
		if(s.getConfigs().get(695) == 1)
			getFlourFromBin();
		else if(!s.getInventory().contains(Resources.GRAIN.toString()))
			getGrain();
		else {
			if(s.myPlayer().getZ() == 0) {
				if(!Areas.LOWER_WINDMILL.contains(s.myPlayer()))
					s.getWalking().webWalk(Areas.LOWER_WINDMILL.getArea());
				else
					climbWindMill("Climb-up");
			} else if(s.myPlayer().getZ() == 1)
				climbWindMill("Climb-up");
			else
				useWindMill();
		}
	}
	
	private void getGrain() {
		if(!Areas.WHEAT_FIELD.contains(s.myPlayer())) {
			s.getWalking().webWalk(Areas.WHEAT_FIELD.getArea());
		} else {
			RS2Object wheat = s.getObjects().closest(Resources.GRAIN.getGroundObjectName());
			if(wheat != null) {
				if(wheat.interact("Pick")) {
					new ConditionalSleep(10000) { 
						@Override
						public boolean condition() throws InterruptedException {
							return s.getInventory().contains(Resources.GRAIN.toString());
						}
					}.sleep();
				}
			}
		}
	}
	
	private void climbWindMill(String climbDirection) {
		int currentFloor = s.myPlayer().getZ();
		RS2Object ladder = s.getObjects().closest("Ladder");
		if(ladder != null) {
			if(ladder.interact(climbDirection)) {
				new ConditionalSleep(10000) {
					@Override
					public boolean condition() throws InterruptedException {
						return currentFloor - s.myPlayer().getZ() != 0;
					}
				}.sleep();
			}
		}
	}
	
	private void getFlourFromBin() {
		if(s.myPlayer().getZ() != 0)
			climbWindMill("Climb-down");
		else if(!Areas.LOWER_WINDMILL.contains(s.myPlayer()))
			s.getWalking().webWalk(Areas.LOWER_WINDMILL.getArea());
		else {
			RS2Object flourBin = s.getObjects().closest("Flour bin");
			if(flourBin != null) {
				if(flourBin.interact("Empty")) {
					new ConditionalSleep(10000) {
						@Override
						public boolean condition() throws InterruptedException {
							return s.getInventory().contains(Resources.POT_OF_FLOUR.toString());
						}
					}.sleep();
				}
			}
		}
	}

	// rewrite checking for animation to determine when to use hopper contols and a boolean for if its put grain in the hopper
	private void useWindMill() { // need to figure out how to determine if the hopper has grain in it
		if(s.getInventory().interact("Use", Resources.GRAIN.toString())) {
			RS2Object hopper = s.getObjects().closest("Hopper");
			if(hopper != null) {
				if(hopper.interact("Use")) {
					new ConditionalSleep(10000) {
						@Override
						public boolean condition() throws InterruptedException {
							return !s.getInventory().contains(Resources.GRAIN.toString());
						}
					}.sleep();
					
					try {
						Script.sleep(Script.random(2000, 4000));	// find a way to remove static random time
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					RS2Object hopperControls = s.getObjects().closest("Hopper controls");
					if(s.getConfigs().get(695) == 0) {
						if(hopperControls.interact("Operate")) {
							new ConditionalSleep(10000) {
								@Override
								public boolean condition() throws InterruptedException {
									return s.getConfigs().get(695) == 1;
								}
							}.sleep();
						}
					}
				}
				
			}
		}
	}
	
	private void turnInQuest() {						// fix up logic for clicking specific options
		if(!Areas.LUMBRIDGE_COOK.contains(s.myPlayer()))
			s.getWalking().webWalk(Areas.LUMBRIDGE_COOK.getArea());
		else {
			NPC cook = s.getNpcs().closest("Cook");
			if(cook != null) {
				if(!s.getDialogues().inDialogue()) {
					if(cook.interact("Talk-to")) {
						new ConditionalSleep(10000) { 
							@Override
							public boolean condition() throws InterruptedException {
								return s.getDialogues().inDialogue();
							}
						}.sleep();
					}
				} else {
					if(s.getDialogues().isPendingContinuation()) {
						if(s.getDialogues().clickContinue()) {
							new ConditionalSleep(10000) { 
								@Override
								public boolean condition() throws InterruptedException {
									return s.getDialogues().inDialogue();					// probably not right
								}
							}.sleep();
						}
					} else if(s.getDialogues().isPendingOption()){
						try {
							if(s.getDialogues().completeDialogue("What's wrong?", "I'm always happy to help a cook in distress.", "Actually, I know where to find this stuff.")) {
								new ConditionalSleep(10000) { 
									@Override
									public boolean condition() throws InterruptedException {
										return s.getDialogues().inDialogue();					// probably not right
									}
								}.sleep();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		if(s.getWidgets().isVisible(277, 15)) {
			if(s.getWidgets().get(277, 15).interact()) {
				new ConditionalSleep(10000) { 
					@Override
					public boolean condition() throws InterruptedException {
						return !s.getWidgets().get(277, 15).isVisible();
					}
				}.sleep();
			}
		}
	}
}
