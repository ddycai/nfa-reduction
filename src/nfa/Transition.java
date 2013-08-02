package nfa;

import graph.*;

/**
 * Represents a transition
 * @author duncan
 *
 */
public class Transition extends Edge {

	private char symbol;
	
	public Transition(int u, int v, char c) {
		super(u, v);
		symbol = c;
	}
	
	/**
	 * Copy constructor
	 */
	public Transition(Transition t) {
		this(t.from(), t.to(), t.symbol);
	}
	
	public char symbol() { return symbol; }
	
    public boolean equals(Object obj) {
    	if(super.equals(obj)) {
    		if (!(obj instanceof Transition))
            	return false;
    		Transition other = (Transition) obj;
    		return symbol == other.symbol;
    	} else
    		return false;
    }
	
	public String toString() {
		return String.format("%d -> %d [label=\"%c\"];", from(), to(), symbol());
	}
	
}
