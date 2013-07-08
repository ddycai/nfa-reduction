package nfa;

import java.util.HashSet;
import java.util.Random;

/**
 * Static utility class for generating random DFAs
 * @author duncan
 *
 */
public class RandomDFA {

	/**
	 * Generates a random DFA; the generated DFA is currently not guaranteed to be trim or connected
	 * @param numState number of states
	 * @param alphabet String specifying the alphabet
	 * @return a random DFA
	 */
	public static DFA generate(int numState, String alphabet) {
		
		Random rand = new Random();
		HashSet<Integer> finalStates = new HashSet<>();
		DFA m = new DFA(numState, alphabet, 0, finalStates);
		
		for(int p = 0; p < numState; p++) {
			for(int i = 0; i < alphabet.length(); i++) {
				int q = rand.nextInt(numState);
				m.addTransition(p, q, alphabet.charAt(i));
			}
			if(rand.nextBoolean())
				finalStates.add(p);
		}
		return m;
	}

	public static void main(String[] args) {
		DFA m = RandomDFA.generate(10, "actg");
		System.out.println(m);
	}

}
