package expt;

import nfa.*;

public class ExptNFAReduction {

	public static void main(String[] args) {
		int expts = 10;
		int n = 5;
		
		int[][] data = new int[expts][4];
		
		NFA m;
		RandomNFA rand = new RandomNFA(n, "atcg", 10);
		NFAEquivalence e;
		
		for(int i = 0; i < expts; i++) {
			m = rand.generate();
			data[i][0] = m.numStates();
			data[i][1] = m.size();
			e = new NFAEquivalence(m);
			NFAReduction.reduce(e);
			data[i][2] = m.numStates();
			data[i][3] = m.size();
		}
		
		for(int i = 0; i < expts; i++) {
			System.out.println(String.format("%d\t%d\t%d\t%d", data[i][0], data[i][1], data[i][2], data[i][3]));
		}
	}
	
}
