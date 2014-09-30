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
	private Storage storage;
	private RavensProblemCase casefile;
	private int bestDiff;

	public Solver2X2(RavensProblemCase casefile, Random random, Logger log) {
		this.casefile = casefile;
		this.problem = casefile.getProblem();
		this.random = random;
		this.log = log;
		this.solution = "1";
		this.bestDiag = -1;
		this.bestDiff = Integer.MAX_VALUE;
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

			/* start up a new evaluation function for this quad */
			FigureQuadEvaluationFunction eval = new FigureQuadEvaluationFunction(
					quad);

			/* permutation search */
			Instance perm = geneticSearchForBetterMapping(quad, eval);

			quad.setPermutations(perm);
			int diff = quad.scoreMappingChangeSet();
			double diag = eval.value(perm);

			log.info(String.format("%s - Option: %s, diag: %.2f, diff: %d",
					problem.getName(), quad.label, diag, diff));

			if (diag > this.bestDiag) {
				this.bestDiag = diag;
				this.solution = quad.label;
			} else if (diag>=this.bestDiag && diff < this.bestDiff) {
				this.bestDiff = diff;
				this.bestDiag = diag;
				this.solution = quad.label;
			}

		}
	}

	private Instance geneticSearchForBetterMapping(FigureQuad quad,
			FigureQuadEvaluationFunction eval) {
		/*
		 * We are searching over the "weights" which are actualy indexes to
		 * precalculated permutations of object mappings each mapped figure pair
		 * in the figure quad.
		 */
		double[] perm = new double[4];
		Instance d = new Instance(perm);
		/* initialize the problem model for ABAGAIL */
		GeneticAlgorithmProblem gap = new FigureQuadPermutationProblem(quad,
				eval, random);
		/* Lots of mating and mutating, prioritize exploration */
		StandardGeneticAlgorithm trainer = new StandardGeneticAlgorithm(100,
				60, 40, gap);
		double fitness;
		/*
		 * Always include one of the simplest starting configurations (it used
		 * to be the only configuration considered)
		 */
		ArrayList<Instance> bestSeen = new ArrayList<Instance>();
		bestSeen.add(quad.findSimplestPermutationInstance());
		/* spend a little time in random search */
		log.info(String.format(
				"Searching the permutation space for answer %s (%d)",
				quad.label, quad.sizeOfPermutationSpace()));
		double bestValue = eval.value(bestSeen.get(0));
		/* also limit GA iterations */
		long generations = Math.min(100L, quad.sizeOfPermutationSpace() / 200L);
		/* Keep time */
		long start = System.currentTimeMillis();
		long tick = start + 1000;
		long timeout = start + 9999; // 10 seconds per answer candidate
		for (int i = 0; i < generations; i++) {
			fitness = trainer.train();
			/* check for improvement every generation */
			Instance b = trainer.getOptimal();
			double value = eval.value(b);
			if (value > bestValue) {
				bestSeen.add(b);
				bestValue = value;
			}
			if (System.currentTimeMillis() >= tick) {
				log.info(String.format(
						"Permuting answer %s, generation %d, improvements: %d",
						quad.label, i, bestSeen.size()));
				if (tick >= timeout) {
					log.info("Time expired, stopping search.");
					break;
				}
				tick = tick + 1000;
			}
		}
		log.info(String
				.format("Answer %s permutation path found %d improvements in %.2f seconds",
						quad.label, bestSeen.size() - 1,
						(System.currentTimeMillis() - start) / 1000.0));
		int peak = 0;
		double[] val = new double[bestSeen.size()];
		int[] diff = new int[bestSeen.size()];
		for (int i = 0; i < val.length; i++) {
			Instance b = bestSeen.get(i);
			quad.setPermutations(b);
			diff[i] = quad.scoreMappingChangeSet();
			val[i] = eval.value(b);
			if (val[i] > val[peak]) {
				peak = i;
			}
			log.info(String.format(
					"Answer %s, improvement %d, fitness %.2f, %s", quad.label,
					i, val[i], b));
		}
		log.info(String.format("Answer %s's best permutation's diagonal: %.2f",
				quad.label, val[peak]));
		/* Now look for ties and prefer the simplest changeset */
		int best = peak;
		for (int i = 0; i < val.length; i++) {
			if (val[i] >= val[peak]) {
				if (diff[i] <= diff[best])
					best = i;
			}
		}
		StringBuffer buf = new StringBuffer();
		int remaining = 0;
		for (int i = 0; i < val.length; i++)
			if (val[i] >= val[peak] && diff[i] <= diff[best]) {
				buf.append(String.format(" %s:(%.2f/%d)", i, val[i], diff[i]));
				remaining++;
			}
		log.info(String.format("Reduced to %d candidate mappings [%s ]",remaining,buf.toString()));
		casefile.mapFigures(quad, bestSeen);
		return bestSeen.get(best);
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
