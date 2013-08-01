package dcai.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import dcai.structure.Time;


/**
 * Hopcroft-Karp algorithm for maximum bipartite matching
 * Based on pseudocode from Wikipedia
 * Only accepts 1-index graphs.
 * @author Duncan
 *
 */

public class BipartiteMatching {

	public static int INF = Integer.MAX_VALUE;
	public static int NIL = 0;		//vertex that emulates a "sink" in a flow network
	
	boolean DEBUG = false;
	
	private UndirectedGraph<Edge> G;
	private Set<Edge> matching;
	private int n;
	private int value;
	private int[] match;	//matching
	private int[] dist;		//layers
	
	
	/**
	 * Finds the maximum matching given a bipartite graph, and an integer n,
	 * that represents the first vertex in the R-partition
	 * @param bipartiteGraph
	 * @param n
	 */
	public BipartiteMatching(UndirectedGraph<Edge> bipartiteGraph, int n) {
		this.n = n;
		G = bipartiteGraph;
		match = new int[G.order()];
		for(int i = 1; i < match.length; i++)
			match[i] = NIL;
		dist = new int[G.order()];
		hopcroftKarp();
	}
	
	/**
	 * Returns the bipartite graph
	 */
	public UndirectedGraph<Edge> getGraph() { return G; }
	
	/**
	 * Checks if vertex is part of a matching
	 */
	public boolean isMatched(int v) { return match[v] != NIL; }
	
	/**
	 * Returns an array of matchings where match[v] is v's matching
	 * match[v] = 0 if v is not matched
	 */
	public int getMatch(int v) { return match[v]; }
	
	/**
	 * Returns a set of edges representing the matching
	 */
	public Set<Edge> getMatching() {
		if(matching != null)
			return matching;
		matching = new HashSet<>();
		for(int v = 1; v < n; v++)
			if(match[v] != 0)
				matching.add(G.getEdge(v, match[v]));
		return matching;
	}
	
	/**
	 * Returns the size of the matching
	 */
	public int size() { return value; }
	
	/**
	 * Breadth-first search that generates layers
	 */
	private boolean bfs() {
		Queue<Integer> q = new ArrayDeque<Integer>();
		
		//initialize distances
		for(int i = 1; i < n; i++) {
			if(match[i] == NIL) {
				dist[i] = 0;
				q.add(i);
			} else
				dist[i] = INF;
		}
		
		dist[NIL] = INF;
		
		while(!q.isEmpty()) {
			int u = q.poll();
			if(DEBUG) System.out.format("Exploring %d\n", u);
			for(Edge e : G.edgesOf(u)) {
				int v = e.other(u);
				//if the matching is not visited, visit it
				//note that if v has no match, it is a free vertex in R
				if(dist[match[v]] == INF) {
					if(DEBUG) System.out.format("%d->%d->%d\n", u, v, match[v]);
					dist[match[v]] = dist[u] + 1;
					if(match[v] != NIL)
						q.add(match[v]);
				}
			}
		}
		
		//did we visit a free vertex in R?
		return dist[NIL] != INF;
	}
	
	/**
	 * Depth-first search on a free vertex, u, following the layers
	 * @param u a free vertex
	 */
	private boolean dfs(int u) {

		if(DEBUG) System.out.format("DFS on %d\n", u);
		
		for(Edge e : G.edgesOf(u)) {
			int v = e.other(u);
			if(dist[match[v]] == dist[u] + 1) {
				//we either have found a free vertex in R
				//or we've found a matched vertex in R
				if(match[v] == NIL || dfs(match[v])) {
					if(DEBUG) System.out.format("matching found (%d, %d)\n", u, v);
					match[v] = u;
					match[u] = v;
					return true;
				}
			}
		}
			
		//at this point, we cannot find an augmenting path on u
		//set dist[u] to infinity so that we don't visit it on another dfs
		dist[u] = INF;
		return false;
	}
	
	public String toString() {
		Set<Edge> matching = getMatching();
		StringBuilder s = new StringBuilder();
		s.append("graph {\n");
		s.append("\tnode [shape=circle, color=black, fontcolor=black];\n");
		for(Edge uv : G.edges()) {
			if(matching.contains(uv))
				s.append(String.format("\t%d -- %d [color=blue];\n", uv.from(), uv.to()));
			else
				s.append(String.format("\t%d -- %d;\n", uv.from(), uv.to()));
		}
		s.append("}");
		return s.toString();
	}
	
	/**
	 * Finds a maximum matching
	 */
	private void hopcroftKarp() {
		value = 0;
		//while there are augmenting paths (can reach 0-node)
		while(bfs()) {
			//augment on those paths
			for(int i = 1; i < n; i++) {
				if(match[i] == NIL) {
					if(dfs(i))
						value++;
				}
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		UndirectedGraph<Edge> G = new UndirectedGraph<Edge>(0);
		int n = -1;
		Scanner sc = new Scanner(new File("BG.txt"));
		int V = sc.nextInt();
		int tmp;
		n = sc.nextInt();
		G.expand(V);
		int E = sc.nextInt();
		for(int i = 0; i < E; i++) {
			int a = sc.nextInt();
			int b = sc.nextInt();
			if(b < n) {
				tmp = b;
				a = b;
				b = tmp;
			}
			G.addEdge(new Edge(a, b));
		}
		sc.close();
		
		Time T = new Time();
		T.start();
		
		BipartiteMatching F = new BipartiteMatching(G, n);
		
		for(int i = 1; i < F.n; i++)
			if(F.match[i] != 0)
				System.out.println(i + "->" + F.match[i]);
		
		System.out.println("|M| = " + F.value);
		
		T.end();
		
	}

}
