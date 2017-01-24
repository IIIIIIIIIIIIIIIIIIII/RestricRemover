package main;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;

import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import main.nodes.CooksAssistant;
import main.nodes.DoricsQuest;
import main.nodes.GoblinDiplomacy;
import main.nodes.LogOut;
import main.nodes.LumbridgeBank;
import main.nodes.Node;
import main.nodes.WestVarrockBank;
import main.paint.Paint;
import main.util.Miner;


@ScriptManifest(author = "SKENGRAT", name = "F2P Restriction remover", info = "Removes 24hr account restriction", logo = "",version = 0.6)
public class Account_Restriction_Remover extends Script {

	private ArrayList<Node> nodes = new ArrayList<Node>();
	private Paint paint;
	private Miner miner;
	
	@Override
	public void onStart() {
		log("wag1 gettin to work...");
		
		miner = new Miner(this);
		GoblinDiplomacy gd = new GoblinDiplomacy(this, miner);
		paint = new Paint(this, gd);
		
		Collections.addAll(nodes, new LumbridgeBank(this), new CooksAssistant(this), new WestVarrockBank(this), new DoricsQuest(this, miner), gd, new LogOut(this));
	}
	
	@Override
	public int onLoop() throws InterruptedException {
		for(Node node : nodes) {
			if(node.validate())
				node.execute();
		}
		return random(200, 300);
	}
	
	@Override
	public void onExit() {
		log("we good blud!");
	}

	@Override
	public void onPaint(Graphics2D g) {
		paint.onPaint(g);
	}

}
