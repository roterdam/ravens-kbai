package project1;

import project2.*;
import java.util.Random;

public class SolutionFactory {
	
	private Random random;

	public SolutionFactory() {
		this.random=new Random(9L);
	}

	public RavensSolver getSolver(RavensProblem problem) {
		/* Inspect the problem and choose an appropriate solver */
		String problemType = problem.getProblemType();
		if (problemType.equals("2x1")) {
			/* choose best solver for 2x1 problem */
			return new Project1Solver(problem,this.random);
		}
		if (problemType.equals("2x2")) {
			/* choose best solver for 2x2 problem */
			;
		}
		if (problemType.equals("3x3")) {
			/* choose best solver for 2x3 problem */
			;
		}
		/* return the default solver if no better candidate found*/
		return new RandomSolver(problem, this.random);
	}

}
