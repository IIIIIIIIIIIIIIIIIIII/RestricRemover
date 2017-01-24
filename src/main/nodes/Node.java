package main.nodes;

import org.osbot.rs07.script.Script;

public abstract class Node {
	
	public Script s;
	
	public Node(Script s) {
		this.s = s;
	}
	
	public abstract boolean validate() throws InterruptedException;
	public abstract boolean execute() throws InterruptedException;

}