package nfa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.Map.Entry;

import dcai.graph.*;

/**
 * Represents a nondeterministic finite automaton (NFA) with a directed graph
 * @author duncan
 *
 */
public class NFA {
	
	public static final char EPSILON = 'ɛ';
	
	private DirectedGraph<Transition> G;
	protected String alphabet;			//alphabet
	protected Set<Integer> I;	//initial states
	protected Set<Integer> F;	//final states
	
	private boolean reversed = false;		//true if automaton is reversed
	
	/**
	 * Constructs an NFA
	 * @param numStates the number of states
	 * @param alphabet a string specifying the alphabet where each character is a symbol
	 * @param initialStates set of initial states
	 * @param finalStates set of final states
	 */
	public NFA(int numStates, String alphabet, Set<Integer> initialStates, Set<Integer> finalStates) {
		G = new DirectedGraph<>(numStates);
		this.alphabet = alphabet;
		I = initialStates;
		F = finalStates;
	}
	
	/**
	 * Builds an NFA from a regular expression
	 * @param regex
	 * @param alphabet
	 */
	public NFA(String regex, String alphabet) {
		this.alphabet = alphabet;
		regex = '(' + regex + ')';
		char[] re = regex.toCharArray();
		
		G = new DirectedGraph<>(re.length + 1);
		
		I = new HashSet<>(1);
		I.add(0);
		F = new HashSet<>(1);
		F.add(re.length);
		
		Stack<Integer> stack = new Stack<>();
		Stack<Integer> orstack = new Stack<>();
		
		int lp = 0;		//top left parentheses
		for(int i = 0; i < re.length; i++) {
			if(re[i] == '(' || re[i] == '|')
				stack.push(i);
			else if(re[i] == ')') {
				int or = stack.pop();
				while(re[or] != '(') {
					orstack.push(or);
					or = stack.pop();
				}
				lp = or;	
				while(!orstack.isEmpty()) {
					or = orstack.pop();
					addTransition(or, i, EPSILON);
					addTransition(lp, or + 1, EPSILON);
				}
			}
			if(i < re.length - 1 && re[i + 1] == '*') {
				if(re[i] == ')') {
					addTransition(i + 1, lp, EPSILON);
					addTransition(lp, i + 1, EPSILON);
				} else {
					addTransition(i, i + 1, EPSILON);
					addTransition(i + 1, i, EPSILON);
				}					
			}
			if(re[i] == '(' || re[i] == '*' || re[i] == ')')
				addTransition(i, i + 1, EPSILON);
			else if(re[i] != '|')
				addTransition(i, i + 1, re[i]);
		}
	}
	
	/**
	 * Reads an NFA from a Reader object
	 * Input format: Input begins with a line containing a single integer, n, specifying the number
	 * of states (including state 0), followed by a line of characters (no space) specifying the alphabet.
	 * The next line contains a set of integers separated by spaces specifying the initial states,
	 * followed by another line containing a set of integers separated by spaces specifying the final states.
	 * The rest of lines contain two integers, a and b, and a character, c, separated by spaces,
	 * indicating that the transition function maps state a to state b through symbol c where
	 * 0 <= a, b < n and c belongs to the alphabet.
	 * @param file the filename
	 * @throws IOException
	 */
	public NFA(Reader in) throws IOException {
		BufferedReader br = new BufferedReader(in);
		String line;
		line = br.readLine();
		//read no. of states
		int numStates = Integer.parseInt(line);
		alphabet = br.readLine();
		
		//read alphabet
		String[] parts;
		
		I = new HashSet<Integer>();
		F = new HashSet<Integer>();
		
		//read initial states
		line = br.readLine();
		parts = line.split(" ");
		for(int i = 0; i < parts.length; i++)
			I.add(Integer.parseInt(parts[i]));
		
		//read final states
		line = br.readLine();
		parts = line.split(" ");
		for(int i = 0; i < parts.length; i++)
			F.add(Integer.parseInt(parts[i]));
		
		G = new DirectedGraph<>(numStates);
		
		//read transitions
		while((line = br.readLine()) != null) {
			if(line.isEmpty())
				continue;
			parts = line.split(" ");
			int a = Integer.parseInt(parts[0]);
			int b = Integer.parseInt(parts[1]);
			if(parts.length == 2) {
				addTransition(a, b, EPSILON);
			} else {
				char c = parts[2].charAt(0);
				addTransition(a, b, c);
			}
		}
		//System.out.println("Done.");
		br.close();
	}
	
	/**
	 * Reads an NFA from file
	 */
	public NFA(File file) throws IOException {
		this(new FileReader(file));
	}
	
	/**
	 * Copy constructor
	 * @param m the NFA to copy
	 */
	public NFA(NFA m) {
		alphabet = m.alphabet;
		I = new HashSet<>();
		for(int i : m.initialStates())
			I.add(i);
		F = new HashSet<>();
		for(int i : m.finalStates())
			F.add(i);
		G = new DirectedGraph<>(m.numStates());
		for(Transition t : m.transitions())
			G.addEdge(new Transition(t));
	}
	
	public void trim() {
		boolean[] marked = new boolean[numStates()];
		//DFS on initial states
		Stack<Integer> stack = new Stack<Integer>();
		for(int q : initialStates())
			stack.push(q);
		while(!stack.isEmpty()) {
			int p = stack.pop();
			marked[p] = true;
			for(Transition t : transitionsFrom(p))
				if(!marked[t.to()])
					stack.push(t.to());
		}
		//remove unmarked states (not reachable thus redundant)
		for(int i = 0; i < numStates(); i++)
			if(!marked[i]) {
				clearVertex(i);
				//logger.info("Found redundant state: " + i);
			}
	}
	
	/**
	 * Simulates the string on the NFA
	 * @param s the input string
	 * @return whether the NFA accepts the input string
	 */
	public boolean accepts(String s) {
		Stack<Integer> stack = new Stack<>();
		stack.addAll(I);
		Set<Integer> marked = epsilonDfs(stack);
//		System.out.println(marked);
		for(int i = 0; i < s.length(); i++) {
			int c = s.charAt(i);			//accept a character
			for(int q : marked)
				for(Transition t : transitionsFrom(q))
					if(t.symbol() == c)
						stack.add(t.to());
			marked = epsilonDfs(stack);
//			System.out.println(marked);
		}
		for(int q : marked)
			if(F.contains(q))
				return true;
		return false;
	}
	
	/**
	 * Given a stack of source nodes, marks all nodes reachable via an epsilon transition.
	 * @param stack source nodes
	 * @return a set of marked nodes
	 */
	private Set<Integer> epsilonDfs(Stack<Integer> stack) {
		Set<Integer> marked = new HashSet<>();
		while(!stack.isEmpty()) {
			int q = stack.pop();
			marked.add(q);
			for(Transition t : transitionsFrom(q))
				if(!marked.contains(t.to())) {
					if(t.symbol() == EPSILON)
						stack.add(t.to());
				}
		}
		return marked;
	}
	
	/**
	 * @return true if automaton is reversed, false otherwise
	 */
	public boolean isReversed() {
		return reversed;
	}
	
	/**
	 * Makes the automaton behave in reverse
	 */
	public void reverse() {
		reversed = !reversed;
		
		Set<Integer> tmp = I;
		I = F;
		F = tmp;
		
		G.complement();
		for(Transition t : G.edges())
			t.reverse();
	}
	
	/**
	 * Adds a transition to the NFA
	 */
	public void addTransition(int u, int v, char symbol) {
		int i = alphabet.indexOf(symbol);
		if(i == -1 && symbol != EPSILON)
				throw new NFAException("Symbol (" + symbol  + ") not part of the alphabet.");
		Transition uv = new Transition(u, v, symbol);
		checkAddTransition(uv, i);
		//prevent duplicates
		if(!transitionsFrom(uv.from()).contains(uv))
			G.addEdge(uv);
	}
	
	protected void checkAddTransition(Transition t, int i) { }
	
	/**
	 * Gets the alphabet
	 * @return the alphabet
	 */
	public String alphabet() {
		return alphabet;
	}
	
	/**
	 * The number of states in the automaton
	 */
	public int numStates() {
		return G.V();
	}
	
	/**
	 * The number of transitions in the automaton
	 * @return
	 */
	public int size() {
		return G.E();
	}
	
	/**
	 * Checks if state is a final state
	 */
	public boolean isFinal(int state) {
		return F.contains(state);
	}
	
	/**
	 * Checks if state is an initial state
	 */
	public boolean isInitial(int state) {
		return I.contains(state);
	}
	
	/**
	 * Returns the set of initial states
	 */
	public Set<Integer> initialStates() {
		return I;
	}
	
	/**
	 * Returns the set of final states
	 */
	public Set<Integer> finalStates() {
		return F;
	}
	
	/**
	 * Removes all transitions involving a vertex
	 * @param v
	 */
	public void clearVertex(int v) {
		G.clearVertex(v);
	}
	
	/**
	 * @return a set of all transitions in the graph
	 */
	public Collection<Transition> transitions() {
		return G.edges();
	}
	
	/**
	 * Returns p : for all a in A, d(p, a)
	 * @param p the state number
	 * @return all transitions ending on a state
	 */
	public Collection<Transition> transitionsTo(int p) {
		return G.edgesTo(p);
	}
	
	/**
	 * Returns q : for all a in A, d(q, a) = p
	 * @param p the state number
	 * @return all transitions starting from a state
	 */
	public Collection<Transition> transitionsFrom(int p) {
		return G.edgesFrom(p);
	}
	
	/**
	 * @param q the state number
	 * @return the number of incoming transitions on the state
	 */
	public int indegree(int q) {
		return G.indegree(q);
	}
	
	/**
	 * @param q the state number
	 * @return the number of outgoing transitions on the state
	 */
	public int outdegree(int q) {
		return G.outdegree(q);
	}
	
	/**
	 * Merges two left-equivalent states v and w
	 */
	public void mergeLeft(int v, int w) {
		for(Transition t : transitionsFrom(w))
			addTransition(v, t.to(), t.symbol());
		clearVertex(w);
		checkMerge(v, w);
	}
	
	/**
	 * Merges two right-equivalent states v and w
	 */
	public void mergeRight(int v, int w) {
		for(Transition t : transitionsTo(w))
			addTransition(v, t.to(), t.symbol());
		clearVertex(w);
		checkMerge(v, w);
	}
	
	private void checkMerge(int v, int w) {
		if(F.contains(w)) {
			F.remove(w);
			F.add(v);
		}
		if(I.contains(w)) {
			I.remove(w);
			I.add(v);
		}
	}
	
	/**
	 * Removes vertices with degree 0, and renumbers vertices accordingly
	 */
	public int[] relabel() {
		int[] map = G.relabel();
		Set<Integer> Fnew = new HashSet<>();
		for(int v : F)
			if(map[v] != -1)
				Fnew.add(map[v]);
		Set<Integer> Inew = new HashSet<>();
		for(int v : I)
			if(map[v] != -1)
				Inew.add(map[v]);
		F = Fnew;
		I = Inew;
		return map;
	}
	
	/**
	 * Converts an NFA to a string
	 * @param M the NFA
	 * @return a string representation
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(numStates());
		sb.append('\n');
		sb.append(alphabet());
		sb.append('\n');
		for(int q : initialStates())
			sb.append(q + " ");
		sb.append('\n');
		for(int q : finalStates())
			sb.append(q + " ");
		sb.append('\n');
		for(Transition t : transitions())
			sb.append(String.format("%d %d %c\n", t.from(), t.to(), t.symbol()));
		return sb.toString();
	}
	
	/**
	 * Returns NFA in .dot format
	 */
	public String toDot() {
		StringBuilder s = new StringBuilder();
		s.append("digraph {\n");
		s.append("\tnode [shape=point, color=white, fontcolor=white]; start;\n");
		s.append("\tnode [shape=circle, color=black, fontcolor=black];\n");
		
		//a roundabout way of collecting transitions...
		HashMap<Tuple, ArrayList<Character>> map = new HashMap<>();
		
		for(Transition t : G.edges()) {
			if(t.symbol() == EPSILON)
				s.append(String.format("\t%d -> %d [color=red];\n", t.from(), t.to()));
			else {
				Tuple pair = new Tuple(t.from(), t.to());
				if(!map.containsKey(pair))
					map.put(pair, new ArrayList<Character>());
				map.get(pair).add(t.symbol());
			}
		}
		
		for(Entry<Tuple, ArrayList<Character>> e : map.entrySet()) {
			Tuple pair = e.getKey();
			Collections.sort(e.getValue());
			String symbols = "" + e.getValue().remove(0);
			for(char c : e.getValue()) {
				symbols += c;
			}
			s.append(String.format("\t%d -> %d [label=\"%s\"];\n", pair.p(), pair.q(), symbols));
		}
		
		for(int i = 0; i < G.V(); i++) {
			if(F.contains(i)) {
				s.append(String.format("\t%d [shape=doublecircle];\n", i));
			} else if(I.contains(i)) {
				s.append(String.format("\tstart -> %d;\n", i));
			}
			/*if(re != null && i < re.length) {
				s.append(String.format("\t%d [label = \"%c\"];\n", i, re[i]));
			}*/
		}
		s.append("}");
		return s.toString();
	}
	
}
