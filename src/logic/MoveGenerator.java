// Descrição: Essa classe é responsável por conseguir movimentos possíveis.

package logic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import model.Board;

// A classe fornece um método para determinar se uma determinada peça pode fazer qualquer movimento ou pular.
public class MoveGenerator {

	/*
	 * Obtém uma lista de pontos finais de movimento para um determinado índice inicial.
	 * 
	 * board: o tabuleiro para procurar movimentos disponíveis.
	 * start: o índice central para procurar movimentos ao redor.
	 * a lista de pontos de modo que o início de um determinado ponto represente um movimento disponível.
	 */
	public static List<Point> getMoves(Board board, Point start) {
		return getMoves(board, Board.toIndex(start));
	}
	
	/*
	 *  Obtém uma lista de pontos finais de movimento para um determinado índice inicial.
	 * 
	 * board: o tabuleiro para procurar movimentos disponíveis.
	 * startIndex: o índice central para procurar movimentos ao redor.
	 * 	a lista de pontos de modo que o início de um determinado ponto represente um movimento disponível.
	 */
	public static List<Point> getMoves(Board board, int startIndex) {
		
		// Casos Triviais
		List<Point> endPoints = new ArrayList<>();
		if (board == null || !Board.isValidIndex(startIndex)) {
			return endPoints;
		}
		
		// Determinar possíveis pontos
		int id = board.get(startIndex);
		Point p = Board.toPoint(startIndex);
		addPoints(endPoints, p, id, 1);
		
		// Remover pontos inválidos
		for (int i = 0; i < endPoints.size(); i ++) {
			Point end = endPoints.get(i);
			if (board.get(end.x, end.y) != Board.EMPTY) {
				endPoints.remove(i --);
			}
		}
		
		return endPoints;
	}
	
	/*
	 * Obtém uma lista de pontos finais ignorados para um determinado ponto inicial.
	 * 
	 * board: o quadro para procurar saltos disponíveis.
	 * start: o índice central para procurar saltos ao redor.
	 * a lista de pontos de modo que o início de um determinado ponto represente um salto disponível.
	 */
	public static List<Point> getSkips(Board board, Point start) {
		return getSkips(board, Board.toIndex(start));
	}
	
	/*
	 * Obtém uma lista de pontos finais ignorados para um determinado índice inicial.
	 * 
	 * board: o quadro para procurar saltos disponíveis.
	 * startIndex: o índice central para procurar saltos ao redor.
	 * a lista de pontos de modo que o início de um determinado ponto represente um salto disponível.
	 */
	public static List<Point> getSkips(Board board, int startIndex) {
		
		// Casos Triviais
		List<Point> endPoints = new ArrayList<>();
		if (board == null || !Board.isValidIndex(startIndex)) {
			return endPoints;
		}
		
		// Determinar possíveis pontos
		int id = board.get(startIndex);
		Point p = Board.toPoint(startIndex);
		addPoints(endPoints, p, id, 2);
		
		// Remover pontos inválidos
		for (int i = 0; i < endPoints.size(); i ++) {
			
			// Verifique se o salto é válido
			Point end = endPoints.get(i);
			if (!isValidSkip(board, startIndex, Board.toIndex(end))) {
				endPoints.remove(i --);
			}
		}

		return endPoints;
	}
	
	/*
	 * Verifica se um salto é válido.
	 * 
	 * board: o tabuleiro para verificar.
	 * startIndex: o índice inicial do salto.
	 * endIndex: o índice final do salto.
	 * verdadeiro se e somente se o salto puder ser executado.
	 */
	public static boolean isValidSkip(Board board, int startIndex, int endIndex) {
		
		if (board == null) {
			return false;
		}

		// Verifique se o final está vazio
		if (board.get(endIndex) != Board.EMPTY) {
			return false;
		}
		
		// Verifique se o meio é inimigo
		int id = board.get(startIndex);
		int midID = board.get(Board.toIndex(Board.middle(startIndex, endIndex)));
		if (id == Board.INVALID || id == Board.EMPTY) {
			return false;
		} else if (midID == Board.INVALID || midID == Board.EMPTY) {
			return false;
		} else if ((midID == Board.BLACK_CHECKER || midID == Board.BLACK_KING)
				^ (id == Board.WHITE_CHECKER || id == Board.WHITE_KING)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Adiciona pontos que podem resultar em movimentos/pulações.
	 * 
	 * points: a lista de pontos a serem adicionados.
	 * p: o ponto central.
	 * id: o ID no ponto central.
	 * delta: a quantidade a somar/subtrair.
	 */
	public static void addPoints(List<Point> points, Point p, int id, int delta) {
		
		// Adicionar pontos movendo para baixo
		boolean isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
		if (isKing || id == Board.BLACK_CHECKER) {
			points.add(new Point(p.x + delta, p.y + delta));
			points.add(new Point(p.x - delta, p.y + delta));
		}
		
		// Adicionar pontos subindo
		if (isKing || id == Board.WHITE_CHECKER) {
			points.add(new Point(p.x + delta, p.y - delta));
			points.add(new Point(p.x - delta, p.y - delta));
		}
	}
}
