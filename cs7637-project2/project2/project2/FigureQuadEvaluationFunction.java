package project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import opt.EvaluationFunction;
import project1.Utils;
import shared.Instance;

public class FigureQuadEvaluationFunction implements EvaluationFunction {
	
	private HashMap<String, HashSet<String>> language;
	private FigureQuad quad;

	public FigureQuadEvaluationFunction(FigureQuad quad,
			HashMap<String, HashSet<String>> language) {
		this.quad=quad;
		this.language=language;
	}


	@Override
	public double value(Instance d) {
		quad.setPermutations(d);
		ArrayList<String[]> topChangeSet = 
				quad.top.getChangeSet(language);
		ArrayList<String[]> leftChangeSet = 
				quad.left.getChangeSet(language);
		ArrayList<String[]> bottomChangeSet = 
				quad.bottom.getChangeSet(language);
		ArrayList<String[]> rightChangeSet = 
				quad.right.getChangeSet(language);

		double[] score = new double[3];
		score[0] = Utils.compareChangeSets(topChangeSet,
				bottomChangeSet);
		score[1] = Utils.compareChangeSets(leftChangeSet,
				rightChangeSet);
		score[2] = Math.sqrt(Math.pow(score[0], 2)+Math.pow(score[1], 2));
		return -score[2];
	}

}
