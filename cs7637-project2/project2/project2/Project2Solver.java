package project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Logger;

import project1.RavensSolver;
import project1.Utils;

@SuppressWarnings("unused")
public class Project2Solver extends RavensSolver {

	private RavensProblem problem;
	private Random random;
	private String solution;
	private Logger log;
	private HashMap<String, HashSet<String>> language;
	private double bestDiag;

	public Project2Solver(RavensProblem problem, Random random, Logger log) {
		this.problem = problem;
		this.random = random;
		this.log = log;
		this.solution = "1";
		this.bestDiag=-1;
	}

	private int findFirstBestPermutationOf(MappedFigurePair mfp){
		int[] differenceScore = new int[mfp.getNumPerms()];
		int minVal=Integer.MAX_VALUE;
		int min=0;
		for (int i=0;i<differenceScore.length;i++) {
			mfp.setPerm(i);
			differenceScore[i]=scoreChangeSet(mfp.getChangeSet(language));
			if(differenceScore[i]<minVal) {
				minVal=differenceScore[i];
				min=i;
			}
		}
		return min;
	}
	@Override
	public void solve() {
		/* Learn the language */
		this.language = Utils.learnLanguage(problem);
		System.out.println(problem.getName());

		/* Candidate answers */
		HashMap<int[], FigureQuad> candidates = generateCandidates();

		/* Evaluate candidates */
		for (int[] key : candidates.keySet()) {
			/* Guess the transformations */
			FigureQuad quad = candidates.get(key);
			
			quad.top.setPerm(findFirstBestPermutationOf(quad.top));
			quad.left.setPerm(findFirstBestPermutationOf(quad.left));
			quad.bottom.setPerm(findFirstBestPermutationOf(quad.bottom));
			quad.right.setPerm(findFirstBestPermutationOf(quad.right));
			
			ArrayList<String[]> topChangeSet = 
					quad.top.getChangeSet(language);
			ArrayList<String[]> leftChangeSet = 
					quad.left.getChangeSet(language);
			ArrayList<String[]> bottomChangeSet = 
					quad.bottom.getChangeSet(language);
			ArrayList<String[]> rightChangeSet = 
					quad.right.getChangeSet(language);

			int leftToRightScore = Utils.compareChangeSets(topChangeSet,
					bottomChangeSet);
			int topToBottomScore = Utils.compareChangeSets(leftChangeSet,
					rightChangeSet);
			double diagonal = Math.sqrt(Math.pow(leftToRightScore, 2)+Math.pow(topToBottomScore, 2));

			log.info(String.format("%s - Option: %s, LTR: %d, TTB: %d, diag: %.2f",
					problem.getName(), quad.label, leftToRightScore,
					topToBottomScore, diagonal));
			
			key[1]=leftToRightScore;
			key[2]=topToBottomScore;
			if(diagonal>this.bestDiag) {
				this.bestDiag=diagonal;
				this.solution=quad.label;
			}
		}
	}

	private final String[] CHANGES = { "flip", "angle", "size", "deleted",
			"shape", "other" };
	private final int[] SCORES = { 1, 2, 3, 4, 5, 1 };

	private int scoreChangeSet(ArrayList<String[]> changeSet) {
		int score = 0;
		for (String[] change : changeSet) {
			if (change[1].equals("insert") || change[1].equals("delete"))
				score += points("deleted");
			else if (change[2].contains("flip"))
				;
			else
				score += points(change[2]);
		}
		return score;
	}

	private int points(String string) {
		int i;
		for (i = 0; i < CHANGES.length - 1; i++)
			if (string.contains(CHANGES[i]))
				;
		return SCORES[i];
	}

	public HashMap<int[], FigureQuad> generateCandidates() {
		HashMap<int[], FigureQuad> candidates = new HashMap<int[], FigureQuad>();
		RavensFigure question = Utils.getFigure(problem, "C");
		for (String figureKey : problem.getFigures().keySet()) {
			if (figureKey.matches("[0-9]+"))
				candidates.put(
						new int[] { Integer.parseInt(figureKey), 0, 0, 0},
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
