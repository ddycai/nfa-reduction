package graph;

import java.util.LinkedList;

/**
 * Undirected edge-weighted graph
 * @author Duncan
 *
 * @param <E>
 */
public class UndirectedGraph<E extends Edge>
	extends BaseGraph<E>
	implements EdgeGraph<E> {
	
	public UndirectedGraph(int V) {
		super(V);
	}
	
	/**
	 * Adds an bidirectional edge to the graph
	 */
	public void addEdge(E uv) {
		super.addEdge(uv);
		adj.get(uv.to()).add(uv);
	}
	
	/**
	 * Removes an edge from the graph
	 */
	public void removeEdge(E uv) {
		super.removeEdge(uv);
		adj.get(uv.to()).remove(uv);
	}
	
	/**
	 * Returns all the undirected edges in the graph
	 */
	public Iterable<E> edges() {
		LinkedList<E> edges = new LinkedList<E>();
		for(int u = 0; u < V; u++) {
			for(E uv : edgesOf(u))
				if(u < uv.other(u))
					edges.add(uv);
		}
		return edges;
	}
	
}
