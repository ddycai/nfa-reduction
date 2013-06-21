package test.nfa;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import nfa.*;

public class SimulateNFATest {
	
	/**
	 * Finds all strings that an NFA accepts (ignores cycles)
	 * @param M the NFA
	 * @return set of strings accepted by M
	 */
	public Set<String> simulate(NFA M) {
		Set<String> accepts = new HashSet<>();
		Set<Transition> visited = new HashSet<>();
		for(int i : M.initialStates()) {
			search(M, accepts, visited, "", i);
		}
		return accepts;
	}
	
	/**
	 * Searches every path in the NFA from a vertex
	 * Warning: running time grows exponentially!
	 * @param M the NFA
	 * @param accepts set of accepted strings
	 * @param visited set of visited edges
	 * @param string
	 * @param v the vertex to search
	 */
	private void search(NFA M, Set<String> accepts, Set<Transition> visited, String string, int v) {
		if(M.finalStates().contains(v)) {
			accepts.add(string);
		}
		for(Transition t : M.transitionsFrom(v)) {
			if(!visited.contains(t)) {
				visited.add(t);
				search(M, accepts, visited, string + t.symbol(), t.to());
				visited.remove(t);
			}
		}
	}
	
	/**
	 * Tests if the reduced NFA accepts the same strings as the original NFA
	 * @throws IOException
	 */
	public void testFiles() {
		String[] files = {"data/00.nfa", "data/01.nfa", "data/02.nfa",
				"data/03.nfa", "data/04.nfa", "data/05.nfa",
				"data/06.nfa", "data/30.nfa", "data/25.nfa", "data/07.nfa"};
		for(String f : files)
			testReduction(new Input(f, Input.FILE));
	}
	
	@Test
	public void testRandomRegex() {
		int nTests = 50;
		int length = 30;
		long sum = 0;
		try {
			RandomRegex rand = new RandomRegex(new File("data/ecoli.txt"), 0.25);
			String regex;
			long begin, end; 
			for(int i = 0; i < nTests; i++) {
				begin = System.currentTimeMillis();
				do {
					regex = rand.generate(length);
				} while(regex.length() > length*2);
				System.out.println(regex);
				testReduction(new Input(regex, Input.REGEX));
				end = System.currentTimeMillis();
				sum += end - begin;
			}
			System.out.println("Average: " + (double)sum/nTests);
		} catch(IOException e) {
			 fail(e + "");
		}
	}
	
	private void testReduction(Input in) {
		try {
			NFA m;
			Set<String> accepts, acceptsReduced;
			if(in.type == Input.FILE)
				m = new NFA(new File(in.input));
			else if(in.type == Input.REGEX)
				m = new NFA(in.input, "actg");
			else
				throw new RuntimeException("NFA input is invalid");
			accepts = simulate(m);
//			System.out.println(accepts);
			NFAEquivalence equivalence = new NFAEquivalence(m);
			NFAReduction.reduce(equivalence);
			acceptsReduced = simulate(m);
//			System.out.println(acceptsReduced);
			for(String s : accepts) {
				if(!acceptsReduced.contains(s)) {
					System.out.println("Reduced NFA does not accept " + s);
					fail();
				}
			}
		} catch(IOException e) {
			fail("IOException: " + e);
		}
	}
	
	class Input {
		static final int FILE = 0;
		static final int REGEX = 1;
		String input;
		int type;
		public Input(String s, int t) {
			input = s;
			type = t;
		}
	}
	
}
