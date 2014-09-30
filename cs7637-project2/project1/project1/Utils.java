package project1;

import project2.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Utils {
	public static final String[] CHANGES = { "flip", "angle", "size",
			"deleted", "shape", "other" };
	public static final int[] SCORES = { 1, 2, 3, 4, 5, 1 };

	public static RavensObject findObjectByName(RavensProblem problem,
			String figure, String object) {
		if (problem == null || figure == null || object == null)
			throw new RuntimeException("Null input not OK");
		RavensFigure f = getFigure(problem, figure);
		if (f == null)
			return null;
		for (RavensObject obj : f.getObjects())
			if (figure.equals(obj.getName()))
				return obj;
		return null;
	}

	/**
	 * @param problem
	 * @return the language of the given problem in terms of object attributes
	 *         and possible values
	 */
	public static HashMap<String, HashSet<String>> learnLanguage(
			RavensProblem problem) {
		/* Learn explicit values */
		HashMap<String, HashSet<String>> language = new HashMap<String, HashSet<String>>();
		for (String key : problem.getFigures().keySet()) {
			RavensFigure f = getFigure(problem, key);
			for (RavensObject o : f.getObjects()) {
				for (RavensAttribute a : o.getAttributes()) {
					if (!language.containsKey(a.getName())) {
						language.put(a.getName(), new HashSet<String>());
					}
					for (String value : a.getValue().split(","))
						language.get(a.getName()).add(value);
				}
			}
		}
		/* Learn where null values are acceptable */
		for (String attribute : language.keySet()) {
			if (isNullable(problem, attribute))
				language.get(attribute).add("");
		}
		return language;
	}

	/**
	 * @param problem
	 * @param attribute
	 * @return if there is an object without any value for this attribute, then
	 *         it is nullable
	 */
	private static boolean isNullable(RavensProblem problem, String attribute) {
		for (String key : problem.getFigures().keySet()) {
			for (RavensObject o : problem.getFigures().get(key).getObjects()) {
				boolean found = false;
				for (RavensAttribute a : o.getAttributes()) {
					if (attribute.equals(a.getName())) {
						found = true;
					}
				}
				if (!found) {
					return true;
				}
			}
		}
		return false;
	}

	public static void printProblem(PrintStream out, RavensProblem problem) {
		out.printf("%s\n", problem.getName());
		for (String figureKey : problem.getFigures().keySet()) {
			printFigure(out, problem, figureKey);
		}
	}

	public static void printFigure(PrintStream out, RavensProblem problem,
			String figureKey) {
		RavensFigure f = getFigure(problem, figureKey);
		out.printf("-%s\n", figureKey);
		printFigure(out, f);
	}

	public static void printFigure(PrintStream out, RavensFigure f) {
		for (RavensObject o : f.getObjects()) {
			out.printf("---%s\n", o.getName());
			for (RavensAttribute a : o.getAttributes()) {
				out.printf("-----%s:%s\n", a.getName(), a.getValue());
			}
		}
	}

	public static RavensFigure getFigure(RavensProblem problem, String figureKey) {
		RavensFigure f = problem.getFigures().get(figureKey);
		return f;
	}

	public static void printFigurePair(PrintStream out, FigurePair pair) {
		out.printf("-%s\n", "Left");
		printFigure(out, pair.getLeft());
		out.printf("-%s\n", "Right");
		printFigure(out, pair.getRight());

	}

	public static int differenceScore(FigurePair example,
			HashMap<String, HashSet<String>> language) {
		int score = 0;
		RavensFigure left = example.getLeft(), right = example.getRight();
		HashSet<String> keys = new HashSet<String>();
		getFigureObjectNames(right, keys);
		getFigureObjectNames(left, keys);
		for (String key : keys) {
			RavensObject r = null, l = null;
			for (RavensObject o : right.getObjects()) {
				if (o.getName().equals(key)) {
					r = o;
					break;
				}
			}
			for (RavensObject o : left.getObjects()) {
				if (o.getName().equals(key)) {
					l = o;
					break;
				}
			}
			if (l == null || r == null) {
				score += 2; // absence is the only difference
				if (l == null)
					return score;
			} else {
				for (String attribute : language.keySet()) {
					score += compareObjectsOnAttribute(r, l, attribute);
				}
			}
		}
		return score;
	}

	public static ArrayList<String[]> changeSet(FigurePair example,
			HashMap<String, HashSet<String>> language) {
		RavensFigure left = example.getLeft(), right = example.getRight();
		HashSet<String> keys = new HashSet<String>();
		getFigureObjectNames(right, keys);
		getFigureObjectNames(left, keys);
		String[] k = keys.toArray(new String[keys.size()]);
		ArrayList<int[]> mappings = JohnsonTrotter.perm(k.length);
		ArrayList<String[]> changeSet, bestChangeSet;
		int best = 0;
		int bestscore = scoreChangeSet(bestChangeSet = changeSet(language,
				left, right, k, mappings.get(0)));
		for (int i = 1; i < k.length; i++) {
			int score = scoreChangeSet(changeSet = changeSet(language, left,
					right, k, mappings.get(i)));
			if (score < bestscore) {
				bestChangeSet = changeSet;
				bestscore = score;
				best = i;
			}
		}

		return bestChangeSet;
	}

	public static ArrayList<String[]> changeSet(
			HashMap<String, HashSet<String>> language, RavensFigure left,
			RavensFigure right, String[] k, int[] mapping) {
		ArrayList<String[]> changeSet = new ArrayList<String[]>();
		for (int i = 0; i < mapping.length; i++) {
			RavensObject r = null, l = null;
			for (RavensObject o : left.getObjects()) {
				if (o.getName().equals(k[i])) {
					l = o;
					break;
				}
			}
			for (RavensObject o : right.getObjects()) {
				if (o.getName().equals(k[mapping[i]])) {
					r = o;
					break;
				}
			}
			String key = k[i] + "->" + k[mapping[i]];
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

	public static int scoreChangeSet(ArrayList<String[]> changeSet) {
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

	private static int points(String string) {
		int i;
		for (i = 0; i < CHANGES.length - 1; i++)
			if (string.contains(CHANGES[i]))
				;
		return SCORES[i];
	}

	public static void getFigureObjectNames(RavensFigure right,
			HashSet<String> keys) {
		for (RavensObject o : right.getObjects()) {
			keys.add(o.getName());
		}
	}

	private static ArrayList<String[]> differences(String key, RavensObject r,
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

	public static int compareObjectsOnAttribute(RavensObject r, RavensObject l,
			String attribute) {
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
		rprops.removeAll(matched);
		lprops.removeAll(matched);
		return rprops.size() + lprops.size();
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

	public static void printStringArrayList(PrintStream out,
			ArrayList<String[]> changeSet) {
		for (String[] change : changeSet) {
			for (String s : change)
				out.printf(" (%s)", s);
			out.println();
		}
	}

	public static int compareChangeSets(ArrayList<String[]> changeSet,
			ArrayList<String[]> answerChangeSet) {
		HashSet<String> relationsA = new HashSet<String>();
		HashSet<String> relationsB = new HashSet<String>();
		for (String[] a : changeSet)
			relationsA.add(a[0]);
		for (String[] a : answerChangeSet)
			relationsB.add(a[0]);
		String[] ra = relationsA.toArray(new String[relationsA.size()]);
		String[] rb = relationsB.toArray(new String[relationsB.size()]);
		String[] l, s;
		ArrayList<String[]> lc, sc;
		if (ra.length > rb.length) {
			l = ra;
			lc = changeSet;
			s = rb;
			sc = answerChangeSet;
		} else {
			s = ra;
			sc = changeSet;
			l = rb;
			lc = answerChangeSet;
		}

		// so tired of relating things at this point...

		ArrayList<int[]> mappings = JohnsonTrotter.perm(l.length);
		ArrayList<String[]> mapping;
		int best = 0;
		int bestscore = scoreChangeSets(lc, sc, l, s, mappings.get(0));
		for (int i = 1; i < mappings.size(); i++) {
			int score = scoreChangeSets(lc, sc, l, s, mappings.get(i));
			if (score > bestscore) {
				bestscore = score;
				best = i;
			}
		}
		return bestscore;
	}

	private static int scoreChangeSets(ArrayList<String[]> lc,
			ArrayList<String[]> sc, String[] l, String[] s, int[] is) {
		int score = 0;
		for (int r = 0; r < l.length; r++) {
			if (is[r] < s.length) {
				String lr = l[r];
				String sr = s[is[r]];
				for (String[] a : lc) {
					if (a[0].equals(lr)) {
						// look for matching transformation
						for (String[] b : sc) {
							if (b[0].equals(sr)) {
								// like relations
								if (a[1].equals(b[1])) {
									// like operations
									if (a[1].equals("delete")
											|| a[1].equals("insert"))
										score++;
									else {
										// it's a change op
										if (a[2].equals(b[2])) {
											// change the same attribute
											if (a[2].contains("angle")) {
												// compare numeric value,
												// probably an angle
												try {
													if (Math.abs(Integer
															.parseInt(a[3])
															- Integer
																	.parseInt(a[4])) == Math
															.abs(Integer
																	.parseInt(b[3])
																	- Integer
																			.parseInt(b[4]))) {
														score += 2;
													} else {
														// At least it's the
														// same
														// changed attr
														score++;
													}
												} catch (NumberFormatException e) {
													score++;
												}
											} else {
												// compare categorical value
												if (a[3].equals(b[3])
														&& a[4].equals(b[4])) {
													score += 2; // tough to
																// match
												} else {
													// At least it's the same
													// changed attr
													score++;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return score / lc.size();
	}
}
