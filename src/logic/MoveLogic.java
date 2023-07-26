// Descrição: Esta classe simplesmente valida movimentos.

package logic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import model.Board;
import model.Game;

// A classe determina o que é um movimento válido. Ele implementa totalmente todas as regras de damas.
public class MoveLogic {

	/*
	 * Determina se o movimento especificado é válido com base nas regras de damas.
	 * 
	 * game: o jogo para verificar contra.
	 * startIndex: o índice inicial do movimento.
	 * endIndex: o índice final do movimento.
	 * verdadeiro se o movimento for legal de acordo com as regras de damas.
	 */
	public static boolean isValidMove(Game game,
			int startIndex, int endIndex) {
		return game == null? false : isValidMove(game.getBoard(),
				game.isP1Turn(), startIndex, endIndex, game.getSkipIndex());
	}
	
	/**
	 * Determina se o movimento especificado é válido com base nas regras de damas.
	 * 
	 * board: o tabuleiro atual para verificar.
	 * isP1Turn: a bandeira indicando se é a vez do jogador 1.
	 * startIndex: o índice inicial do movimento.
	 * endIndex: o índice final do movimento.
	 * skipIndex: o índice do último pulo neste turno.
	 * verdadeiro se o movimento for legal de acordo com as regras de damas.
	 */
	public static boolean isValidMove(Board board, boolean isP1Turn,
			int startIndex, int endIndex, int skipIndex) {
		
		// Verificações básicas
		if (board == null || !Board.isValidIndex(startIndex) ||
				!Board.isValidIndex(endIndex)) {
			return false;
		} else if (startIndex == endIndex) {
			return false;
		} else if (Board.isValidIndex(skipIndex) && skipIndex != startIndex) {
			return false;
		}
		
		// Realize os testes para validar o movimento
		if (!validateIDs(board, isP1Turn, startIndex, endIndex)) {
			return false;
		} else if (!validateDistance(board, isP1Turn, startIndex, endIndex)) {
			return false;
		}
		
		// Passou em todos os testes
		return true;
	}
	
	/*
	 * Valida todos os valores relacionados ao ID para início, fim e meio (se o movimento for um salto).
	 * 
	 * board: o tabuleiro atual para verificar.
	 * isP1Turn: a bandeira indicando se é a vez do jogador 1.
	 * startIndex: o índice inicial do movimento.
	 * endIndex: o índice final do movimento.
	 * verdadeiro se e somente se todos os IDs forem válidos.
	 */
	private static boolean validateIDs(Board board, boolean isP1Turn,
			int startIndex, int endIndex) {
		
		// Verifique se o final está livre
		if (board.get(endIndex) != Board.EMPTY) {
			return false;
		}
		
		// Verifique se o ID adequado
		int id = board.get(startIndex);
		if ((isP1Turn && id != Board.BLACK_CHECKER && id != Board.BLACK_KING)
				|| (!isP1Turn && id != Board.WHITE_CHECKER
				&& id != Board.WHITE_KING)) {
			return false;
		}
		
		// Verifique o meio
		Point middle = Board.middle(startIndex, endIndex);
		int midID = board.get(Board.toIndex(middle));
		if (midID != Board.INVALID && ((!isP1Turn &&
				midID != Board.BLACK_CHECKER && midID != Board.BLACK_KING) ||
				(isP1Turn && midID != Board.WHITE_CHECKER &&
				midID != Board.WHITE_KING))) {
			return false;
		}
		
		// Passou em todos os testes
		return true;
	}
	
	/*
	 * Verifica se o movimento é diagonal e de magnitude 1 ou 2 na direção correta. Se a magnitude não for 2 (ou seja, não for um salto), ele verifica se não há saltos disponíveis por outros verificadores do mesmo jogador.
	 * 
	 * board: o tabuleiro atual para verificar.
	 * isP1Turn: a bandeira indicando se é a vez do jogador 1.
	 * startIndex: o índice inicial do movimento.
	 * endIndex: o índice final do movimento.
	 * verdadeiro se e somente se a distância do movimento for válida.
	 */
	private static boolean validateDistance(Board board, boolean isP1Turn,
			int startIndex, int endIndex) {
		
		// Verifique se foi um movimento diagonal
		Point start = Board.toPoint(startIndex);
		Point end = Board.toPoint(endIndex);
		int dx = end.x - start.x;
		int dy = end.y - start.y;
		if (Math.abs(dx) != Math.abs(dy) || dx == 0) {
			return false;
		}
		
		// Verifique se estava na direção certa
		int id = board.get(startIndex);
		if ((id == Board.WHITE_CHECKER && dy > 0) ||
				(id == Board.BLACK_CHECKER && dy < 0)) {
			return false;
		}
		
		// Verifique se isso não é um salto, não há nenhum disponível
		Point middle = Board.middle(startIndex, endIndex);
		int midID = board.get(Board.toIndex(middle));
		if (midID < 0) {
			
			// Obtenha as damas corretas
			List<Point> checkers;
			if (isP1Turn) {
				checkers = board.find(Board.BLACK_CHECKER);
				checkers.addAll(board.find(Board.BLACK_KING));
			} else {
				checkers = board.find(Board.WHITE_CHECKER);
				checkers.addAll(board.find(Board.WHITE_KING));
			}
			
			// Verifique se algum deles tem um salto disponível
			for (Point p : checkers) {
				int index = Board.toIndex(p);
				if (!MoveGenerator.getSkips(board, index).isEmpty()) {
					return false;
				}
			}
		}
		
		// Passou em todos os testes
		return true;
	}
	
	/*
	 * Verifica se a peça selecionada é seguro (ou seja, o oponente não pode pular o verificador).
	 * 
	 * board: o estado atual da placa.
	 * checker: o ponto onde a peça de teste está localizado.
	 * verdadeiro se e somente se o verificador no ponto estiver seguro.
	 */
	public static boolean isSafe(Board board, Point checker) {
		
		// Casos Triviais
		if (board == null || checker == null) {
			return true;
		}
		int index = Board.toIndex(checker);
		if (index < 0) {
			return true;
		}
		int id = board.get(index);
		if (id == Board.EMPTY) {
			return true;
		}
		
		// Determine se pode ser ignorado
		boolean isBlack = (id == Board.BLACK_CHECKER || id == Board.BLACK_KING);
		List<Point> check = new ArrayList<>();
		MoveGenerator.addPoints(check, checker, Board.BLACK_KING, 1);
		for (Point p : check) {
			int start = Board.toIndex(p);
			int tid = board.get(start);
			
			// Nada aqui
			if (tid == Board.EMPTY || tid == Board.INVALID) {
				continue;
			}
			
			// Verificar ID
			boolean isWhite = (tid == Board.WHITE_CHECKER ||
					tid == Board.WHITE_KING);
			if (isBlack && !isWhite) {
				continue;
			}
			boolean isKing = (tid == Board.BLACK_KING || tid == Board.BLACK_KING);
			
			// Determine se a direção de salto é válida
			int dx = (checker.x - p.x) * 2;
			int dy = (checker.y - p.y) * 2;
			if (!isKing && (isWhite ^ (dy < 0))) {
				continue;
			}
			int endIndex = Board.toIndex(new Point(p.x + dx, p.y + dy));
			if (MoveGenerator.isValidSkip(board, start, endIndex)) {
				return false;
			}
		}
		
		return true;
	}
}
