package graph;

import java.util.*;

/**
 * Basic edge-weighted graph implementation by an adjacency list
 * @author Duncan
 *
 * @param <E> edge type
 */
public abstract class BaseGraph<E extends Edge>
	implements EdgeGraph<E> {
	
	protected int V;			//number of vertices
	protected int E;			//number of edges
	protected List<List<E>> adj;	//adjacency list representation that maps vertices to edges
	
	/**
	 * Creates an empty graph with V vertices
	 */
	public BaseGraph(int V) {
		this.V = V;
		this.adj = new ArrayList<List<E>>(V);
		for(int i = 0; i < V; i++)
			adj.add(new ArrayList<E>());
		E = 0;
	}
	
	/**
	 * Expands the number of vertices in the graph
	 */
	public void expand(int capacity) {
		if(V > capacity)
			return;
		for(int i = V; i < capacity; i++)
			adj.add(new ArrayList<E>());
		V = capacity;
	}
	
	/**
	 * @return the number of vertices
	 */
	public int order() { return V; }
	
	/**
	 * @return the number of edges
	 */
	public int size() { return E; }
	
	/**
	 * Adds an edge
	 * @param uv
	 */
	public void addEdge(E uv) {
		int u = uv.from();
		int v = uv.to();
		if(!contains(u)) throw new GraphException(u + " is not a vertex in this graph");
		if(!contains(v)) throw new GraphException(v + " is not a vertex in this graph");
		adj.get(u).add(uv);
		E++;
	}
	
	/**
	 * Remove an edge object
	 */
	public void removeEdge(E uv) {
		int u = uv.from();
		adj.get(u).remove(uv);
		E--;
	}
	
	/**
	 * Return the edge between vertices u and v
	 */
	public E getEdge(int u, int v) {
		if(!contains(u)) throw new GraphException(u + " is not a vertex in this graph");
		if(!contains(v)) throw new GraphException(v + " is not a vertex in this graph");
		Collection<E> list = adj.get(u);
		for(E uv : list)
			if(uv.other(u) == v)
				return uv;
		return null;
	}
	
	/**
	 * Returns all edges incident on v
	 * @param v
	 */
	public Collection<E> edgesOf(int v) {
		if(!contains(v)) throw new GraphException(v + " is not a vertex in this graph");
		return adj.get(v);
	}
	
	/**
	 * Checks if two vertices are adjacent to each other
	 */
	public boolean isAdjacent(int u, int v) {
		return (getEdge(u, v) != null);
	}
	
	/**
	 * Checks if graph contains vertex
	 */
	public boolean contains(int v) {
		return v >= 0 && v < V;
	}
	
}
