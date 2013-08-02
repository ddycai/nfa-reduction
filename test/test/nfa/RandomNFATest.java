package test.nfa;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

import nfa.*;
import nfa.generators.*;

import org.junit.Test;

public class RandomNFATest {
	
	static int lengthcap = 70;
	static Set<Transition> path = new HashSet<>();
	
	@Test
	public void test() {
		AbstractNFAGenerator gen = new RegexMethod();
		NFA m0 = gen.generate(50, 0.25, "actg");
		NFA m = new NFA(m0);
		NFAReduction.reduce(m);
		
		String s;
		int n = 0;		//number of strings
		for(int i = 0; i < n; i++) {
			System.out.println(String.format("%d (%f)", i, (double)i/n));
			s = randomTraversal(m);
//			System.out.println(s);
			assertTrue(m.accepts(s));
		}
	}
	
	
	public void testRegex() throws IOException {
		int length = 30;
		AbstractNFAGenerator gen = new RegexMethod();
		NFA m0 = gen.generate(length, 0.25, "actg");
		NFA m = new NFA(m0);
		NFAReduction.reduce(m);
		
		String s;
		int n = 10;		//number of strings
		for(int i = 0; i < n; i++) {
			s = randomTraversal(m);
			System.out.println(s);
			assertTrue(m.accepts(s));
		}
	}
	
	public String randomTraversal(NFA m) {
		path.clear();
		String s;
		for(int i : m.initialStates()) {
			s = randomTraversal(m, path, "", i);
			if(s != null)
				return s;

		}
		return null;
	}
	
	private String randomTraversal(NFA m, Set<Transition> path, String s, int p) {
		ArrayList<Transition> rand = new ArrayList<>();
		if(s.length() > lengthcap)
			return null;
		if(m.isFinal(p))
			return s;
		//pick some random transitions
		rand.addAll(m.transitionsFrom(p));
		Collections.shuffle(rand);
		for(Transition t : rand) {
			if(path.contains(t))
				continue;
			path.add(t);
			if(t.symbol() != NFA.EPSILON)
				s = s + t.symbol();
			String attempt = randomTraversal(m, path, s, t.to());
			if(attempt != null)
				return attempt;
			path.remove(t);
		}
		return null;
	}
}
