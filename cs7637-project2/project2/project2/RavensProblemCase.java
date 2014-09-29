package project2;

public class RavensProblemCase {

	private RavensProblem problem;
	private String response;
	private String answer;
	private String solver ;

	public RavensProblemCase(RavensProblem problem) {
		this.problem=problem;
	}

	public void setResponse(String response) {
		this.response=response;
	}

	public RavensProblem getProblem() {
		return problem;
	}

	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer=answer;
	}

	public void setSolver(String name) {
		this.solver =name;
	}
	
	public String getSolver() {
		return solver;
	}
}
