package nfa;

import java.util.Random;

public class RandomDFA {

	public RandomDFA(int numState, String alphabet) {
		char[] A = alphabet.toCharArray();
		Random rand = new Random();
		for(int i = 0; i < numState; i++) {
			for(int j = 0; j < A.length; j++) {
				int a = rand.nextInt(A.length);
				
			}
		}
	}

				

}
