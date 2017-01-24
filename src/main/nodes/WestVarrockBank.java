package main.nodes;

import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.Quests.Quest;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import main.obj.Ores;
import main.obj.Pickaxes;
import main.obj.Resources;

public class WestVarrockBank extends Node {
	
	public WestVarrockBank(Script s) {
		super(s);
	}

	@Override
	public boolean validate() throws InterruptedException {
		if( ( s.getQuests().isComplete(Quest.COOKS_ASSISTANT) && !s.getQuests().isComplete(Quest.DORICS_QUEST) && !s.getInventory().onlyContains(Pickaxes.BRONZE.toString(), Ores.CLAY.toString(), Ores.COPPER.toString(), Ores.IRON.toString(), Ores.TIN.toString(), Resources.GOBLIN_MAIL.toString(), Resources.ORANGE_DYE.toString(), Resources.BLUE_DYE.toString(), Resources.COINS.toString()) ) 
			|| ( s.getQuests().isComplete(Quest.COOKS_ASSISTANT) && !s.getInventory().contains(Pickaxes.BRONZE.toString()) && !s.getQuests().isComplete(Quest.DORICS_QUEST) )
			|| ( s.getQuests().isComplete(Quest.COOKS_ASSISTANT) && s.getSkills().getStatic(Skill.MINING) >= 15 && !s.getQuests().isComplete(Quest.DORICS_QUEST) && s.getInventory().isFull())
			|| ( s.getQuests().isComplete(Quest.DORICS_QUEST) && !s.getInventory().onlyContains(Pickaxes.BRONZE.toString(), Resources.GOBLIN_MAIL.toString(), Resources.BLUE_DYE.toString(), Resources.ORANGE_DYE.toString(), Resources.COINS.toString(), Ores.CLAY.toString(), Resources.ORANGE_GOBLIN_MAIL.toString(), Resources.BLUE_GOBLIN_MAIL.toString()) )
			|| ( s.getQuests().isComplete(Quest.DORICS_QUEST) && ((s.getInventory().contains(Pickaxes.BRONZE.toString()) && s.getInventory().getAmount(Pickaxes.BRONZE.toString()) >= 2) || !s.getInventory().contains(Pickaxes.BRONZE.toString())) ) 
			)
			return true;
		return false;
	}

	@Override
	public boolean execute() throws InterruptedException {
		if(!Banks.VARROCK_WEST.contains(s.myPlayer())) {
			s.getWalking().webWalk(Banks.VARROCK_WEST);
		} else {
			Bank bank = s.getBank();
			if(!bank.isOpen()) {
				bank.open();
				new ConditionalSleep(10000) { 
					@Override
					public boolean condition() throws InterruptedException {
						return bank.isOpen();
					}
				}.sleep();
			} else if(s.getQuests().isComplete(Quest.DORICS_QUEST)) {
				if(bank.depositAll()) {
					new ConditionalSleep(10000) { 
						@Override
						public boolean condition() throws InterruptedException {
							return s.getInventory().isEmpty();
						}
					}.sleep();
				}
				Script.sleep(Script.random(4000, 6000));
				if(bank.getAmount(Resources.COINS.toString()) >= 9000
						&& bank.withdraw(Resources.COINS.toString(), (int)bank.getAmount(Resources.COINS.toString()))) {
					new ConditionalSleep(5000) { 
						@Override
						public boolean condition() throws InterruptedException {
							return s.getInventory().contains(Resources.COINS.toString());
						}
					}.sleep();
				}
				Script.sleep(Script.random(4000, 6000));
				if(bank.withdraw(Pickaxes.BRONZE.toString(), 1)) {
					new ConditionalSleep(10000) { 
						@Override
						public boolean condition() throws InterruptedException {
							return s.getInventory().contains(Pickaxes.BRONZE.toString());
						}
					}.sleep();
				}
				if(bank.close()) {
					new ConditionalSleep(5000) {
						@Override
						public boolean condition() throws InterruptedException {
							return !s.getBank().isOpen();
						}
					}.sleep();
				}
			} else if(s.getQuests().isComplete(Quest.COOKS_ASSISTANT) && s.getSkills().getStatic(Skill.MINING) >= 15) {
				if(bank.depositAll()) {
					new ConditionalSleep(10000) { 
						@Override
						public boolean condition() throws InterruptedException {
							return s.getInventory().isEmpty();
						}
					}.sleep();
				}
				if(bank.withdraw(Pickaxes.BRONZE.toString(), 1)) {
					new ConditionalSleep(10000) { 
						@Override
						public boolean condition() throws InterruptedException {
							return s.getInventory().contains(Pickaxes.BRONZE.toString());
						}
					}.sleep();
				}
				if(bank.contains(Ores.CLAY.toString())) {
					if(bank.withdraw(Ores.CLAY.toString(), 6)) {
						new ConditionalSleep(10000) { 
							@Override
							public boolean condition() throws InterruptedException {
								return s.getInventory().contains(Ores.CLAY.toString());
							}
						}.sleep();
					}
				}
				if(bank.contains(Ores.COPPER.toString())) {
					if(bank.withdraw(Ores.COPPER.toString(), 4)) {
						new ConditionalSleep(10000) { 
							@Override
							public boolean condition() throws InterruptedException {
								return s.getInventory().contains(Ores.COPPER.toString());
							}
						}.sleep();
					}
				}
				if(bank.contains(Ores.IRON.toString())) {
					if(bank.withdraw(Ores.IRON.toString(), 2)) {
						new ConditionalSleep(10000) { 
							@Override
							public boolean condition() throws InterruptedException {
								return s.getInventory().contains(Ores.IRON.toString());
							}
						}.sleep();
					}
				}
				bank.close();
			}
		}
		return false;
	}
}
