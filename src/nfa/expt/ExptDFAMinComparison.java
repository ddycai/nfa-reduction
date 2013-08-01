package nfa.expt;

import nfa.DFA;
import nfa.DFAMinimization;
import nfa.NFA;
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
			
			NFA red = new NFA(original);
			NFAReduction.reduce(red);
			
			if(min.size() != red.size()) {
				System.out.println("HUGE ERROR");
				System.out.println(original);
				System.exit(0);
			}
			
			//if this DFA can be reduced, then also apply NFAReduction
			if(min.numStates() < original.numStates()) {
				
//				if(min.size() == red.size())
//					continue;
				data[count][2] = min.numStates();
				data[count][3] = min.size();
				data[count][4] = red.numStates();
				data[count][5] = red.size();
				double dN = (double)min.numStates()/red.numStates();
				double dM = (double)min.numStates()/red.numStates();
				System.out.println(
						String.format("%d\t%d\t%d\t%d\t%d\t%d\t%f\t%f",
						data[count][0], data[count][1],  data[count][2], data[count][3], data[count][4], data[count][5],
						dN, dM));
				count++;
			}
		}
		
//		for(int i = 0; i < goal; i++) {
//			System.out.println(
//					String.format("%d,%d,%d,%d,%d,%d",
//					data[i][0], data[i][1],  data[i][2], data[i][3], data[i][4], data[i][5]));
//		}
	}
	
}
