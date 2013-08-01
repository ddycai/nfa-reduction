package nfa.expt;

import java.io.*;

import nfa.NFA;
import nfa.generators.AbstractNFAGenerator;

public class ReductionExperiment {

	public void run(AbstractNFAGenerator gen, String alphabet, File outfile) {
		int[] states = {50, 100, 200, 300, 400, 500};
		double[] densities = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
		int n = 5;
		int sumV = 0;
		int sumE = 0;
		
		//TODO complete this program
		
		for(int i = 0; i < states.length; i++) {
			for(int j = 0; j < densities.length; j++) {
				for(int k = 0; k < n; k++) {
					NFA m = gen.generate(states[i], densities[j], alphabet);
					sumV += m.numStates();
					sumE += m.size();
				}
			}
		}
		
	}
	
}
