package project2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import shared.Instance;

public class RavensProblemCase {

	private static final String VERSION = "2.0";
	private RavensProblem problem;
	private String response;
	private String answer;
	private String solver;
	private UUID uid;
	private HashMap<FigureQuad, ArrayList<Instance>> figureMappings;

	public RavensProblemCase(RavensProblem problem) {
		this.problem = problem;
		this.uid = UUID.randomUUID();
		this.figureMappings = new HashMap<FigureQuad, ArrayList<Instance>>();
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public RavensProblem getProblem() {
		return problem;
	}

	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public void setSolver(String name) {
		this.solver = name;
	}

	public String getSolver() {
		return solver;
	}

	public void mapFigures(FigureQuad quad, ArrayList<Instance> bestSeen) {
		figureMappings.put(quad, bestSeen);
	}

	public String getUID() {
		return this.uid.toString();
	}

	public void write(File f) throws IOException {
		PrintWriter out = null;
		try {
			f.createNewFile();
			out = new PrintWriter(f);
			out.write(field("case-file-version", VERSION));
			out.write(field("case-file-uuid", this.getUID()));
			out.write(field("problem-name", problem.getName()));
			out.write(field("problem-type", problem.getProblemType()));
			out.write(field("solver-class", solver));
			out.write(field("solver-answer", response));
			out.write(field("solver-correct", response.equals(problem.checkAnswer(response))?"yes":"no"));
			for(FigureQuad quad:figureMappings.keySet()) {
				ArrayList<Instance> bestMappings = figureMappings.get(quad);
				for (Instance d: bestMappings) {
					quad.setPermutations(d);
					out.write(field("figure-quad-label",quad.label));
					out.write(field("figure-quad-mapping-top",quad.top.printMapping()));
					out.write(field("figure-quad-mapping-left",quad.left.printMapping()));
					out.write(field("figure-quad-mapping-bottom",quad.bottom.printMapping()));
					out.write(field("figure-quad-mapping-right",quad.right.printMapping()));
					out.write(field("figure-quad-mapping-value",quad.retrieveValue().toString()));
					out.write(field("figure-quad-changes-top",quad.top.printChangeSet()));
					out.write(field("figure-quad-changes-left",quad.left.printChangeSet()));
					out.write(field("figure-quad-changes-bottom",quad.bottom.printChangeSet()));
					out.write(field("figure-quad-changes-right",quad.right.printChangeSet()));
				}
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("This should never happen");
		} finally {
			if (out != null)
				out.close();
		}
	}

	private String field(String key, String value) {
		return String.format("%s:%s\r\n", key, value);
	}

}
