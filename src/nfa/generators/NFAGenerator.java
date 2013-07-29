package nfa.generators;

import nfa.NFA;

public interface NFAGenerator {
	
	public NFA generate(int numStates, String alphabet);
	
}
