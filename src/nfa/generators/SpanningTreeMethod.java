package nfa.generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import nfa.NFA;
import nfa.NFAReduction;

import dcai.graph.Edge;
import dcai.structure.Time;

public class SpanningTreeMethod extends AbstractNFAGenerator {
	
	Time time = new Time();
	boolean[][][] adj;
	
	/**
	 * The number of transitions in the NFA will be m = n^2 * density
	 * @param density between 0 and 1 (inclusive), the transition density of the NFA
	 */
	public NFA generate(int n, double density, String alphabet) {
		adj = new boolean[n][n][alphabet.length()];
		NFA m = skeleton(n, alphabet);
		int count = m.size();
		int goal = (int)(n * n * alphabet.length() * density);
		while(count < goal) {
			int p = rand.nextInt(n);
			int q = rand.nextInt(n);
			int k = rand.nextInt(alphabet.length());
			if(!adj[p][q][k]) {
				m.addTransition(p, q, alphabet.charAt(k));
				adj[p][q][k] = true;
				count++;
			}
		}
		time.end();
		return m;
	}
	
	/**
	 * Generates a trim NFA skeleton
	 * @param n
	 * @param alphabet
	 * @return a trim NFA
	 */
	private NFA skeleton(int n, String alphabet) {
		Set<Edge> T = randomSpanningTree(0, n);
		int finalState = (int)(Math.random() * n);
		Set<Edge> finalTree = randomSpanningTree(finalState, n);
		for(Edge e : finalTree) {
			T.add(new Edge(e.to(), e.from()));
		}
		
		Set<Integer> initialStates = new HashSet<>();
		initialStates.add(0);
		Set<Integer> finalStates = new HashSet<>();
		finalStates.add(finalState);
		
		NFA m = new NFA(n, alphabet, initialStates, finalStates);
		for(Edge e : T) {
			int k = (int)(Math.random() * alphabet.length());
			m.addTransition(e.from(), e.to(), alphabet.charAt(k));
			adj[e.from()][e.to()][k] = true;
		}
		
		return m;
	}
	
	/**
	 * Creates a random spanning tree
	 * 1. initialize a tree T with node 0 as root
	 * 2. pick some vertex v not in T
	 * 3. do a random walk from v, backtracking when we reach a cycle
	 * 4. once the path reaches T, add the path into T
	 * @return a set of edges representing the spanning tree
	 */
	public static Set<Edge> randomSpanningTree(int r, int n) {
		Set<Edge> T = new HashSet<>();
		ArrayList<Integer> notInTree = new ArrayList<>();
		for(int i = 0; i < n; i++) {
			if(i != r)
				notInTree.add(i);
		}
		
		while(!notInTree.isEmpty()) {
			ArrayList<Integer> path = new ArrayList<>();
			int k = (int)(Math.random() * notInTree.size());
			path.add(notInTree.remove(k));
			int u;
			do {
				do {
					u = (int)(Math.random() * n);
				} while(path.contains(u));
				path.add(u);
			} while(notInTree.contains(u));
			for(int i = path.size() - 1; i >= 0; i--) {
				if(i != 0)
					T.add(new Edge(path.get(i), path.get(i - 1)));
				notInTree.remove((Integer)path.get(i));
			}
		}
		return T;
	}
	
	public static void main(String[] args) {
		AbstractNFAGenerator rand = new SpanningTreeMethod();
		NFA m = rand.generate(100, 0.5, "01");
		System.out.println(String.format("|V| = %d |E| = %d", m.numStates(), m.size()));
		System.out.println("Final states: " + m.finalStates());
//		System.out.println(m);
		NFAReduction.reduce(m);
		System.out.println(m);
		System.out.println(String.format("|V| = %d |E| = %d", m.numStates(), m.size()));
	}
}
