package nfa;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;


/**
 * Generates a random NFA
 * @author duncan
 *
 */
public class RandomNFA {

	int n;				//the number of states
	String alphabet;	//the alphabet
	double density;
	
	public RandomNFA(int numStates, String alphabet, double density) {
		n = numStates;
		this.alphabet = alphabet;
		this.density = density;
	}
	
	/**
	 * Generates a random connected NFA
	 * @return
	 */
	public NFA generate() {
		HashSet<Integer> I = new HashSet<>();
		I.add(0);
		HashSet<Integer> F = new HashSet<>();
		NFA m = new NFA(n, alphabet, I, F);
		Random rand = new Random();
		
		//generates a random, connected NFA
		int[] list = randomList();
		for(int i = 1; i < n; i++) {
			int k = rand.nextInt(i);
			int a = rand.nextInt(alphabet.length());
			char c = alphabet.charAt(a);
			m.addTransition(list[k], list[i], c);
		}
		
		//designate the final states
		int f = rand.nextInt(n);
		F.add(f);
		
		//generate random transitions
		for(int p = 0; p < n; p++)
			for(int a = 0; a < alphabet.length(); a++)
				for(int q = 0; q < n; q++) {
					if(rand.nextDouble() < density) {
						m.addTransition(p, q, alphabet.charAt(a));
					}
				}
		
		return m;
	}
	
	private static boolean hasUnreachableStates(NFA m) {
		Stack<Integer> s = new Stack<>();
		boolean[] marked = new boolean[m.numStates()];
		for(int i : m.initialStates())
			s.push(i);
		
		while(!s.isEmpty()) {
			int p = s.pop();
			marked[p] = true;
			for(Transition t : m.transitionsFrom(p))
				if(!marked[t.to()])
					s.push(t.to());
		}
		for(int i = 0; i < marked.length; i++)
			if(!marked[i])
				return true;
		return false;
	}
	
	public static boolean isTrim(NFA m) {
		if(hasUnreachableStates(m))
			return false;
		m.reverse();
		if(hasUnreachableStates(m))
			return false;
		m.reverse();
		return true;
	}
	
	/**
	 * Generates a list of consecutive integers ordered randomly
	 */
	private int[] randomList() {
		int[] list = new int[n];
		for(int i = 1; i < n; i++)
			list[i] = i;
		for(int i = 1; i < n - 1; i++) {
			int k = (int)(Math.random() * (n - i) + i);
			int temp = list[k];
			list[k] = list[i];
			list[i] = temp;
		}
		return list;
	}
	
	public static void main(String[] args) throws IOException {
		
		if(args.length != 2) {
			System.out.println("Args: length operator-density");
			System.exit(0);
		}
		
		int length = Integer.parseInt(args[0]);
		double density = Double.parseDouble(args[1]);
		
		RandomNFA rand = new RandomNFA(length, "atcg", density);
		NFA m;
//		do {
		m = rand.generate();
//		} while(!isTrim(m));
		
		System.out.println(m);
	}
	
}
