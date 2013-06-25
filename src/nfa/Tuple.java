package nfa;

public class Tuple {

	private final int p;
	private final int q;
	
	public Tuple(int p, int q) {
		this.p = p;
		this.q = q;
	}
	
	public int p() { return p; }
	
	public int q() { return q; }
	
	public boolean equals(Object obj) {
		if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Tuple))
            return false;
        Tuple other = (Tuple)obj;
		return p == other.p && q == other.q;
	}
	
	public int hashCode() {
		return p * 31 + q;
	}
	
	public String toString() {
		return p + " " + q;
	}
	
}
