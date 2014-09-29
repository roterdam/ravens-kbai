package project2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Linter {

	public static void main(String[] args) {
		ArrayList<ProblemSet> sets = new ArrayList<ProblemSet>();
		File[] files = new File("resources/Problems").listFiles();
		for (File file : files) { 
			ProblemSet newSet = new ProblemSet(file.getName()); 
			sets.add(newSet);
			for (File problem : file.listFiles()) { 
				newSet.addProblem(problem); 
			}
		}

		ArrayList<RavensProblem> all = new ArrayList<RavensProblem>();
		for (ProblemSet set : sets)
			all.addAll(set.getProblems());

		Agent agent = new project2.Agent();
		String response;
		try {
			PrintWriter out = new PrintWriter("lint.txt");
			for (int i = 0; i < 100; i++) {
				for (RavensProblem problem : all) {
					response = agent.Solve(problem);
					out.printf("%s,%s,%s\n", problem.getName(), response,
							problem.checkAnswer(""));
				}
			}
			out.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
