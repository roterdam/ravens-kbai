package project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Logger;

import project1.FigurePair;
import project1.RavensSolver;
import project1.Utils;

@SuppressWarnings("unused")
public class Project2Solver extends RavensSolver {

	private RavensProblem problem;
	private Random random;
	private String solution;
	private Logger log;

	public Project2Solver(RavensProblem problem, Random random, Logger log) {
		this.problem = problem;
		this.random = random;
		this.log = log;
		project1.RandomSolver init = new project1.RandomSolver(problem, random);
		init.solve();
		this.solution = init.getSolution();
	}

	@Override
	public void solve() {
		/* Learn the language */
		HashMap<String, HashSet<String>> language = Utils
				.learnLanguage(problem);
		System.out.println(problem.getName());

		/* Candidate answers */
		HashMap<int[], FigureQuad> candidates = generateCandidates();

		/* Evaluate candidates */
		for (int[] key : candidates.keySet()) {
			/* Guess the transformations */
			FigureQuad quad = candidates.get(key);
			ArrayList<String[]> topChangeSet = quad.top.getChangeSet(language);
			ArrayList<String[]> leftChangeSet = quad.left.getChangeSet(language);
			ArrayList<String[]> bottomChangeSet = quad.bottom.getChangeSet(language);
			ArrayList<String[]> rightChangeSet = quad.right.getChangeSet(language);

			int leftToRightScore = Utils.compareChangeSets(topChangeSet,
					bottomChangeSet);
			int topToBottomScore = Utils.compareChangeSets(leftChangeSet,
					rightChangeSet);

			log.info(String.format("%s - Option: %s, LTR: %d, TTB: %d",
					problem.getName(), quad.label, leftToRightScore,
					topToBottomScore));
		}
	}

	public HashMap<int[], FigureQuad> generateCandidates() {
		HashMap<int[], FigureQuad> candidates = new HashMap<int[], FigureQuad>();
		RavensFigure question = Utils.getFigure(problem, "C");
		for (String figureKey : problem.getFigures().keySet()) {
			if (figureKey.matches("[0-9]+"))
				candidates.put(
						new int[] { Integer.parseInt(figureKey), 0, 0 },
						new FigureQuad(Utils.getFigure(problem, "A"), Utils
								.getFigure(problem, "B"), Utils.getFigure(
								problem, "C"), Utils.getFigure(problem,
								figureKey)));
		}
		return candidates;
	}

	@Override
	public String getSolution() {
		// TODO Auto-generated method stub
		return solution;
	}

}
