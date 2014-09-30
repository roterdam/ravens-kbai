package project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

import project1.RavensSolver;

public class MyProductionSystem {

	private Logger log;
	private ArrayList<RavensProblemCase> recordedCases;
	private Random random;

	public MyProductionSystem(Logger log) {
		this.log = log;
		this.recordedCases = new ArrayList<RavensProblemCase>();
		this.random = new Random(1L);
	}

	public String solve(RavensProblem problem) {
		/* initial tableau */
		RavensSolver solver = null;
		String problemType = problem.getProblemType();
		/* logic */
		if (problemType.equals("2x1")) {
			solver = new project2.Solver2X2(problem, random, log);
		}
		if (problemType.equals("2x2")) {
			/*
			 * todo: clone problem replacing symmetric rotations with flips to
			 * handle 2x2 basic 4
			 */
			solver = new project2.Solver2X2(problem, random, log);
		}
		if (solver == null) {
			/* default case */
			solver = new project1.RandomSolver(problem, random);
		}
		// Random override
		// solver = new project1.RandomSolver(problem, random);
		/* execute plan */
		solver.solve();
		String response = solver.getSolution();
		/* record case */
		RavensProblemCase c = new RavensProblemCase(problem);
		c.setSolver(solver.getClass().getName());
		c.setResponse(response);
		recordedCases.add(c);
		return response;
	}

	public void provideFeedbackOnProblem(RavensProblem problem, String answer) {
		RavensProblemCase c = findCaseByProblem(problem);
		if (c != null)
			c.setAnswer(answer);
		else
			log.warning("Feedback did not match any recorded case!");
	}

	private RavensProblemCase findCaseByProblem(RavensProblem problem) {
		for (RavensProblemCase c : recordedCases) {
			if (c.getProblem().getName().equals(problem.getName())
					&& c.getProblem().getProblemType()
							.equals(problem.getProblemType())
					&& c.getAnswer() == null) {
				return c;
			}
		}
		return null;
	}

}
