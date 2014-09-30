package project2;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

import project1.RavensSolver;

public class MyProductionSystem {

	private Logger log;
	private Storage storage;
	private Random random;

	public MyProductionSystem(Logger log) {
		this.log = log;
		this.storage = new Storage();
		this.random = new Random(1L);
	}

	public String solve(RavensProblemCase casefile) {
		/* initial tableau */
		RavensSolver solver = null;
		RavensProblem problem = casefile.getProblem();
		String problemType = problem.getProblemType();
		/* logic */
		if (problemType.equals("2x1")) {
			/*
			 * the 2x2 solver performs better on 2x1 problems than the 2x1
			 * solver from project 1.
			 */
			solver = new project2.Solver2X2(casefile, random, log);
		}
		if (problemType.equals("2x2")) {
			/*
			 * todo: clone problem replacing symmetric rotations with flips to
			 * handle 2x2 basic 4
			 */
			solver = new project2.Solver2X2(casefile, random, log);
		}
		if (solver == null) {
			/* default case */
			solver = new project1.RandomSolver(problem, random);
		}
		/* execute plan */
		solver.solve();
		String response = solver.getSolution();
		/* record case */
		casefile.setSolver(solver.getClass().getName());
		casefile.setResponse(response);
		try {
			storage.save(casefile);
		} catch (IOException e) {
			log.warning("Unable to save case file:"+e.getLocalizedMessage());
		}
		return response;
	}

}
