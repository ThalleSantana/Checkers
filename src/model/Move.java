// Descrição: Esta classe representa um movimento.

package model;

import java.awt.Point;

// A classe representa um movimento e contém um peso associado ao movimento.
public class Move {
	
	// O peso correspondente a um movimento inválido.
	public static final double WEIGHT_INVALID = Double.NEGATIVE_INFINITY;

	// O índice inicial do movimento.
	private byte startIndex;
	
	// O índice final do movimento.
	private byte endIndex;
	
	// O peso associado ao movimento.
	private double weight;
	
	public Move(int startIndex, int endIndex) {
		setStartIndex(startIndex);
		setEndIndex(endIndex);
	}
	
	public Move(Point start, Point end) {
		setStartIndex(Board.toIndex(start));
		setEndIndex(Board.toIndex(end));
	}
	
	public int getStartIndex() {
		return startIndex;
	}
	
	public void setStartIndex(int startIndex) {
		this.startIndex = (byte) startIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public void setEndIndex(int endIndex) {
		this.endIndex = (byte) endIndex;
	}
	
	public Point getStart() {
		return Board.toPoint(startIndex);
	}
	
	public void setStart(Point start) {
		setStartIndex(Board.toIndex(start));
	}
	
	public Point getEnd() {
		return Board.toPoint(endIndex);
	}
	
	public void setEnd(Point end) {
		setEndIndex(Board.toIndex(end));
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void changeWeight(double delta) {
		this.weight += delta;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[startIndex=" + startIndex + ", "
				+ "endIndex=" + endIndex + ", weight=" + weight + "]";
	}
}
