package nfa.generators;

import nfa.NFA;

/**
 * Interface for various methods of generating an NFA
 * @author duncan
 *
 */
public interface NFAGenerator {
	
	public NFA generate(int n, String alphabet);
	
}
