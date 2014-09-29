package project1;

import project2.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * @author nathaniel A baseline solver suitable for any problem type that
 *         chooses random answers.
 * 
 */
public class RandomSolver extends RavensSolver {

	private RavensProblem problem;
	private String solution;
	private Random random;

	public RandomSolver(RavensProblem problem, Random random) {
		this.problem = problem;
		this.solution = null;
		this.random = random;
	}

	@Override
	public void solve() {
		System.out.println(problem.getName());
		HashMap<String, RavensFigure> figures = problem.getFigures();
		ArrayList<String> answers = new ArrayList<String>();
		for (String key : figures.keySet()) {
			System.out.print(key + " ");
			if(key.matches("\\d+")) {
				answers.add(key);
			}
		}
		System.out.println();
		solution = answers.get(random.nextInt(answers.size()));

	}

	@Override
	public String getSolution() {
		return solution;
	}

}
