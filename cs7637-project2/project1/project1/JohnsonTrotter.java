/* Modified from http://introcs.cs.princeton.edu/java/23recursion/JohnsonTrotter.java.html 
 * This implementation is recursive and as such is not likely to be very scalable.
 */
/*************************************************************************
 *  Compilation:  javac Perm.java
 *  Execution:    java Permutations N k
 *  
 *  Generate permutations by transposing adjacent elements using the
 *  Johnson-Trotter algorithm.
 *
 *  This program is a Java version based on the program SJT.c
 *  writen by Frank Ruskey.
 *  
 *     http://theory.cs.uvic.ca/inf/perm/PermInfo.html
 * 
 *  % java JohnsonTrotter 3
 *  012   (2 1)
 *  021   (1 0)
 *  201   (2 1)
 *  210   (0 1)
 *  120   (1 2)
 *  102   (0 1)
 *
 *************************************************************************/
package project1;

import java.util.ArrayList;
import java.util.HashMap;

public class JohnsonTrotter {

	private static HashMap<Integer,ArrayList<int[]>> knownPerms = new HashMap<Integer,ArrayList<int[]>>();
	
	public static ArrayList<int[]> perm(int N) {
		/* memoization */
		if(knownPerms.containsKey(Integer.valueOf(N))) return knownPerms.get(Integer.valueOf(N));
		int[] p = new int[N]; // permutation
		int[] pi = new int[N]; // inverse permutation
		int[] dir = new int[N]; // direction = +1 or -1
		for (int i = 0; i < N; i++) {
			dir[i] = -1;
			p[i] = i;
			pi[i] = i;
		}
		ArrayList<int[]> perms = new ArrayList<int[]>();
		perm(0, p, pi, dir, perms);
		knownPerms.put(Integer.valueOf(N), perms);
		return perms;
	}

	public static void perm(int n, int[] p, int[] pi, int[] dir,
			ArrayList<int[]> perms) {

		// base case - print out permutation
		if (n >= p.length) {
			perms.add(p.clone());
			return;
		}

		perm(n + 1, p, pi, dir, perms);
		for (int i = 0; i <= n - 1; i++) {

			// swap
			int z = p[pi[n] + dir[n]];
			p[pi[n]] = z;
			p[pi[n] + dir[n]] = n;
			pi[z] = pi[n];
			pi[n] = pi[n] + dir[n];

			perm(n + 1, p, pi, dir, perms);
		}
		dir[n] = -dir[n];
	}

	public static void main(String[] args) {
		int N = 4;
		ArrayList<int[]> perms = perm(N);
		for (int[] p : perms) {
			for (int i = 0; i < p.length; i++)
				System.out.print(p[i]);
			System.out.println();
		}
	}
}
