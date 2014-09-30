package project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import opt.ga.GeneticAlgorithmProblem;
import opt.ga.StandardGeneticAlgorithm;
import project1.RavensSolver;
import project1.Utils;
import shared.Instance;

@SuppressWarnings("unused")
public class Solver2X2 extends RavensSolver {

	private RavensProblem problem;
	private Random random;
	private String solution;
	private Logger log;
	private HashMap<String, HashSet<String>> language;
	private double bestDiag;
	private FigureQuadEvaluationFunction eval;
	private Storage storage;
	private RavensProblemCase casefile;

	public Solver2X2(RavensProblemCase casefile, Random random, Logger log) {
		this.casefile=casefile;
		this.problem = casefile.getProblem();
		this.random = random;
		this.log = log;
		this.solution = "1";
		this.bestDiag = -1;
		this.language = Utils.learnLanguage(problem);
	}

	@Override
	public void solve() {
		/* Learn the language */
		System.out.println(problem.getName());

		/* Candidate answers */
		HashMap<int[], FigureQuad> candidates = generateCandidates();

		HashMap<String, Double> scores = new HashMap<String, Double>();
		/* Evaluate candidates */
		for (int[] key : candidates.keySet()) {
			/* Guess the transformations */
			FigureQuad quad = candidates.get(key);

			/* permutation search */
			Instance perm = searchForGoodPermutationOf(quad);

			double[] score = scoreQuadPermutation(quad, perm);

			log.info(String
					.format("%s - Option: %s, LTR: %.0f, TTB: %.0f, diag: %.2f",
							problem.getName(), quad.label, score[0], score[1],
							score[2]));

			key[1] = (int) score[0];
			key[2] = (int) score[1];
			if (score[2] > this.bestDiag) {
				this.bestDiag = score[2];
				this.solution = quad.label;
			}
		}
	}

	private double[] scoreQuadPermutation(FigureQuad quad, Instance perm) {
		quad.setPermutations(perm);
		ArrayList<String[]> topChangeSet = quad.top.getChangeSet(language);
		ArrayList<String[]> leftChangeSet = quad.left.getChangeSet(language);
		ArrayList<String[]> bottomChangeSet = quad.bottom
				.getChangeSet(language);
		ArrayList<String[]> rightChangeSet = quad.right.getChangeSet(language);

		double[] score = new double[3];
		score[0] = Utils.compareChangeSets(topChangeSet, bottomChangeSet);
		score[1] = Utils.compareChangeSets(leftChangeSet, rightChangeSet);
		score[2] = Math.sqrt(Math.pow(score[0], 2) + Math.pow(score[1], 2));
		return score;
	}

	private Instance searchForGoodPermutationOf(FigureQuad quad) {
		double[] perm = new double[4];
		Instance d = new Instance(perm);
		eval = new FigureQuadEvaluationFunction(quad, language);
		GeneticAlgorithmProblem gap = new FigureQuadPermutationProblem(quad,
				eval, random);
		StandardGeneticAlgorithm trainer = new StandardGeneticAlgorithm(100,
				10, 50, gap);
		double fitness;
		long start = System.currentTimeMillis();
		long tick = start + 1000;
		long timeout = start+60000-1;
		log.info(String.format(
				"Searching the permutation space for answer %s (%d)",
				quad.label, quad.sizeOfPermutationSpace()));
		ArrayList<Instance> bestSeen = new ArrayList<Instance>();
		bestSeen.add(quad.findSimplestPermutationInstance());
		double bestValue = eval.value(bestSeen.get(0));
		long generations = Math.max(100L,
				100L * (long) Math.log10(quad.sizeOfPermutationSpace()));
		for (int i = 0; i < generations; i++) {
			fitness = trainer.train();
			/* check for improvement every generation */
			Instance b = trainer.getOptimal();
			double value = -eval.value(b);
			if (value > bestValue) {
				bestSeen.add(b);
				bestValue = value;
			}
			if (System.currentTimeMillis() >= tick) {
				log.info(String
						.format("Permuting answer %s, generation %d/%d, improvements: %d",
								quad.label, i, generations, bestSeen.size()));
				if(tick>=timeout) {
					log.info("Time expired, stopping search.");
					break;
				}
				tick = tick + 1000;
			}
		}
		log.info(String
				.format("Answer %s permutation path found %d improvements in %.2f seconds",
						quad.label, bestSeen.size()-1,
						(System.currentTimeMillis() - start) / 1000.0));
		int peak = 0;
		double bestVal = -1;
		for (int i = 0; i < bestSeen.size(); i++) {
			Instance b = bestSeen.get(i);
			double value = -eval.value(b);
			if (value > bestVal) {
				bestVal = value;
				peak = i;
			}
			log.info(String.format(
					"Answer %s, improvement %d, fitness %.2f, %s", quad.label,
					i, value, b));
		}
		log.info(String.format(
				"Answer %s's best permutation's diagonal: %.2f", quad.label,
				bestVal));
		casefile.mapFigures(quad,bestSeen);
		return bestSeen.get(peak);
	}

	public HashMap<int[], FigureQuad> generateCandidates() {
		HashMap<int[], FigureQuad> candidates = new HashMap<int[], FigureQuad>();
		RavensFigure question = Utils.getFigure(problem, "C");
		for (int i = 1; i <= 6; i++) {
			candidates.put(
					new int[] { i, 0, 0, 0 },
					new FigureQuad(Utils.getFigure(problem, "A"), Utils
							.getFigure(problem, "B"), Utils.getFigure(problem,
							"C"), Utils.getFigure(problem, "" + i), language));
		}
		return candidates;
	}

	@Override
	public String getSolution() {
		// TODO Auto-generated method stub
		return solution;
	}

}
