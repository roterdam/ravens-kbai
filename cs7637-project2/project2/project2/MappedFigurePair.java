package project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import project1.FigurePair;
import project1.JohnsonTrotter;

public class MappedFigurePair extends FigurePair {
	private int numObjects;
	private String[] indexLeft;
	private String[] indexRight;
	private ArrayList<int[]> perms;
	private int perm;
	private int[] mapping;

	public MappedFigurePair(RavensFigure left, RavensFigure right) {
		super(left, right);
		this.numObjects = Math.max(left.getObjects().size(), right.getObjects()
				.size());
		this.indexLeft = new String[numObjects];
		this.indexRight = new String[numObjects];
		/* index object names */
		for (int i = 0; i < getLeft().getObjects().size(); i++)
			indexLeft[i] = getLeft().getObjects().get(i).getName();
		for (int i = 0; i < getRight().getObjects().size(); i++)
			indexRight[i] = getRight().getObjects().get(i).getName();
		/* default mapping */
		perms=JohnsonTrotter.perm(numObjects);
		setPerm(0);
	}

	public ArrayList<String[]> getChangeSet(
			HashMap<String, HashSet<String>> language) {
		ArrayList<String[]> changeSet = new ArrayList<String[]>();
		for (int i = 0; i < mapping.length; i++) {
			RavensObject l = null, r = null;
			for (RavensObject o : getLeft().getObjects()) {
				if (o.getName().equals(indexLeft[i])) {
					l = o;
					break;
				}
			}
			for (RavensObject o : getRight().getObjects()) {
				if (o.getName().equals(indexRight[mapping[i]])) {
					r = o;
					break;
				}
			}
			String key = indexLeft[i] + "->" + indexRight[mapping[i]];
			if (l == null || r == null) {
				if (l == null)
					changeSet.add(new String[] { key, "insert" });
				if (r == null)
					changeSet.add(new String[] { key, "delete" });
			} else {
				for (String attribute : language.keySet()) {
					changeSet.addAll(differences(key, r, l, attribute));

				}
			}
		}
		return changeSet;
	}

	public static ArrayList<String[]> differences(String key, RavensObject r,
			RavensObject l, String attribute) {
		ArrayList<String[]> rprops = new ArrayList<String[]>();
		makePropositions(r, attribute, rprops);

		ArrayList<String[]> lprops = new ArrayList<String[]>();
		makePropositions(l, attribute, lprops);

		ArrayList<String[]> matched = new ArrayList<String[]>();
		for (String[] a : rprops) {
			for (String[] b : lprops) {
				if (a[1].equals(b[1])) {
					matched.add(a);
				}
			}
		}
		for (String[] b : lprops) {
			for (String[] a : rprops) {
				if (a[1].equals(b[1])) {
					matched.add(b);
				}
			}
		}
		ArrayList<String[]> changeSet = new ArrayList<String[]>();

		if (matched.size() == 0)
			for (String[] a : lprops) {
				boolean found = false;
				for (String[] b : rprops) {
					if (a[0].equals(b[0])) {
						found = true;
						changeSet.add(new String[] { key, "change", a[0], a[1],
								b[1] });
					}

				}
				if (!found)
					throw new RuntimeException(
							"Comparison failed, missing matching attribute");
			}
		return changeSet;
	}

	public static void makePropositions(RavensObject r, String attribute,
			ArrayList<String[]> props) {
		boolean found = false;
		for (RavensAttribute attr : r.getAttributes()) {
			if (attr.getName().equals(attribute)) {
				found = true;
				props.add(new String[] { attribute, attr.getValue() });
			}
		}
		if (!found)
			props.add(new String[] { attribute, "" });
	}

	public void setPerm(int i) {
		this.perm = i;
		this.mapping = perms.get(i);
	}

	public int getPerm() {
		return perm;
	}

	public int permute() {
		this.perm++;
		if (this.perm >= perms.size())
			this.perm = 0;
		this.mapping = perms.get(this.perm);
		return this.perm;
	}

}
