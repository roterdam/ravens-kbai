package project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import project1.FigurePair;
import project1.RavensSolver;
import project1.Utils;

@SuppressWarnings("unused")
public class Project2Solver extends RavensSolver {

	private RavensProblem problem;
	private Random random;
	private String solution;

	public Project2Solver(RavensProblem problem, Random random) {
		this.problem=problem;
		this.random=random;
		project1.RandomSolver init = new project1.RandomSolver(problem, random);
		init.solve();
		this.solution=init.getSolution();
	}

	@Override
	public void solve() {
		/* Learn the language */
		HashMap<String, HashSet<String>> language = Utils
				.learnLanguage(problem);
		System.out.println(problem.getName());

		/* Candidate answers */
		HashMap<int[], FigureQuad> candidates = generateCandidates();

		/* Guess the transformations */
		ArrayList<String[]> changeSet = Utils.changeSet(example, language);

		/* Pick the best answer */
		int best=1, bestscore=0;
		for (int[] answer : candidates.keySet()) {
			ArrayList<String[]> answerChangeSet = Utils.changeSet(
					candidates.get(answer), language);
			int answerScore = Utils.compareChangeSets(changeSet,answerChangeSet);
			if (answerScore>bestscore) {
				best=answer[0];
				bestscore=answerScore;
			}
		}
	}
	
	public HashMap<int[], FigureQuad> generateCandidates() {
		HashMap<int[], FigureQuad> candidates = new HashMap<int[], FigureQuad>();
		RavensFigure question = Utils.getFigure(problem, "C");
		for (String figureKey : problem.getFigures().keySet()) {
			if (figureKey.matches("[0-9]+"))
				candidates.put(
						new int[] { Integer.parseInt(figureKey), 0, 0 },
						new FigureQuad(
								Utils.getFigure(problem, "A"),
								Utils.getFigure(problem, "B"),
								Utils.getFigure(problem, "C"),
								Utils.getFigure(problem, figureKey));
		}
		return candidates;
	}
	@Override
	public String getSolution() {
		// TODO Auto-generated method stub
		return solution;
	}

}
