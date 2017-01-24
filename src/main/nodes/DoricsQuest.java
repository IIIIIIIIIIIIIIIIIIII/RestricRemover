package main.nodes;

import org.osbot.rs07.api.Quests.Quest;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import main.obj.Ores;
import main.obj.Pickaxes;
import main.util.Miner;
import main.walking.Areas;

public class DoricsQuest extends Node {
	
	private Miner miner;

	public DoricsQuest(Script s, Miner miner) {
		super(s);
		this.miner = miner;
	}

	@Override
	public boolean validate() throws InterruptedException {
		if(s.getQuests().isComplete(Quest.COOKS_ASSISTANT) && s.getInventory().contains(Pickaxes.BRONZE.toString()) && !s.getQuests().isComplete(Quest.DORICS_QUEST)
				&& s.getInventory().onlyContains(Pickaxes.BRONZE.toString(), Ores.CLAY.toString(), Ores.COPPER.toString(), Ores.IRON.toString(), Ores.TIN.toString())
				&& !s.getInventory().isFull())
			return true;
		return false;
	}

	@Override
	public boolean execute() throws InterruptedException {
		
		if(s.getSkills().getStatic(Skill.MINING) < 15)
			mineTo15();
		else if(s.getInventory().getAmount("Clay") < 6)
			mineClay();
		else if(s.getInventory().getAmount("Copper ore") < 4)
			mineCopperOre();
		else if(s.getInventory().getAmount("Iron ore") < 2)
			mineIronOre();
		else
			turnInQuest();
		return false;
	}
	
	private void dropOre(Ores ore) {
		int oldAmount = (int) s.getInventory().getAmount(ore.toString()); // might not work
		if(s.getInventory().contains(ore.toString())) {
			if(s.getInventory().drop(ore.toString())) {
				new ConditionalSleep(5000) {
					@Override
					public boolean condition() throws InterruptedException {
						return oldAmount - s.getInventory().getAmount(ore.toString()) == 1;
					}
				}.sleep();
			}
		}
	}

	private void mineTo15() {
		if(!Areas.BARBARIAN_VILLAGE.contains(s.myPlayer())) {
			s.getWalking().webWalk(Areas.BARBARIAN_VILLAGE.getArea());
		} else if(s.getInventory().contains(Ores.TIN.toString())) {
			dropOre(Ores.TIN);
		} else {
			miner.mineClosest(Ores.TIN);
		}
	}
	
	private void mineClay() {
		if(s.getInventory().contains(Ores.TIN.toString()))
			dropOre(Ores.TIN);
			
		if(!Areas.RIMMINGTON_MINE_CLAY.contains(s.myPlayer()))
			s.getWalking().webWalk(Areas.RIMMINGTON_MINE_CLAY.getArea());
		else {
			miner.mineClosest(Ores.CLAY);
		}
	}
	
	private void mineCopperOre() {
		if(!Areas.RIMMINGTON_MINE_COPPER.contains(s.myPlayer()))
			s.getWalking().webWalk(Areas.RIMMINGTON_MINE_COPPER.getArea());
		else {
			miner.mineClosest(Ores.COPPER);
		}
	}
	
	private void mineIronOre() {
		if(!Areas.RIMMINGTON_MINE_IRON.contains(s.myPlayer()))
			s.getWalking().webWalk(Areas.RIMMINGTON_MINE_IRON.getArea());
		else {
			miner.mineClosest(Ores.IRON);
		}
	}
	
	private void turnInQuest() {
		if(!Areas.DORICS_HUT.contains(s.myPlayer()))
			s.getWalking().webWalk(Areas.DORICS_HUT.getArea());
		else {
			NPC doric = s.getNpcs().closest("Doric");
			if(doric != null) {
				if(!s.getDialogues().inDialogue()) {
					if(doric.interact("Talk-to")) {
						new ConditionalSleep(4000) {
							@Override
							public boolean condition() throws InterruptedException {
								return s.getDialogues().inDialogue();
							}
						}.sleep();
					}
				} else {
					if(s.getDialogues().isPendingContinuation()) {
						if(s.getDialogues().clickContinue()) {
							new ConditionalSleep(4000) {
								@Override
								public boolean condition() throws InterruptedException {
									return s.getDialogues().inDialogue();
								}
							}.sleep();
						}
					} else if(s.getDialogues().isPendingOption()) {
						try {
							if(s.getDialogues().completeDialogue("I wanted to use your anvils.", "Yes, I will get you the materials.", "Certainly, I'll be right back!")) {
								new ConditionalSleep(4000) { 
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
	}
}
