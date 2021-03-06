package project2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	private HashMap<String, HashSet<String>> language;

	public MappedFigurePair(RavensFigure left, RavensFigure right, HashMap<String, HashSet<String>> language) {
		super(left, right);
		this.language=language;
		this.numObjects = Math.max(left.getObjects().size(), right.getObjects()
				.size());
		this.indexLeft = new String[numObjects];
		this.indexRight = new String[numObjects];
		/* assure order and label invariant mappings are produced */
		RavensObjectComparator comparator = new RavensObjectComparator(language);
		ArrayList<RavensObject> leftObjects = new ArrayList<RavensObject>();
		leftObjects.addAll(getLeft().getObjects());
		Collections.sort(leftObjects,comparator);
		ArrayList<RavensObject> rightObjects = new ArrayList<RavensObject>();
		rightObjects.addAll(getRight().getObjects());
		Collections.sort(rightObjects,comparator);
		/* index object names */
		for (int i = 0; i < leftObjects.size(); i++)
			indexLeft[i] = leftObjects.get(i).getName();
		for (int i = 0; i < rightObjects.size(); i++)
			indexRight[i] = rightObjects.get(i).getName();
		/* set default mapping */
		perms = JohnsonTrotter.perm(numObjects);
		setPerm(0);
	}

	public ArrayList<String[]> getChangeSet() {
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

	public int getNumPerms() {
		return perms.size();
	}

	public int permute() {
		this.perm++;
		if (this.perm >= perms.size())
			this.perm = 0;
		this.mapping = perms.get(this.perm);
		return this.perm;
	}

	public String printMapping() {
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		for(int i=0;i<mapping.length;i++) {
			buf.append(" "+i+":"+indexLeft[i]+"->"+indexRight[mapping[i]]);
		}
		buf.append(" ]");
		return buf.toString();
	}
	
	public String printChangeSet() {
		ArrayList<String[]> c = getChangeSet();
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		for(int i=0;i<c.size();i++) {
			String[] p = c.get(i);
			buf.append(" "+i+":[ ");
			for (int j=0;j<p.length;j++) buf.append(" "+p[j]);
			buf.append(" ]");
		}
		buf.append(" ]");
		return buf.toString();
	}
}
