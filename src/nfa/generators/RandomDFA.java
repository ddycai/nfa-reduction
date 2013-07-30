package nfa.generators;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import nfa.DFA;

/**
 * Static utility class for generating random DFAs
 * Uses algorithm from 'On the performance of automata minimization algorithms'
 * @author duncan
 *
 */
public class RandomDFA {

	/**
	 * Generates a random DFA
	 * The generated DFA is trimmed and thus will not have the 
	 * @param numState number of states
	 * @param alphabet String specifying the alphabet
	 * @return a random DFA
	 */
	public static DFA generate(int numState, String alphabet) {
		Random rand = new Random();
		HashSet<Integer> finalStates = new HashSet<>();
		DFA m = new DFA(numState, alphabet, 0, finalStates);
		Queue<Integer> queue = new LinkedList<>();
		
		boolean[] visited = new boolean[numState];
		queue.add(m.initialState());
		visited[m.initialState()] = true;
		
		while(!queue.isEmpty()) {
			int p = queue.poll();
			for(int i = 0; i < alphabet.length(); i++) {
				int q = rand.nextInt(numState);
				m.addTransition(p, q, alphabet.charAt(i));
				if(!visited[q]) {
					queue.add(q);
					visited[q] = true;
				}
			}
		}
		
		//select final states
		for(int p = 0; p < numState; p++) {
			if(rand.nextBoolean())
				finalStates.add(p);
		}
		
		//finalStates.add(rand.nextInt(numState));
		
		m.trim();
		m.reverse();
		m.trim();
		m.reverse();
		m.relabel();
		
		return m;
	}

	public static void main(String[] args) {
		if(args.length != 2) {
			System.out.println("Usage: RandomDFA length alphabet");
		}
		DFA m = RandomDFA.generate(Integer.parseInt(args[0]), args[1]);
		System.out.println(m);
	}

}
