/*
 * TODO: 
 * 2x1 Basic Problem 19 requires remapping object keys
 */
package project1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import project2.*;

/**
 * @author nathaniel
 * 
 */
public class Project1Solver extends RavensSolver {

	private static final boolean DEBUG = true;
	private RavensProblem problem;
	@SuppressWarnings("unused")
	private Random random;
	private String solution;
	private int solutionScore = Integer.MAX_VALUE;

	public Project1Solver(RavensProblem problem, Random random) {
		if (!"2x1".equals(problem.getProblemType()))
			throw new RuntimeException(
					"Unsuitable problem type for this solver");
		this.problem = problem;
		this.random = random;
	}

	@Override
	public void solve() {
		/* Learn the language */
		HashMap<String, HashSet<String>> language = Utils
				.learnLanguage(problem);
		System.out.println(problem.getName());

		/* Build a model */
		FigurePair example = new FigurePair(Utils.getFigure(problem, "A"),
				Utils.getFigure(problem, "B"));
		HashMap<int[], FigurePair> candidates = generateCandidates();

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
		this.solution=""+best;
		this.solutionScore=bestscore;
		
		/* Dump debug info */
		if (DEBUG) {
			printlanguage(language);
			System.out.println();
			Utils.printFigurePair(System.out, example);
			System.out.println();
			int differenceScore = Utils.differenceScore(example, language);
			System.out.println("Difference: " + differenceScore);
			System.out.println();
			System.out.println("Example:");
			Utils.printStringArrayList(System.out, changeSet);
			System.out.println();
			for (int[] answer : candidates.keySet()) {
				System.out.println("Answer " + answer[0] + ":");
				Utils.printStringArrayList(System.out,
						Utils.changeSet(candidates.get(answer), language));
				System.out.println();
			}
		}

	}

	public HashMap<int[], FigurePair> generateCandidates() {
		HashMap<int[], FigurePair> candidates = new HashMap<int[], FigurePair>();
		RavensFigure question = Utils.getFigure(problem, "C");
		for (String figureKey : problem.getFigures().keySet()) {
			if (figureKey.matches("[0-9]+"))
				candidates.put(
						new int[] { Integer.parseInt(figureKey), 0, 0 },
						new FigurePair(question, problem.getFigures().get(
								(figureKey))));
		}
		return candidates;
	}

	private void printlanguage(HashMap<String, HashSet<String>> language) {
		for (String attribute : language.keySet()) {
			System.out.printf("(%s) ->", attribute);
			for (String value : language.get(attribute)) {
				System.out.printf(" (%s)", value);
			}
			System.out.println();
		}
	}

	@Override
	public String getSolution() {
		return solution;
	}

}
