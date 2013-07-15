package expt;

import nfa.DFA;
import nfa.DFAMinimization;
import nfa.NFAEquivalence;
import nfa.NFAReduction;
import nfa.RandomDFA;

/**
 * Generates a random DFA and runs the DFA minimization algorithm on it.
 * Then runs the NFA reduction algorithm on the DFA (since a DFA is an NFA)
 * and compares the results of the two algorithms.
 * @author duncan
 *
 */
public class ExptDFAMinComparison {

	public static void main(String[] args) {
		DFA original, min;
		int goal = Integer.parseInt(args[0]);
		int n = Integer.parseInt(args[1]);
		int[][] data = new int[goal][4];
		int count = 0;
		
		String alphabet = "01";
		//generate a list of DFAs
		while(count < goal) {
			original = RandomDFA.generate(n, alphabet);
			original.trim();
			original.reverse();
			original.trim();
			original.reverse();
			original.relabel();
			min = new DFA(original);
			min.doStuff();
			DFAMinimization.minimize(min);
			//if this DFA can be reduced, then also apply NFAReduction
			if(min.size() != original.size()) {
				NFAEquivalence eq = new NFAEquivalence(original);
				NFAReduction.reduce(eq);
				data[count][0] = min.numStates();
				data[count][1] = min.size();
				data[count][2] = original.numStates();
				data[count][3] = original.size();
				count++;
			}
		}
		
		for(int i = 0; i < goal; i++) {
			System.out.println(String.format("DFA: %d %d, NFA: %d %d", data[i][0], data[i][1],  data[i][2], data[i][3]));
		}
	}
	
}
