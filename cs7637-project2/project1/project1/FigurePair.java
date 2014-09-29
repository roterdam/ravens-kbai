package project1;

import project2.*;
public class FigurePair {

	private RavensFigure left;
	private RavensFigure right;

	public FigurePair(RavensFigure left, RavensFigure right) {
		this.setLeft(left);
		this.setRight(right);
	}

	public RavensFigure getLeft() {
		return left;
	}

	public void setLeft(RavensFigure left) {
		this.left = left;
	}

	public RavensFigure getRight() {
		return right;
	}

	public void setRight(RavensFigure right) {
		this.right = right;
	}

}
