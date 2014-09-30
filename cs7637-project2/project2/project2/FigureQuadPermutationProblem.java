package project2;

import java.util.Random;

import shared.Instance;

public class FigureQuadPermutationProblem implements opt.ga.GeneticAlgorithmProblem {

	private FigureQuad quad;
	private Random random;
	private int[] lim;
	private FigureQuadEvaluationFunction eval;

	public FigureQuadPermutationProblem(FigureQuad quad, FigureQuadEvaluationFunction eval, Random random) {
		this.quad = quad;
		this.random = random;
		this.lim = new int[] { quad.top.getNumPerms(), quad.left.getNumPerms(),
				quad.bottom.getNumPerms(), quad.right.getNumPerms() };
		this.eval=eval;
	}

	@Override
	public double value(Instance d) {
		/* make negative for minimization */
		return eval.value(d);
	}

	@Override
	public Instance random() {
		double[] r = new double[lim.length];
		for (int i=0;i<lim.length;i++) r[i]=random.nextInt(lim[i]);
		return new Instance(r);
	}

	@Override
	public Instance mate(Instance a, Instance b) {
		Instance c = (Instance) a.copy();
		int mpos = random.nextInt(4);
		c.getData().set(mpos, b.getData().get(mpos));
		return c;
	}

	@Override
	public void mutate(Instance d) {
		int mpos = random.nextInt(4);
		double mdir = random.nextDouble();
		double mval = d.getData().get(mpos);
		mval += (mdir > 0.5 ? 1 : -1);
		if (mval >= lim[mpos])
			mval = 0;
		if (mval < 0)
			mval = lim[mpos] - 1;
		d.getData().set(mpos, mval);
	}

}
