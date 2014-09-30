package project2;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures: public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
	private Logger log;
	private double right;
	private double problems;
	private MyProductionSystem productionSystem;
	private FileOutputStream dfile;

	/**
	 * The default constructor for your Agent. Make sure to execute any
	 * processing necessary before your Agent starts solving problems here.
	 * 
	 * Do not add any variables to this signature; they will not be used by
	 * main().
	 * 
	 */
	public Agent() {
		this.log = Logger.getLogger("project2");
		/* Initialize production system, which may have saved state */
		this.productionSystem = new MyProductionSystem(log);
		try {
		this.dfile=new FileOutputStream("debug.log");
		} catch (FileNotFoundException e) {
			log.severe("Unable to open debug.log for writing");
		}
		log.info("Agent initialized");
	}

	/**
	 * The primary method for solving incoming Raven's Progressive Matrices. For
	 * each problem, your Agent's Solve() method will be called. At the
	 * conclusion of Solve(), your Agent should return a String representing its
	 * answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
	 * are also the Names of the individual RavensFigures, obtained through
	 * RavensFigure.getName().
	 * 
	 * In addition to returning your answer at the end of the method, your Agent
	 * may also call problem.checkAnswer(String givenAnswer). The parameter
	 * passed to checkAnswer should be your Agent's current guess for the
	 * problem; checkAnswer will return the correct answer to the problem. This
	 * allows your Agent to check its answer. Note, however, that after your
	 * agent has called checkAnswer, it will *not* be able to change its answer.
	 * checkAnswer is used to allow your Agent to learn from its incorrect
	 * answers; however, your Agent cannot change the answer to a question it
	 * has already answered.
	 * 
	 * If your Agent calls checkAnswer during execution of Solve, the answer it
	 * returns will be ignored; otherwise, the answer returned at the end of
	 * Solve will be taken as your Agent's answer to this problem.
	 * 
	 * @param problem
	 *            the RavensProblem your agent should solve
	 * @return your Agent's answer to this problem
	 */
	public String Solve(RavensProblem problem) {
		RavensProblemCase casefile = new RavensProblemCase(problem);
		/* redirect debug output to file */
		PrintStream o = System.out;
			PrintStream f = new PrintStream(dfile);
			System.setOut(f);
		String response = productionSystem.solve(casefile);
		String answer = problem.checkAnswer(response);
		boolean correct = response.equals(answer);
		/* Keep statistics */
		problems++;
		if (correct)
			right++;
		/* Output the result */
		log.info(String.format("%s - Response: %s, Answer: %s, Accuracy: %.02f",
				problem.getName(), response, answer, right / problems));
		/* Record the result */
		casefile.setAnswer(answer);
		/* return output to stdout */
		System.setOut(o);
		return response;
	}

	public Logger getLogger() {
		return log;
	}

}
