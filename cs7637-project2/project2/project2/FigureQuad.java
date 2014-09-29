package project2;

public class FigureQuad {

	public MappedFigurePair top;
	public MappedFigurePair left;
	public MappedFigurePair bottom;
	public MappedFigurePair right;
	public String label;

	public FigureQuad(RavensFigure a, RavensFigure b,
			RavensFigure c, RavensFigure d) {
		this.top=new MappedFigurePair(a,b);
		this.left=new MappedFigurePair(a,c);
		this.bottom=new MappedFigurePair(c,d);
		this.right=new MappedFigurePair(b,d);
		this.label=d.getName();
	}
}
