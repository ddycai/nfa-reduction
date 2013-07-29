package expt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import nfa.NFA;
import nfa.NFAEquivalence;
import nfa.NFAReduction;
import nfa.generators.RandomRegex;

/**
 * Given a length, density and nTests,
 * generates nTests NFAs from random regular expressions and reduces them.
 * Then calculates the average and standard deviation.
 * @author duncan
 *
 */
public class ExptRegexReduction {
	
	File text;
	String alphabet;
	
	public ExptRegexReduction(File text, String alphabet) {
		this.text = text;
		this.alphabet = alphabet;
	}
	
	public DataSet runexpt(int nTests, int length, double density) throws IOException {
		RandomRegex rand = new RandomRegex(text, density);
		int[][] data = new int[nTests][4];
		double[][] reduced = new double[nTests][2];

		NFA m;
		
		String filename = String.format("expt-data/%s-%d-%.2f.txt", alphabet, length, density);
		System.out.println(filename);
		PrintWriter writer = new PrintWriter(new FileWriter(new File(filename)));
		
		for(int i = 0; i < nTests; i++) {
			String regex = rand.generate(length);
			writer.println(regex);
			m = new NFA(regex, "actg");
			data[i][0] = m.numStates();
			data[i][1] = m.size();
			NFAEquivalence e = new NFAEquivalence(m);
			NFAReduction.reduce(e);
			data[i][2] = m.numStates();
			data[i][3] = m.size();
		}
		
		//calculate % change in number of states/transitions
		for(int i = 0;i < nTests; i++) {
			reduced[i][0] = (double)data[i][2]/data[i][0];
			reduced[i][1] = (double)data[i][3]/data[i][1];
//			writer.println(String.format("%d,%d,%d,%d,%f,%f",
//					data[i][0], data[i][1],  data[i][2],  data[i][3], reduced[i][0], reduced[i][1]));
		}
		double sumV = 0;
		double sumE = 0;
		//calculate average
		for(int i = 0; i < nTests; i++) {
			sumV += reduced[i][0];
			sumE += reduced[i][1];
		}
		double Vaverage = sumV/nTests;
		double Eaverage = sumE/nTests;
		
		//calculate standard deviation
		sumV = sumE = 0;
		for(int i = 0; i < nTests; i++) {
			sumV += Math.pow(Vaverage - reduced[i][0], 2);
			sumE += Math.pow(Eaverage - reduced[i][1], 2);
		}
		
		double Vstddev = Math.sqrt(sumV/(nTests - 1));
		double Estddev = Math.sqrt(sumE/(nTests - 1));
		
//		writer.println("V(avg)," + Vaverage);
//		writer.println("E(avg)," + Eaverage);
//		writer.println("V(dev)," + Vstddev);
//		writer.println("E(dev)," + Estddev);
		
		writer.close();
		return new DataSet(Vaverage, Eaverage, Vstddev, Estddev);
	}
	
	public static void main(String[] args) throws IOException {
		
		if(args.length == 0){
			System.out.println("Usage: nTests");
			System.exit(0);
		}
		
		int nTests = Integer.parseInt(args[0]);
		boolean optimize = false;
		
		ExptRegexReduction rand = new ExptRegexReduction(new File("data/ecoli.txt"), "atcg");
		
		if(args.length > 1 && args[1].equals("optimize")) {
			optimize = true;
		}
		
//		rand.runexpt(nTests, 10, 0.3);
		int[] sizes = {
				10, 25, 50, 100, 250, 500, 750, 1000
		};
//		double[] densities = {
//			0.1, 0.2, 0.3, 0.4, 0.5, 0.75, 0.9	
//		};
		DataSet[][] data = new DataSet[sizes.length][10];
		
		double start = System.currentTimeMillis();
		for(int i = 0; i < sizes.length; i++) {
			for(int j = 1; j < 10; j++) {
				if(optimize && 500000/Math.pow(sizes[i], 2) < (double)j/10)
					break;
				data[i][j - 1] = rand.runexpt(nTests, sizes[i], (double)j/10);
			}
		}
		double end = System.currentTimeMillis();
		System.out.println((end - start));
		
		PrintWriter[] writers = new PrintWriter[4];
		writers[0] = new PrintWriter(new FileWriter(new File(String.format("expt-results/%s-Vaverage.csv", rand.alphabet))));
		writers[1] = new PrintWriter(new FileWriter(new File(String.format("expt-results/%s-Eaverage.csv", rand.alphabet))));
		writers[2] = new PrintWriter(new FileWriter(new File(String.format("expt-results/%s-Vstddev.csv", rand.alphabet))));
		writers[3] = new PrintWriter(new FileWriter(new File(String.format("expt-results/%s-Estddev.csv", rand.alphabet))));
		
		for(int i = 1; i <= 10; i++) {
			for(int j = 0; j < 4; j++)
				writers[j].print(String.format(",%.2f", (double)(i*10)/100));
		}
		for(int j = 0; j < 4; j++)
			writers[j].println();
		
		for(int i = 0; i < data.length; i++) {
			for(int k = 0; k < 4; k++)
				writers[k].print(sizes[i]);
			for(int j = 0; j < data[i].length; j++) {
				for(int k = 0; k < 4; k++) {
					if(data[i][j] != null)
						writers[k].print("," + data[i][j].data[k]);
					else
						writers[k].print(",");
				}
			}
			for(int k = 0; k < 4; k++)
				writers[k].println();
		}
		
		for(int k = 0; k < 4; k++)
			writers[k].close();
		
		System.out.println("FINISHED.");
		
	}
	
	class DataSet {
		public double[] data = new double[4];
		public DataSet(double Vaverage, double Eaverage, double Vstddev, double Estddev) {
			data[0] = Vaverage;
			data[1] = Eaverage;
			data[2] = Vstddev;
			data[3] = Estddev;
		}
	}
	
}
