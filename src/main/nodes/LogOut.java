package main.nodes;

import org.osbot.rs07.api.Quests.Quest;
import org.osbot.rs07.script.Script;

public class LogOut extends Node {

	public LogOut(Script s) {
		super(s);
	}

	@Override
	public boolean validate() throws InterruptedException {
		return s.getQuests().isComplete(Quest.GOBLIN_DIPLOMACY);
	}

	@Override
	public boolean execute() throws InterruptedException {
		if(s.getLogoutTab().logOut()) {
			s.stop();
		}
		return false;
	}

}
