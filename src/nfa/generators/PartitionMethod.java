package nfa.generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import nfa.*;

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
		connect(m, 3);
		return m;
	}
	
	/**
	 * Takes an NFA that is not trim and adds transitions randomly so that it becomes trim
	 * The procedure is to partition the NFA into three sets: accessible, coaccessible, and both
	 * Then to connect these three sets together so that all states are accessible and coaccessible 
	 * @param m
	 */
	private static void connect(NFA m, int k) {
		int n = m.numStates();
		System.out.println(m);
		List<Integer> A = new ArrayList<>();
		for(int i = 0; i < n; i++)
			A.add(i);
		//DFS from initial states
		List<Integer> B = new ArrayList<>();
		boolean[] marked = mark(m);
		for(int i = 0; i < n; i++)
			if(!marked[i] && A.remove(i) != -1) {
				B.add(i);
			}
		
		//reverse DFS on final states
		m.reverse();
		List<Integer> C = new ArrayList<>();
		marked = mark(m);
		for(int i = 0; i < n; i++)
			if(!marked[i] && A.remove(i) != -1) {
				C.add(i);
			}
		
		m.reverse();
		m.trim();
		System.out.println(m);
		
		System.out.println("A: " + A);
		System.out.println("B: " + B);
		System.out.println("C: " + C);
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
		gen.generate(5, 0.25, "01");
//		Set<Integer> initialStates = new HashSet<Integer>();
//		Set<Integer> finalStates = new HashSet<Integer>();
//		NFA m = new NFA(6, "01", initialStates, finalStates);
//		initialStates.add(0);
//		finalStates.add(5);
//		m.addTransition(0, 1, '0');
//		m.addTransition(0, 2, '0');
//		m.addTransition(0, 3, '0');
//		m.addTransition(1, 5, '0');
//		m.addTransition(2, 5, '0');
//		m.addTransition(4, 5, '0');
	}

}
