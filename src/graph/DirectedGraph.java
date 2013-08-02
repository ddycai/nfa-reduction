package graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Directed edge-weighted graph
 * @author Duncan
 *
 * @param <E> edge type
 */
public class DirectedGraph<E extends Edge>
	extends BaseGraph<E> implements EdgeGraph<E> {

	protected List<List<E>> adjR;	//reverse adjacency list
	
	/**
	 * Initializes a graph
	 * @param V the number of vertices
	 */
	public DirectedGraph(int V) {
		super(V);
		adjR = new ArrayList<List<E>>(V);
		for(int i = 0; i < V; i++)
			adjR.add(new ArrayList<E>());
	}
	
	/**
	 * Adds an edge to the graph
	 */
	public void addEdge(E uv) {
		super.addEdge(uv);
		adjR.get(uv.to()).add(uv);
	}
	
	/**
	 * Removes an edge from the graph
	 */
	public void removeEdge(E uv) {
		super.removeEdge(uv);
		adjR.get(uv.to()).remove(uv);
	}
	
	/**
	 * Removes all incoming and outgoing edges on a vertex
	 */
	public void clearVertex(int v) {
		if(!contains(v)) throw new GraphException(v + " is not a vertex in this graph");
		Iterator<E> it = edgesFrom(v).iterator();
		while(it.hasNext()) {
			E uv = it.next();
			adjR.get(uv.to()).remove(uv);
			it.remove();
			E--;
		}
		it = edgesTo(v).iterator();
		while(it.hasNext()) {
			E uv = it.next();
			adj.get(uv.from()).remove(uv);
			it.remove();
			E--;
		}
	}
	
	/**
	 * Finds the transpose of the graph (reverses all the edges)
	 */
	public void transpose() {
		List<List<E>> tmplist = adj;
		adj = adjR;
		adjR = tmplist;
		for(Edge e : edges())
			e.reverse();
	}
	
	/**
	 * Calculates an array mapping old labels to new labels, removed labels have entry -1
	 * Warning: changes the fields of Edge
	 */
	public int[] relabel() {
		int[] map = new int[V];
		Stack<Integer> remove = new Stack<>();
		
		if(indegree(0) == 0 && outdegree(0) == 0) {
			map[0] = -1;
		} else
			map[0] = 0;
		
		for(int v = 1; v < V; v++) {
			if(indegree(v) == 0 && outdegree(v) == 0) {
				remove.push(v);
				map[v] = map[v - 1];
			} else
				map[v] = map[v - 1]  + 1;
		}
		
		while(!remove.isEmpty()) {
			int v = remove.pop();
			adj.remove(v);
			adjR.remove(v);
			map[v] = -1;
			V--;
		}
	
		for(E e : edges()) {
			e.u = map[e.u];
			e.v = map[e.v];
		}
		
		return map;
	}
	
	/**
	 * Removes all the edges in the graph
	 */
	public void clear() {
		for(List<E> list : adj)
			list.clear();
		for(List<E> list : adjR)
			list.clear();
		E = 0;
	}
	
	
	/**
	 * Returns all the edges leaving vertex v
	 */
	public Collection<E> edgesFrom(int v) {
		if(!contains(v)) throw new GraphException(v + " is not a vertex in this graph");
		return adj.get(v);
	}
	
	/**
	 * Returns all the edges going to vertex v
	 */
	public Collection<E> edgesTo(int v) {
		if(!contains(v)) throw new GraphException(v + " is not a vertex in this graph");
		return adjR.get(v);
	}
	
	/**
	 * Returns the indegree of a vertex
	 */
	public int indegree(int v) {
		if(!contains(v)) throw new GraphException(v + " is not a vertex in this graph");
		return adjR.get(v).size();
	}
	
	/**
	 * Returns the outdegree of a vertex
	 */
	public int outdegree(int v) {
		if(!contains(v)) throw new GraphException(v + " is not a vertex in this graph");
		return adj.get(v).size();
	}
	
	/**
	 * Returns all the edges in the graph
	 */
	public Collection<E> edges() {
		ArrayList<E> edges = new ArrayList<E>(E);
		for(Collection<E> list : adj)
			for(E uv : list)
				edges.add(uv);
		return edges;
	}
	
}
