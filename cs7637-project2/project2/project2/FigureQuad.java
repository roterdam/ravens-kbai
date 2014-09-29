package project2;

import java.util.HashMap;
import java.util.HashSet;

public class FigureQuad {

	public MappedFigurePair top;
	public MappedFigurePair left;
	public MappedFigurePair bottom;
	public MappedFigurePair right;
	public String label;

	public FigureQuad(RavensFigure a, RavensFigure b,
			RavensFigure c, RavensFigure d, HashMap<String, HashSet<String>> language) {
		this.top=new MappedFigurePair(a,b,language);
		this.left=new MappedFigurePair(a,c,language);
		this.bottom=new MappedFigurePair(c,d,language);
		this.right=new MappedFigurePair(b,d,language);
		this.label=d.getName();
	}
}
