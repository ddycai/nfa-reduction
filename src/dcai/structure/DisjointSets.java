package dcai.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Union-find data structure for working with disjoint sets
 * using path compression and union by rank
 * @author Duncan
 *
 */

public class DisjointSets {

	private int[] parent;	//array of parents/id
	private int[] rank;		//array of ranks
	private int count;		//number of disjoint sets
	private int size;
	private boolean finalized = false;
	
	/**
	 * Makes disjoint sets
	 * @param size
	 */
	public DisjointSets(int size) {
		parent = new int[size];
		rank = new int[size];
		this.size = size;
		count = size;
		for(int i = 0; i < size; i++) {
			parent[i] = i;
			rank[i] = 0;
		}
	}
	
	/**
	 * Finds the id or parent of element p
	 * @param p the element
	 */
	public int find(int p) {
		if(finalized)
			return parent[p];
		int root = p;
		//find the parent
		while(root != parent[root])
			root = parent[root];
		//path compression
		while(p != root) {
			int q = parent[p];
			parent[p] = root;
			p = q;
		}
		return root;
	}
	
	/**
	 * Unions the set that p belongs to with the set that q belongs to
	 */
	public void union(int p, int q) {
		if(finalized)
			throw new RuntimeException("Cannot modify (perform union on) finalized disjoint sets");
		int rootp = find(p);
		int rootq = find(q);
		if(rootp == rootq) return;
		
		//union by rank
		if(rank[rootp] > rank[rootq])
			parent[rootq] = rootp;
		else if(rank[rootq] < rank[rootp])
			parent[rootp] = rootq;
		else {
			parent[rootp] = rootq;
			rank[rootq]++;
		}
		count--;
	}
	
	/**
	 * Determines if p and q belong to the same set
	 */
	public boolean connected(int p, int q) {
		return find(p) == find(q);
	}
	
	/**
	 * Gives each disjoint set a number from 0...count that will be returned when calling find().
	 * Unions CANNOT be performed after this operation.
	 */
	public void finalize() {
		if(finalized)
			return;
		int[] map = new int[parent.length];
		for(int i = 0; i < map.length; i++)
			map[i] = -1;
		int cur = 0;
		for(int i = 0; i < parent.length; i++) {
			if(map[find(i)] == -1) {
				map[find(i)] = cur;
				cur++;
			}
			map[i] = map[find(i)];
		}
		finalized = true;
		parent = map;
	}
	
	/**
	 * Finalizes the sets and returns an ArrayList of the disjoint sets
	 * Unions CANNOT be performed after this operation.
	 */
	public ArrayList<Set<Integer>> getSets() {
		finalize();
		ArrayList<Set<Integer>> sets = new ArrayList<>(count);
		for(int i = 0; i < count; i++)
			sets.add(new HashSet<Integer>());
		for(int i = 0; i < size; i++)
			sets.get(find(i)).add(i);
		return sets;
	}
	
	/**
	 * The number of disjoint sets
	 */
	public int count() { return count; }
	
	/**
	 * The number of elements in the union-find
	 */
	public int size() { return size; }
	
}
