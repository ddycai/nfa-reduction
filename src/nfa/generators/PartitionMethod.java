package nfa.generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import nfa.*;

/**
 * Generates a trim NFA
 * The procedure is to partition the NFA into three sets: A, B and C which represent
 * states that are accessible, coaccessible, and both, respectively
 * Then randomly add transitions from each state in B to A U C,
 * and from each state in C to A U B so that the NFA is trim.
 * @author duncan
 *
 */
public class PartitionMethod extends AbstractNFAGenerator {
	
	/**
	 * Generates a trim, connected NFA
	 * density is the probability of generating a transition for every pair of states, for every symbol of alphabet
	 */
	public NFA generate(int n, double density, String alphabet) {
		Set<Integer> initialStates = new HashSet<Integer>();
		Set<Integer> finalStates = new HashSet<Integer>();
		NFA m = new NFA(n, alphabet, initialStates, finalStates);
		
		//generate random transitions
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < alphabet.length(); j++) {
				for(int k = 0; k < n; k++) {
					if(Math.random() < density)
						m.addTransition(i, k, alphabet.charAt(j));
				}
			}
		}
		
		initialStates.add(0);
		finalStates.add((int)(Math.random() * n));
//		System.out.println(m);
		connect(m, 3);
		return m;
	}
	
	/**
	 * Takes an NFA that is not trim and adds transitions randomly so that it becomes trim
	 * @param m the NFA
	 * @param k the number of random edges to use in connecting sets together
	 */
	private void connect(NFA m, int k) {
		int n = m.numStates();
		List<Integer> A = new ArrayList<>();
		for(int i = 0; i < n; i++)
			A.add(i);
		//DFS from initial states
		List<Integer> B = new ArrayList<>();
		boolean[] marked = mark(m);
		for(Integer i = 0; i < n; i++)
			if(!marked[i]) {
				A.remove((Integer)i);
				B.add(i);
			}
		
		//reverse DFS on final states
		m.reverse();
		List<Integer> C = new ArrayList<>();
		marked = mark(m);
		for(Integer i = 0; i < n; i++)
			if(!marked[i]) {
				A.remove((Integer)i);
				C.add(i);
			}
		m.reverse();
		
//		System.out.println("A: " + A);
//		System.out.println("B: " + B);
//		System.out.println("C: " + C);
		
		List<Integer> AUB = new ArrayList<>(A);
		AUB.addAll(B);
		
		List<Integer> AUC = new ArrayList<>(A);
		AUC.addAll(C);
		
		//connect
		for(int vertex : C) {
			int randIndex = rand.nextInt(AUB.size());
			char randSymbol = m.alphabet().charAt(rand.nextInt(m.alphabet().length()));
			m.addTransition(vertex, AUB.get(randIndex), randSymbol);
//			System.out.println(t);
		}
		
		
		for(int vertex : B) {
			int randIndex = rand.nextInt(AUC.size());
			char randSymbol = m.alphabet().charAt(rand.nextInt(m.alphabet().length()));
			m.addTransition(AUC.get(randIndex), vertex, randSymbol);
//			System.out.println(t);
		}
		
		//System.out.println(m);
	}
	
	private static boolean[] mark(NFA m) {
		
		boolean[] marked = new boolean[m.numStates()];
		Stack<Integer> stack = new Stack<>();
		for(int i : m.initialStates()) {
			stack.push(i);
			marked[i] = true;
		}
		
		while(!stack.isEmpty()) {
			int v = stack.pop();
			for(Transition t : m.transitionsFrom(v))
				if(!marked[t.to()]) {
					stack.add(t.to());
					marked[t.to()] = true;
				}
		}
		
		return marked;
	}
	
	public static void main(String[] args) {
		AbstractNFAGenerator gen = new PartitionMethod();
		NFA m = gen.generate(1000, 0.001, "01");
		System.out.println(m);
		m.trim();
		System.out.println(m.numStates());
	}

}
