package main.paint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;

import main.nodes.GoblinDiplomacy;
import main.util.Utilities;

public class Paint implements ActionListener {

	private Script s;
	private GoblinDiplomacy gd;
	
	private Point mp;
	private static long runTime, startTime;
	
	public Paint(Script s, GoblinDiplomacy gd) {
		this.s = s;
		this.gd = gd;
		startTime = System.currentTimeMillis();
	}
	
	public void onPaint(Graphics2D g) {
		
		runTime = System.currentTimeMillis() - startTime;
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 200, 100);
		
		g.setColor(Color.WHITE);
		g.drawString("Runtime: " + Utilities.formatTime(runTime), 10, 20);
		g.drawString("Mining Level: " + s.getSkills().getStatic(Skill.MINING)
				+ "(+" + s.getExperienceTracker().getGainedLevels(Skill.MINING) + ")", 10, 35);
		g.drawString("Clay to mine: " + gd.getAmountOfClayToMine(), 10, 50);
		
		mp = s.getMouse().getPosition();
		g.setColor(Color.CYAN);
		g.drawLine(mp.x - 5, mp.y + 5, mp.x + 5, mp.y - 5);
		g.drawLine(mp.x + 5, mp.y + 5, mp.x - 5, mp.y - 5);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
	}

}
