package graph;

/**
 * A graph with |V| vertices numbered 0...|V| - 1, and a set of edges.
 * @author Duncan
 *
 * @param <E> edge type
 */

public interface EdgeGraph<E> {
	
	/**
	 * Expands the capacity, |V|, of the graph
	 * @param V
	 */
	public void expand(int V);
	
	/**
	 * Returns true if graph contains vertex
	 * @param v the vertex number
	 */
	public boolean contains(int v);
	
	/**
	 * Adds an edge to the graph
	 * @param uv
	 */
	public void addEdge(E uv);
	
	/**
	 * Returns the edge between u and v
	 */
	public E getEdge(int u, int v);
	
	/**
	 * Removes an edge from the graph
	 * @param uv the edge to remove
	 */
	public void removeEdge(E uv);
	
	/**
	 * Returns all the edges incident on v
	 */
	public Iterable<E> edgesOf(int v);
	
	/**
	 * Returns all the edges of the graph
	 */
	public Iterable<E> edges();
	
	/**
	 * Returns true if u and v are adjacent
	 * @return
	 */
	public boolean isAdjacent(int u, int v);
	
}
