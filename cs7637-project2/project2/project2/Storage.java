package project2;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.UUID;

public class Storage {

	public class CaseNotFoundException extends Exception {
		private static final long serialVersionUID = -5298764044129421261L;
	}

	ArrayList<RavensProblemCase> casefiles = new ArrayList<RavensProblemCase>();
	
	public ArrayList<RavensProblemCase> findCaseByProblem(RavensProblem problem) throws CaseNotFoundException {
		ArrayList<RavensProblemCase> matches = new ArrayList<RavensProblemCase>();
		for (RavensProblemCase c : casefiles) {
			if (c.getProblem().getName().equals(problem.getName())
					&& c.getProblem().getProblemType()
							.equals(problem.getProblemType())) {
				matches.add(c);
			}
		}
		return matches;
	}

	public void provideFeedbackOnCase(String uid, String answer) throws CaseNotFoundException {
		RavensProblemCase c = findCaseByUid(uid);
		if (c != null)
			c.setAnswer(answer);
		else
			throw new Storage.CaseNotFoundException();
	}

	private RavensProblemCase findCaseByUid(String uid) throws CaseNotFoundException {
		UUID uuid = UUID.fromString(uid);
		for (RavensProblemCase c : casefiles) {
			if (c.getUID().equals(uuid)) {
				return c;
			}
		}
		throw new Storage.CaseNotFoundException();
	}

	public void save(RavensProblemCase casefile) throws IOException {
		File storage = new File("storage/"+casefile.getProblem().getName());
		storage.mkdirs();
		File f = new File(storage,casefile.getUID()+".txt");
		casefile.write(f);
	}
}
