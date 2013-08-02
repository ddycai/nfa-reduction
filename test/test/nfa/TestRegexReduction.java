package test.nfa;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import nfa.NFA;
import nfa.NFAReduction;
import nfa.Transition;
import nfa.generators.AbstractNFAGenerator;
import nfa.generators.RegexMethod;

import org.junit.Test;

public class TestRegexReduction {

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
	
	@Test
	public void test() {
		int nTests = 50;
		AbstractNFAGenerator gen = new RegexMethod();
		try {
			for(int i = 0; i < nTests; i++) {
				
				NFA m = gen.generate(15, 0.5, "actg");
				testReduction(m);
			}
		} catch(Exception e) {
			 fail(e + "");
		}
	}
	
	private void testReduction(NFA m) {
		Set<String> accepts, acceptsReduced;
		accepts = simulate(m);
//			System.out.println(accepts);
		NFAReduction.reduce(m);
		acceptsReduced = simulate(m);
//			System.out.println(acceptsReduced);
		for(String s : accepts) {
			if(!acceptsReduced.contains(s)) {
				System.out.println("Reduced NFA does not accept " + s);
				fail();
			}
		}
	}

}
