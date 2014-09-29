package project2;

import project1.FigurePair;

public class FigureQuad {

	public FigurePair pair_1h;
	public FigurePair pair_1v;
	public FigurePair pair_2h;
	public FigurePair pair_2v;

	public FigureQuad(RavensFigure a, RavensFigure b,
			RavensFigure c, RavensFigure d) {
		this.pair_1h=new FigurePair(a,b);
		this.pair_1v=new FigurePair(a,c);
		this.pair_2h=new FigurePair(c,d);
		this.pair_2v=new FigurePair(b,d);
	}

}
