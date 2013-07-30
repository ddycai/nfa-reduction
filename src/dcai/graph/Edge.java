package dcai.graph;

/**
 * A basic edge with no label or extra properties
 * @author duncan
 *
 */
public class Edge {

	protected int u, v;
	
	public Edge(int from, int to) {
		u = from;
		v = to;
	}
	
	public int from() { return u; }
	
	public int to() { return v; }
	
	public int either() { return u; }
	
	public int other(int o) {
		if(u == o) return v;
		else if(v == o) return u;
		else throw new GraphException(o + " is not a valid endpoint of edge " + toString());
	}
	
	public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Edge))
        	return false;
        Edge other = (Edge) obj;
        return u == other.u && v == other.v;
    }
	
	public int hashCode() {
		return u + v * 31;
	}
	
	public String toString() { return String.format("%d-%d", u, v); }
	
}
