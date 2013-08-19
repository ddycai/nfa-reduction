package nfa.expt;

import java.util.Scanner;

import nfa.NFA;
import nfa.NFAReduction;
import nfa.generators.*;

/**
 * Generates states by a certain method and reduces them, then generates a table of data that shows the reduction percentage
 * @author duncan
 *
 */
public class ReductionExperiment {

	public static void run(AbstractNFAGenerator gen, String alphabet, int[] states, double[] densities, int nTests) {
		double[] vAvg = new double[densities.length];
		double[] eAvg = new double[densities.length];
		
		double[] vReduction = new double[nTests];
		double[] eReduction = new double[nTests];
		int sum[][] = new int[nTests][2];
		
		double vSum, eSum, navg, mavg;
		
		for(int i = 0; i < states.length; i++) {
			System.out.println("-------------------------");
			System.out.println(states[i] + " states");
			System.out.println("-------------------------");
			System.out.println("d	n-avg	m-avg	n-std	m-std	%n-avg	%m-avg	%n-std	%m-std");
			for(int j = 0; j < densities.length; j++) {
				vSum = eSum = navg = mavg = 0;
				for(int k = 0; k < nTests; k++) {
					NFA m = gen.generate(states[i], densities[j], alphabet);
					int order = m.numStates();
					int size = m.size();
					sum[k][0] = m.numStates();
					sum[k][1] = m.size();
					navg += order;
					mavg += size;
					NFAReduction.reduce(m);
					vReduction[k] = (double)m.numStates()/order;
					eReduction[k] = (double)m.size()/size;
					vSum += vReduction[k];
					eSum += eReduction[k];
				}
				double vStd = 0, eStd = 0, nStd = 0, mStd = 0;
				navg /= nTests;
				mavg /= nTests;
				vAvg[j] = vSum / nTests;
				eAvg[j] = eSum / nTests;
				for(int k = 0; k < nTests; k++) {
					vStd += Math.pow(vAvg[j] - vReduction[k], 2);
					eStd += Math.pow(eAvg[j] - eReduction[k], 2);
				}
				for(int k = 0; k < nTests; k++) {
					nStd += Math.pow(navg - sum[k][0], 2);
					mStd += Math.pow(mavg - sum[k][1], 2);
				}
				vStd = Math.sqrt(vStd/(nTests - 1));
				eStd = Math.sqrt(eStd/(nTests - 1));
				nStd = Math.sqrt(nStd/(nTests - 1));
				mStd = Math.sqrt(mStd/(nTests - 1));
				System.out.println(String.format("%.2f	%.1f	%.1f	%.1f	%.1f	%.4f	%.4f	%.4f	%.4f",
					densities[j], navg, mavg, nStd, mStd, vAvg[j], eAvg[j], vStd, eStd));
			}
		}
		
	}
	
	public static void main(String[] args) {
		String[] tokens;
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Alphabet: ");
		String alphabet = sc.nextLine();
		System.out.println("States (separate by space): ");
		tokens = sc.nextLine().split(" ");
		int[] states = new int[tokens.length];
		for(int i = 0; i < states.length; i++)
			states[i] = Integer.parseInt(tokens[i]);
		
		System.out.println("Densities (separate by space): ");
		tokens = sc.nextLine().split(" ");
		double[] densities = new double[tokens.length];
		for(int i = 0; i < densities.length; i++)
			densities[i] = Double.parseDouble(tokens[i]);
		
		System.out.println("Number of tests: ");
		int nTests = sc.nextInt();
		sc.nextLine();

		AbstractNFAGenerator g = null;
		
		do {
			System.out.println("Method (spanning, partition, regex): ");
			String method = sc.nextLine();
			
			if(method.equals("spanning")) {
				g = new SpanningTreeMethod();
			} else if(method.equals("regex")) {
				g = new RegexMethod();
			} else if(method.equals("partition")) {
				g = new PartitionMethod();
			} else {
				System.out.println("Invalid method. Please try again.");
			}
		} while(g == null);
		sc.close();
		
		run(g, alphabet, states, densities, nTests);
	}
	
}
