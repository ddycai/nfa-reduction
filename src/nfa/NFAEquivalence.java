package nfa;


import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import dcai.structure.*;


/**
 * Implementation of an algorithm for calculating right-equivalence classes of an NFA
 * Takes an NFA as input, and calculates its right-equivalence, then left-equivalence by
 * reversing the automaton and applying the same algorithm
 * @author duncan
 *
 */
public class NFAEquivalence {

	private final Logger logger = Logger.getLogger(NFAEquivalence.class.getName());
	boolean DEBUG = false;
	
	private NFA M;
	private int nStates;			//number of states
	private boolean[][] table;		//true if pairs are non-equivalent states
	private DisjointSets L;			//left-equivalent states
	private DisjointSets R;			//right-equivalent states
	public boolean relabel = true;

	/**
	 * Computes the left and right equivalences of the given automaton as union-find structures
	 * @param automaton
	 */
	public NFAEquivalence(NFA automaton) {
		Time t = new Time();
		M = automaton;
		nStates = M.numStates();
		
		trim();
		M.reverse();
		trim();
		if(relabel)
			M.relabel();
		nStates = M.numStates();
		
		//compute left-equivalence (since automaton is reversed)
		t.start("Computing =L");
		L = computeEquivalence();
		t.end();
		automaton.reverse();
		
		//then compute right-equivalence
		t.start("Computing =R");
		R = computeEquivalence();
		t.end();
	}
	
	/**
	 * Returns NFA
	 */
	public NFA getNFA() { return M; }
	
	/**
	 * Returns left union-find structure
	 */
	public DisjointSets getLeft() { return L; }
	
	/**
	 * Returns right union-find structure
	 */
	public DisjointSets getRight() { return R; }
	
	/**
	 * Computes sets of right-equivalent states and returns the union-find structure
	 * @return the UnionFind structure representing the states
	 */
	public DisjointSets computeEquivalence() {
		computePairs();
		
		if(DEBUG) {
			System.out.println("Equivalence table: ");
			for(int i = 0; i < nStates; i++) {
				for(int j = 0; j < nStates; j++)
					System.out.print((table[i][j] ? "1" : "0") + " ");
				System.out.println();
			}
		}
		
		DisjointSets uf = new DisjointSets(nStates);
		for(int i = 0; i < nStates; i++)
			for(int j = i + 1; j < nStates; j++)
				if(areEquivalent(i, j)) {
					if(DEBUG)
						System.out.println(String.format("Joining %d and %d", i, j));
					uf.union(i, j);
				}
		return uf;
	}
	
	/**
	 * Performs a depth-first search from the initial states and marks visited nodes
	 * @param marked
	 */
	public void trim() {
		boolean[] marked = new boolean[nStates];
		//DFS on initial states
		Stack<Integer> stack = new Stack<Integer>();
		for(int q : M.initialStates())
			stack.push(q);
		while(!stack.isEmpty()) {
			int p = stack.pop();
			marked[p] = true;
			for(Transition t : M.transitionsFrom(p))
				if(!marked[t.to()])
					stack.push(t.to());
		}
		//remove unmarked states (not reachable thus redundant)
		for(int i = 0; i < nStates; i++)
			if(!marked[i]) {
				M.clearVertex(i);
				logger.info("Found redundant state: " + i);
			}
	}
	
	/**
	 * Computes all pairs of states that are not equivalent by the following rules:
	 * 1. final states are not equivalent to any other state
	 * 2. for all i, j, if d(i) != d(j) then i ~ j where d(i) = {a in A | d(i) is defined}
	 * 3. for all i, j, if there exists d(i, c) = i' where for all d(j, c) = j', i' ~ j' then i ~ j
	 */
	private void computePairs() {
		table = new boolean[nStates][nStates];
		
		//build the lookup table for whether a state has a transition
		boolean[][] lookup = new boolean[M.numStates()][M.alphabet().length() + 1];
		for(Transition t : M.transitions()) {
			if(t.symbol() != NFA.EPSILON) {
				int i = M.alphabet().indexOf(t.symbol());
				lookup[t.from()][i + 1] = true;
			} else {
				lookup[t.from()][0] = true;
			}
		}
		
		if(DEBUG) {
			System.out.println("Lookup table: ");
			for(int i = 0; i < lookup.length; i++) {
				for(int j = 0; j < lookup[i].length; j++)
					System.out.print((lookup[i][j] ? "1" : "0") + " ");
				System.out.println();
			}
		}
		
		System.out.println("Applying rule 1");
		//rule 1
		for(int s : M.finalStates())
			for(int i = 0; i < nStates; i++)
				if(s != i && !M.finalStates().contains(i)) {
					if(DEBUG)
						System.out.println(String.format("{%d, %d}", s, i));
					table[i][s] = true;
					table[s][i] = true;
				}

		System.out.println("Applying rule 2");
		//rule 2
		for(int i = 0; i < nStates; i++) {
			for(int j = i + 1; j < nStates; j++)
				if(areEquivalent(i, j)) {
					for(int k = 0; k <= M.alphabet().length(); k++)
						if(lookup[i][k] != lookup[j][k]) {
							
							if(DEBUG)
								System.out.println(String.format("{%d, %d}", i, j));
							
							table[i][j] = true;
							table[j][i] = true;
							break;
						}
				}
		}
		
		
		System.out.println("Applying rule 3");
		//rule 3
		Queue<Tuple> Q = new ArrayDeque<Tuple>(nStates);
		for(int i = 0; i < nStates; i++) {
			for(int j = i + 1; j < nStates; j++)
				if(!areEquivalent(i, j)) {
					rule3(Q, i, j);
					rule3(Q, j, i);
				}
		}
		//apply rule3 on new non-equivalent pairs
		while(!Q.isEmpty()) {
			Tuple pair = Q.poll();
			rule3(Q, pair.p(), pair.q());
			rule3(Q, pair.q(), pair.p());
		}
	}
	
	/**
	 * Finds non-equivalent states given two non-equivalent states p0 and q0
	 */
	public void rule3(Queue<Tuple> Q, int p0, int q0) {
		int p, q;	//states that transition to p0 & q0 respectively
		for(Transition tp : M.transitionsTo(p0)) {
			p = tp.from();
			for(Transition tq : M.transitionsTo(q0)) {
				q = tq.from();
				if(!areEquivalent(p, q) || p == q || tp.symbol() != tq.symbol())
					continue;
				char c = tp.symbol();
				boolean foundEquivalent = false;
				for(Transition t : M.transitionsFrom(q)) {
					if(t.symbol() == c && tq != t && areEquivalent(p0, t.to())) {
						foundEquivalent = true;
						break;
					}
				}
				if(foundEquivalent) continue;
				if(DEBUG)
					if(M.finalStates().contains(p) && M.finalStates().contains(q) || 
						!M.finalStates().contains(p) && !M.finalStates().contains(q))
						System.out.println(String.format("since %d != %d {%d, %d}", p0, q0, p, q));

//				System.out.format("%s has no equivalent in %s\n", tp, q);
//				System.out.format("(%d, %d) eliminated by rule 3\n", p, q);
				table[p][q] = true;
				table[q][p] = true;
				Q.add(new Tuple(p, q));
			}
		}
	}
	
	/**
	 * Checks if states u and v are equivalent
	 */
	private boolean areEquivalent(int u, int v) { return !table[u][v]; }
	
	public static void main(String[] args) {
		
		if(args.length != 1) {
			System.out.println("Usage: java NFAEquivalence inputFile");
			System.exit(0);
		}
		
		String fileName = args[0];
		try {
			NFA M = new NFA(new File(fileName));
			NFAEquivalence eq = new NFAEquivalence(M);
			
			System.out.println("=R:");
			for(Set<Integer> merge : eq.R.getSets())
				if(merge.size() >= 1)
					System.out.println(merge);
			
			System.out.println("=L");
			for(Set<Integer> merge : eq.L.getSets())
				if(merge.size() >= 1)
					System.out.println(merge);
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
}
