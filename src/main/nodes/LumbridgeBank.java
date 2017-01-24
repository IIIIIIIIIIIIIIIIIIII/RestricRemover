package main.nodes;

import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.Quests.Quest;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import main.obj.Containers;
import main.obj.Pickaxes;
import main.obj.Resources;

public class LumbridgeBank extends Node {

	public LumbridgeBank(Script s) {
		super(s);
	}

	@Override
	public boolean validate() throws InterruptedException {
		if((!s.getInventory().onlyContains(Resources.EGG.toString(), Containers.POT.toString(), Containers.BUCKET.toString(), Resources.BUCKET_OF_MILK.toString(),
				Resources.POT_OF_FLOUR.toString(), Resources.GRAIN.toString(), Pickaxes.BRONZE.toString())
				|| s.getInventory().isEmpty()) && !s.getQuests().isComplete(Quest.COOKS_ASSISTANT))
			return true;
		return false;
	}

	@Override
	public boolean execute() throws InterruptedException {
		s.log("Visiting Lumbridge Bank now!");

		if(!Banks.LUMBRIDGE_UPPER.contains(s.myPlayer())) {
			s.getWalking().webWalk(Banks.LUMBRIDGE_UPPER);
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
			} else if(!s.getQuests().isComplete(Quest.COOKS_ASSISTANT)) {
				bank.depositAll();
				new ConditionalSleep(10000) {
					@Override
					public boolean condition() throws InterruptedException {
						return s.getInventory().isEmpty();
					}
				}.sleep();
				Script.sleep(Script.random(2000, 4000));
				bank.withdraw(Containers.POT.toString(), 1);
				s.log("Withdrawing pot");
				new ConditionalSleep(10000) {
					@Override
					public boolean condition() throws InterruptedException {
						return s.getInventory().contains(Containers.POT.toString());
					}
				}.sleep();
				Script.sleep(Script.random(2000, 4000));
				bank.withdraw(Containers.BUCKET.toString(), 1);
				s.log("Withdrawing bucket");
				new ConditionalSleep(10000) {
					@Override
					public boolean condition() throws InterruptedException {
						return s.getInventory().contains(Containers.BUCKET.toString());
					}
				}.sleep();
				Script.sleep(Script.random(2000, 4000));
				bank.withdraw(Pickaxes.BRONZE.toString(), 1);
				s.log("Withdrawing pickaxe");
				new ConditionalSleep(10000) {
					@Override
					public boolean condition() throws InterruptedException {
						return s.getInventory().contains(Pickaxes.BRONZE.toString());
					}
				}.sleep();
			}
		}
		return false;
	}
	
}
