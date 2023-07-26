// Descrição: Esta classe representa um jogador de computador que pode atualizar o estado do jogo sem interação do usuário.

package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import logic.MoveGenerator;
import logic.MoveLogic;

// A classe representa um jogador de computador e atualiza o tabuleiro com base em um modelo.
public class ComputerPlayer extends Player {
	
	// O peso de poder pular.
	private static final double WEIGHT_SKIP = 25;
	
	// O peso de poder pular na próxima curva.
	private static final double SKIP_ON_NEXT = 20;
	
	// O peso associado a estar seguro antes e depois.
	private static final double SAFE_SAFE = 5;

	// O peso associado a ser seguro e inseguro antes e depois.
	private static final double SAFE_UNSAFE = -40;

	// O peso associado a ser inseguro e seguro antes e depois.
	private static final double UNSAFE_SAFE = 40;

	// O peso associado a ser inseguro e depois inseguro antes e depois.
	private static final double UNSAFE_UNSAFE = -40;
	
	// O peso de um verificador sendo seguro.
	private static final double SAFE = 3;
	
	// O peso de um verificador não é seguro.
	private static final double UNSAFE = -5;
	
	// O fator usado para multiplicar alguns pesos quando a peça observada é uma Dama.
	private static final double KING_FACTOR = 2;

	@Override
	public boolean isHuman() {
		return false;
	}

	@Override
	public void updateGame(Game game) {
		
		// Nada para fazer
		if (game == null || game.isGameOver()) {
			return;
		}
			
		// Obtenha os movimentos disponíveis
		Game copy = game.copy();
		List<Move> moves = getMoves(copy);

		// Determine qual é o melhor
		int n = moves.size(), count = 1;
		double bestWeight = Move.WEIGHT_INVALID;
		for (int i = 0; i < n; i ++) {
			Move m = moves.get(i);
			getMoveWeight(copy.copy(), m);
			if (m.getWeight() > bestWeight) {
				count = 1;
				bestWeight = m.getWeight();
			} else if (m.getWeight() == bestWeight) {
				count ++;
			}
		}

		// Selecione aleatoriamente um movimento
		int move = ((int) (Math.random() * count)) % count;
		for (int i = 0; i < n; i ++) {
			Move m = moves.get(i);
			if (bestWeight == m.getWeight()) {
				if (move == 0) {
					game.move(m.getStartIndex(), m.getEndIndex());
				} else {
					move --;
				}
			}
		}
	}
	
	// Obtém todos os movimentos e pulos disponíveis para o jogador atual.
	// game: o estado atual do jogo.
	// uma lista de movimentos válidos que o jogador pode fazer.
	private List<Move> getMoves(Game game) {
		
		// O próximo movimento precisa ser um salto
		if (game.getSkipIndex() >= 0) {
			
			List<Move> moves = new ArrayList<>();
			List<Point> skips = MoveGenerator.getSkips(game.getBoard(),
					game.getSkipIndex());
			for (Point end : skips) {
				moves.add(new Move(game.getSkipIndex(), Board.toIndex(end)));
			}
			
			return moves;
		}
		
		// Pegue as damas
		List<Point> checkers = new ArrayList<>();
		Board b = game.getBoard();
		if (game.isP1Turn()) {
			checkers.addAll(b.find(Board.BLACK_CHECKER));
			checkers.addAll(b.find(Board.BLACK_KING));
		} else {
			checkers.addAll(b.find(Board.WHITE_CHECKER));
			checkers.addAll(b.find(Board.WHITE_KING));
		}
		
		// Determine se há algum salto
		List<Move> moves = new ArrayList<>();
		for (Point checker : checkers) {
			int index = Board.toIndex(checker);
			List<Point> skips = MoveGenerator.getSkips(b, index);
			for (Point end : skips) {
				Move m = new Move(index, Board.toIndex(end));
				m.changeWeight(WEIGHT_SKIP);
				moves.add(m);
			}
		}
		
		// Se não houver saltos, adicione os movimentos regulares
		if (moves.isEmpty()) {
			for (Point checker : checkers) {
				int index = Board.toIndex(checker);
				List<Point> movesEnds = MoveGenerator.getMoves(b, index);
				for (Point end : movesEnds) {
					moves.add(new Move(index, Board.toIndex(end)));
				}
			}
		}
		
		return moves;
	}
	
	// Obtém o número de saltos que podem ser feitos em um turno a partir de um determinado índice inicial.
	// game: o estado do jogo para verificar.
	// startIndex: o índice inicial dos saltos.
	// isP1Turn: a bandeira de virada do jogador original.
	// o número máximo de pulos disponíveis a partir do ponto determinado.
	private int getSkipDepth(Game game, int startIndex, boolean isP1Turn) {
		
		// Caso Trivial
		if (isP1Turn != game.isP1Turn()) {
			return 0;
		}
		
		// Obtenha a profundidade recursivamente
		List<Point> skips = MoveGenerator.getSkips(game.getBoard(), startIndex);
		int depth = 0;
		for (Point end : skips) {
			int endIndex = Board.toIndex(end);
			game.move(startIndex, endIndex);
			int testDepth = getSkipDepth(game, endIndex, isP1Turn);
			if (testDepth > depth) {
				depth = testDepth;
			}
		}
		
		return depth + (skips.isEmpty()? 0 : 1);
	}
	
	// Determina o peso de um movimento com base em vários fatores (por exemplo, quão seguro o verificador está antes/depois, se ele pode levar o verificador de um oponente depois, etc.).
	// game: o estado atual do jogo.
	// m: o movimento para testar.
	private void getMoveWeight(Game game, Move m) {
		
		Point start = m.getStart(), end = m.getEnd();
		int startIndex = Board.toIndex(start), endIndex = Board.toIndex(end);
		Board b = game.getBoard();
		boolean changed = game.isP1Turn();
		boolean safeBefore = MoveLogic.isSafe(b, start);
		int id = b.get(startIndex);
		boolean isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
		
		// Definir o peso inicial
		m.changeWeight(getSafetyWeight(b, game.isP1Turn()));
		
		// Faça o movimento
		if (!game.move(m.getStartIndex(), m.getEndIndex())) {
			m.setWeight(Move.WEIGHT_INVALID);
			return;
		}
		b = game.getBoard();
		changed = (changed != game.isP1Turn());
		id = b.get(endIndex);
		isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
		boolean safeAfter = true;
		
		// Determine se um pulo pode ser feito no próximo movimento
		if (changed) {
			safeAfter = MoveLogic.isSafe(b, end);
			int depth = getSkipDepth(game, endIndex, !game.isP1Turn());
			if (safeAfter) {
				m.changeWeight(SKIP_ON_NEXT * depth * depth);
			} else {
				m.changeWeight(SKIP_ON_NEXT);
			}
		}
		
		// Verifique quantos saltos estão disponíveis
		else {
			int depth = getSkipDepth(game, startIndex, game.isP1Turn());
			m.changeWeight(WEIGHT_SKIP * depth * depth);
		}
		
		// Adicione o peso apropriado ao nível de segurança da peça
		if (safeBefore && safeAfter) {
			m.changeWeight(SAFE_SAFE);
		} else if (!safeBefore && safeAfter) {
			m.changeWeight(UNSAFE_SAFE);
		} else if (safeBefore && !safeAfter) {
			m.changeWeight(SAFE_UNSAFE * (isKing? KING_FACTOR : 1));
		} else {
			m.changeWeight(UNSAFE_UNSAFE);
		}
		m.changeWeight(getSafetyWeight(b,
				changed? !game.isP1Turn() : game.isP1Turn()));
	}
	
	// Calcula o estado de 'segurança' do jogo para o jogador especificado. O jogador tem damas 'seguras' e 'inseguras', que respectivamente, não podem e podem ser puladas pelo oponente no próximo turno.
	// b: o estado da placa para verificar.
	// isBlack: a bandeira indicando se as damas pretas devem ser observadas.
	// o peso correspondente à segurança das peças do jogador.
	private double getSafetyWeight(Board b, boolean isBlack) {
		
		// Pegue as damas
		double weight = 0;
		List<Point> checkers = new ArrayList<>();
		if (isBlack) {
			checkers.addAll(b.find(Board.BLACK_CHECKER));
			checkers.addAll(b.find(Board.BLACK_KING));
		} else {
			checkers.addAll(b.find(Board.WHITE_CHECKER));
			checkers.addAll(b.find(Board.WHITE_KING));
		}
		
		// Determine as condições para cada verificador
		for (Point checker : checkers) {
			int index = Board.toIndex(checker);
			int id = b.get(index);
			boolean isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
			if (MoveLogic.isSafe(b, checker)) {
				weight += SAFE;
			} else {
				weight += UNSAFE * (isKing? KING_FACTOR : 1);
			}
		}
		
		return weight;
	}
}
