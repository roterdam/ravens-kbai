package project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import shared.Instance;

public class FigureQuad {

	public MappedFigurePair top;
	public MappedFigurePair left;
	public MappedFigurePair bottom;
	public MappedFigurePair right;
	public String label;
	private long permSize;
	private HashMap<String, HashSet<String>> language;
	private Instance perm;
	private HashMap<Instance, Double> permValue;

	public FigureQuad(RavensFigure a, RavensFigure b,
			RavensFigure c, RavensFigure d, HashMap<String, HashSet<String>> language) {
		this.top=new MappedFigurePair(a,b,language);
		this.left=new MappedFigurePair(a,c,language);
		this.bottom=new MappedFigurePair(c,d,language);
		this.right=new MappedFigurePair(b,d,language);
		this.label=d.getName();
		this.language=language;
		this.permSize=top.getNumPerms();
		this.permSize*=left.getNumPerms();
		this.permSize*=bottom.getNumPerms();
		this.permSize*=right.getNumPerms();
		this.permValue=new HashMap<Instance,Double>();
	}
	
	public void setPermutations(double[] perm) {
		this.setPermutations(new Instance(perm));
	}

	public void setPermutations(Instance d) {
		this.top.setPerm((int) d.getContinuous(0));
		this.left.setPerm((int) d.getContinuous(1));
		this.bottom.setPerm((int) d.getContinuous(2));
		this.right.setPerm((int) d.getContinuous(3));
		this.perm=d;
	}
	
	public long sizeOfPermutationSpace() {
		return this.permSize;
	}
	
	public double[] findSimplestPermutation() {
		MappedFigurePair[] mfps = new MappedFigurePair[]{
				top,left,bottom,right
		};
		double[] perm = new double[mfps.length];
		for(int p=0;p<mfps.length;p++) {
		int[] differenceScore = new int[mfps[p].getNumPerms()];
		int minVal = Integer.MAX_VALUE;
		int min = 0;
		for (int i = 0; i < differenceScore.length; i++) {
			mfps[p].setPerm(i);
			differenceScore[i] = scoreChangeSet(mfps[p].getChangeSet(language));
			if (differenceScore[i] < minVal) {
				minVal = differenceScore[i];
				min = i;
			}
		}
		perm[p]= min;
		}
		return perm;
	}
	
	public Instance findSimplestPermutationInstance() {
		return new Instance(findSimplestPermutation());
	}
	
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
	
	private final String[] CHANGES = { "flip", "angle", "size", "deleted",
			"shape", "other" };
	private final int[] SCORES = { 1, 3, 2, 4, 9, 1 };

	public void setValue(double d) {
		permValue.put(perm, Double.valueOf(d));
	}
	
	public Double retrieveValue() {
		return permValue.get(perm);
	}
}
