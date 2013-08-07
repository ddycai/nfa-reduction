package nfa.expt;

import java.io.*;

import nfa.NFA;
import nfa.NFAReduction;
import nfa.generators.*;

public class ReductionExperiment {

	public static void run(AbstractNFAGenerator gen, String alphabet, File outfile) {
		int[] states = {500};
		double[] densities = {0.01, 0.02, 0.03, 0.04, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
		int nTests = 5;
		double[] vAvg = new double[densities.length];
		double[] eAvg = new double[densities.length];
		
		double[] vReduction = new double[nTests];
		double[] eReduction = new double[nTests];
		
		double vSum, eSum;
		
		//TODO complete this program
		
		System.out.println("d	n-avg	m-avg	n-std	m-std");
		
		for(int i = 0; i < 1; i++) {
			for(int j = 0; j < densities.length; j++) {
				vSum = eSum = 0;
				for(int k = 0; k < nTests; k++) {
					NFA m = gen.generate(states[i], densities[j], alphabet);
					int order = m.numStates();
					int size = m.size();
					NFAReduction.reduce(m);
					vReduction[k] = (double)m.numStates()/order;
					eReduction[k] = (double)m.size()/size;
					vSum += vReduction[k];
					eSum += eReduction[k];
				}
				double vStd = 0;
				double eStd = 0;
				vAvg[j] = vSum / nTests;
				eAvg[j] = eSum / nTests;
				for(int k = 0; k < nTests; k++) {
					vStd += Math.pow(vAvg[j] - vReduction[k], 2);
					eStd += Math.pow(eAvg[j] - eReduction[k], 2);
				}
				vStd = Math.sqrt(vStd/(nTests - 1));
				eStd = Math.sqrt(eStd/(nTests - 1));
				System.out.println(String.format("%.2f	%.4f	%.4f	%.4f	%.4f", densities[j], vAvg[j], eAvg[j], vStd, eStd));
			}
		}
		
	}
	
	public static void main(String[] args) {
		run(new RegexMethod(), "01", null);
	}
	
}
