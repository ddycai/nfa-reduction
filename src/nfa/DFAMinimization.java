package nfa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;

import dcai.structure.DisjointSets;

public class DFAMinimization {
	
	public static void helloWorld() {
	
	}

	/**
	 * Minimizes a DFA by finding equivalent states and merging them
	 * @param A the automaton
	 */
	public static void minimize(DFA A) {
		int n = A.numStates();
		boolean[][] table = tableFillingAlgorithm(A);
		
		DisjointSets sets = new DisjointSets(n);
		
		for(int i = 0; i < n; i++)
			for(int j = i + 1; j < n; j++)
				if(!table[i][j]) {
					sets.union(i, j);
				}

		System.out.println("Merged states: ");
		ArrayList<Set<Integer>> equivalent = sets.getSets();
		for(Set<Integer> merge : equivalent) {
			if(merge.size() == 1)
				continue;
			System.out.println(merge);
			int v = -1;
			for(int w : merge) {
				if(v == -1)
					v = w;
				else
					A.mergeRight(v, w);
			}
		}
	}

	
	/**
	 * Fills in a boolean table where table[p][q] = false if and only if p and q are equivalent states
	 * @param a
	 * @return
	 */
	private static boolean[][] tableFillingAlgorithm(DFA a) {
		int n = a.numStates();
		char[] alphabet = a.alphabet().toCharArray();
		
		boolean[][] table = new boolean[n][n];
		Queue<Tuple> queue = new ArrayDeque<>();
		for(int f : a.finalStates())
			for(int i = n - 1; i >= 0; i--)
				if(i != f && !a.finalStates().contains(i)) {
					table[f][i] = true;
					table[i][f] = true;
					queue.add(new Tuple(f, i));
				}
		
		for(int p = 0; p < n; p++)
			for(int q = p + 1; q < n; q++)
				for(int i = 0; i < alphabet.length; i++)
					if(!table[p][q] && a.delta(p, i) != a.delta(q, i)) {
						table[p][q] = true;
						table[q][p] = true;
						queue.add(new Tuple(p, q));
					}
		
		while(!queue.isEmpty()) {
			Tuple pair = queue.remove();
			int p = pair.p();
			int q = pair.q();
			for(Transition t : a.transitionsTo(p))
				for(Transition u : a.transitionsTo(q)) {
					if(t.symbol() == u.symbol()) {
						int r = t.from();
						int s = u.from();
						if(!table[r][s] && r != s) {
							table[r][s] = true;
							table[s][r] = true;
							queue.add(new Tuple(r, s));
							
						}
					}
				}
		}
		
		return table;
		
	}
	
	public static void main(String[] args) {
		
		if(args.length == 0) {
			System.out.println("Args: inFile [outFile]");
			System.exit(0);
		}
		String inFile = args[0];
		String outFile;
		if(args.length == 1)
			outFile = null;
		else
			outFile = args[1];
		
		try {
			DFA a = new DFA(new File(inFile));
			DFAMinimization.minimize(a);
			String output = a.toString();
			if(outFile != null) {
				NFAReduction.writeToFile(output, outFile);
				System.out.println("Written to " + outFile);
			} else {
				System.out.println();
				System.out.println("OUTPUT: ");
				System.out.print(output);
			}
		} catch(IOException e) {
			System.out.println(e);
		} catch(NFAException e) {
			System.out.println(e);
		}
	}
	
}
