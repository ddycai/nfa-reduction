package nfa;

import dcai.graph.*;

/**
 * Represents a transition
 * @author duncan
 *
 */
public class Transition extends Edge {

	private char symbol;
	boolean reversed;
	
	public Transition(int u, int v, char c) {
		super(u, v);
		symbol = c;
		reversed = false;
	}
	
	/**
	 * Copy constructor
	 */
	public Transition(Transition t) {
		this(t.u, t.v, t.symbol);
		reversed = t.reversed;
	}
	
	public void reverse() { reversed = !reversed; }
	
	public char symbol() { return symbol; }
	
	public int from() {
		return reversed ? v : u;
	}
	
	public int to() {
		return reversed ? u : v;
	}
	
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Transition))
        	return false;
        Transition other = (Transition) obj;
        return symbol == other.symbol && u == other.u && v == other.v;
    }
	
	public String toString() {
		return String.format("%d -> %d [label=\"%c\"];", from(), to(), symbol());
	}
	
}
