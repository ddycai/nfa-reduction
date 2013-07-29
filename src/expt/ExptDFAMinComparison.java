package expt;

import nfa.DFA;
import nfa.DFAMinimization;
import nfa.NFA;
import nfa.NFAEquivalence;
import nfa.NFAReduction;
import nfa.generators.RandomDFA;

/**
 * Generates a random DFA and runs the DFA minimization algorithm on it.
 * Then runs the NFA reduction algorithm on the DFA (since a DFA is an NFA)
 * and compares the results of the two algorithms.
 * @author duncan
 *
 */
public class ExptDFAMinComparison {

	public static void main(String[] args) {
		
		if(args.length != 3) {
			System.out.println("Usage: ExptDFAMinComparison numStates numTests alphabet");
			System.exit(0);
		}
		
		boolean debug = false;
		
		DFA original, min;
		
		int n = Integer.parseInt(args[0]);
		int goal = Integer.parseInt(args[1]);
		String alphabet = args[2];
		int[][] data = new int[goal][6];
		int count = 0;
		
		//generate a list of DFAs
		while(count < goal) {
			original = RandomDFA.generate(n, alphabet);
			data[count][0] = original.numStates();
			data[count][1] = original.size();
			min = new DFA(original);
			DFAMinimization.minimize(min);
			//if this DFA can be reduced, then also apply NFAReduction
			if(min.size() != original.size()) {
				NFA red = new NFA(original);
				NFAEquivalence eq = new NFAEquivalence(red);
				NFAReduction.reduce(eq);
				
//				if(min.size() == red.size())
//					continue;
				
				if(debug) {
					System.out.println(original.toString());
					System.out.println("---");
					System.out.println(min.toString());
					break;
				} else {
					data[count][2] = min.numStates();
					data[count][3] = min.size();
					data[count][4] = red.numStates();
					data[count][5] = red.size();
					System.out.println(
							String.format("%d,%d,%d,%d,%d,%d",
							data[count][0], data[count][1],  data[count][2], data[count][3], data[count][4], data[count][5]));
					count++;
				}
			}
		}
		
//		for(int i = 0; i < goal; i++) {
//			System.out.println(
//					String.format("%d,%d,%d,%d,%d,%d",
//					data[i][0], data[i][1],  data[i][2], data[i][3], data[i][4], data[i][5]));
//		}
	}
	
}
