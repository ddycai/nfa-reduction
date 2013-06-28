package nfa;

import java.util.HashSet;
import java.util.Random;

public class RandomDFA {

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
