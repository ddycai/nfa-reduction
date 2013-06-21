package nfa;

public class Tuple {

	private int p;
	private int q;
	
	public Tuple(int p, int q) {
		this.p = p;
		this.q = q;
	}
	
	public int p() { return p; }
	
	public int q() { return q; }
	
}
