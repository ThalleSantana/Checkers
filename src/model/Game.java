// Descrição: Esta classe representa um jogo de damas. Ele fornece um método para atualizar o estado do jogo e acompanhar quem é a vez.

package model;

import java.awt.Point;
import java.util.List;

import logic.MoveGenerator;
import logic.MoveLogic;

// A classe representa um jogo de damas e garante que todas as jogadas feitas sejam válidas de acordo com as regras das damas.
public class Game {

	// O estado atual do tabuleiro de damas.
	private Board board;
	
	// A bandeira que indica se é a vez do jogador 1.
	private boolean isP1Turn;
	
	// O índice do último salto, para permitir vários saltos em uma curva.
	private int skipIndex;
	
	public Game() {
		restart();
	}
	
	public Game(String state) {
		setGameState(state);
	}
	
	public Game(Board board, boolean isP1Turn, int skipIndex) {
		this.board = (board == null)? new Board() : board;
		this.isP1Turn = isP1Turn;
		this.skipIndex = skipIndex;
	}
	
	// Cria uma cópia deste jogo de forma que quaisquer modificações feitas em um não sejam feitas no outro.
	// devolva uma cópia exata deste jogo.
	public Game copy() {
		Game g = new Game();
		g.board = board.copy();
		g.isP1Turn = isP1Turn;
		g.skipIndex = skipIndex;
		return g;
	}
	
	// Redefine o jogo de damas para o estado inicial.
	public void restart() {
		this.board = new Board();
		this.isP1Turn = true;
		this.skipIndex = -1;
	}
	
	// Tenta fazer um movimento do ponto inicial ao ponto final.
	// start: o ponto de partida para o movimento.
	// end: o ponto final do movimento.
	// verdadeiro se e somente se uma atualização foi feita no estado do jogo.
	
	public boolean move(Point start, Point end) {
		if (start == null || end == null) {
			return false;
		}
		return move(Board.toIndex(start), Board.toIndex(end));
	}
	
	// Tenta fazer um movimento dado o índice inicial e final do movimento.
	// startIndex: o índice inicial do movimento.
	// endIndex: o índice final do movimento.
	// verdadeiro se e somente se uma atualização foi feita no estado do jogo.

	public boolean move(int startIndex, int endIndex) {
		
		// Valide a mudança
		if (!MoveLogic.isValidMove(this, startIndex, endIndex)) {
			return false;
		}
		
		// Faça o movimento
		Point middle = Board.middle(startIndex, endIndex);
		int midIndex = Board.toIndex(middle);
		this.board.set(endIndex, board.get(startIndex));
		this.board.set(midIndex, Board.EMPTY);
		this.board.set(startIndex, Board.EMPTY);
		
		// Faça da peça uma Dama, se necessário
		Point end = Board.toPoint(endIndex);
		int id = board.get(endIndex);
		boolean switchTurn = false;
		if (end.y == 0 && id == Board.WHITE_CHECKER) {
			this.board.set(endIndex, Board.WHITE_KING);
			switchTurn = true;
		} else if (end.y == 7 && id == Board.BLACK_CHECKER) {
			this.board.set(endIndex, Board.BLACK_KING);
			switchTurn = true;
		}
		
		// Verifique se a curva deve mudar (ou seja, sem mais saltos)
		boolean midValid = Board.isValidIndex(midIndex);
		if (midValid) {
			this.skipIndex = endIndex;
		}
		if (!midValid || MoveGenerator.getSkips(
				board.copy(), endIndex).isEmpty()) {
			switchTurn = true;
		}
		if (switchTurn) {
			this.isP1Turn = !isP1Turn;
			this.skipIndex = -1;
		}
		
		return true;
	}
	
	// Obtém uma cópia do estado atual do quadro.
	// uma não referência ao estado atual do tabuleiro do jogo.
	public Board getBoard() {
		return board.copy();
	}
	
	// Determina se o jogo acabou. O jogo termina se um ou ambos os jogadores não puderem fazer um único movimento durante sua vez.
	// retorna verdadeiro se o jogo acabou.
	public boolean isGameOver() {

		// Certifique-se de que haja pelo menos um de cada verificador
		List<Point> black = board.find(Board.BLACK_CHECKER);
		black.addAll(board.find(Board.BLACK_KING));
		if (black.isEmpty()) {
			return true;
		}
		List<Point> white = board.find(Board.WHITE_CHECKER);
		white.addAll(board.find(Board.WHITE_KING));
		if (white.isEmpty()) {
			return true;
		}
		
		// Verifique se o jogador atual pode se mover
		List<Point> test = isP1Turn? black : white;
		for (Point p : test) {
			int i = Board.toIndex(p);
			if (!MoveGenerator.getMoves(board, i).isEmpty() ||
					!MoveGenerator.getSkips(board, i).isEmpty()) {
				return false;
			}
		}
		
		// Sem movimentos
		return true;
	}
	
	public boolean isP1Turn() {
		return isP1Turn;
	}
	
	public void setP1Turn(boolean isP1Turn) {
		this.isP1Turn = isP1Turn;
	}
	
	public int getSkipIndex() {
		return skipIndex;
	}
	
	// Obtém o estado atual do jogo como uma string de dados que pode ser analisada
	// retorna uma string representando o estado atual do jogo.
	public String getGameState() {
		
		// Adicione o tabuleiro de jogo
		String state = "";
		for (int i = 0; i < 32; i ++) {
			state += "" + board.get(i);
		}
		
		// Adicione as outras informações
		state += (isP1Turn? "1" : "0");
		state += skipIndex;
		
		return state;
	}
	
	// Analisa uma string representando um estado do jogo que foi gerado
	// state: o estado do jogo.
	public void setGameState(String state) {
		
		restart();
		
		// casos triviais
		if (state == null || state.isEmpty()) {
			return;
		}
		
		// Atualize o tabuleiro
		int n = state.length();
		for (int i = 0; i < 32 && i < n; i ++) {
			try {
				int id = Integer.parseInt("" + state.charAt(i));
				this.board.set(i, id);
			} catch (NumberFormatException e) {}
		}
		
		// Atualize as outras informações
		if (n > 32) {
			this.isP1Turn = (state.charAt(32) == '1');
		}
		if (n > 33) {
			try {
				this.skipIndex = Integer.parseInt(state.substring(33));
			} catch (NumberFormatException e) {
				this.skipIndex = -1;
			}
		}
	}
}
