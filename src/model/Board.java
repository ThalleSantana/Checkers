/* Descrição: Esta classe implementa um tabuleiro de damas 8x8. De acordo com as regras padrão, uma peça só pode se mover em ladrilhos pretos, o que 
 * significa que existem apenas 32 ladrilhos disponíveis. Ele usa três inteiros para representar o tabuleiro, dando 3 bits para cada ladrilho preto.
 */

package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/* A classe representa um estado de jogo para damas. Um tabuleiro de damas padrão tem 8 x 8 (64) ladrilhos, alternando branco/preto. 
 * Damas só são permitidas em ladrilhos pretos e, portanto, só podem se mover na diagonal. A placa é otimizada para usar o mínimo de espaço de 
 * memória possível e usa apenas 3 números inteiros para representar o estado da placa (3 bits para cada uma das 32 peças). Isso torna rápido e 
 * eficiente o estado da placa;
 */
  
// Essa classe usa números inteiros para representar o estado de cada bloco e usa especificamente constantes para IDs;

public class Board {
	
	//Um ID indicando um ponto não estava no tabuleiro de damas.
	public static final int INVALID = -1;

	//A ID de um bloco de tabuleiro de damas vazio.
	public static final int EMPTY = 0;

	//A ID de uma peça preta no tabuleiro de damas.
	public static final int BLACK_CHECKER = 4 * 1 + 2 * 1 + 1 * 0;
	
	//A ID de uma peça branco no tabuleiro de damas.
	public static final int WHITE_CHECKER = 4 * 1 + 2 * 0 + 1 * 0;

	//A ID de uma peça preto que também é uma Dama.
	public static final int BLACK_KING = 4 * 1 + 2 * 1 + 1 * 1;
	
	//A ID de uma peça branca que também é uma Dama.
	public static final int WHITE_KING = 4 * 1 + 2 * 0 + 1 * 1;

	//O estado atual do quadro, representado como três números inteiros.
	private int[] state;
	
	//Constrói um novo tabuleiro de jogo de damas, pré-preenchido com um novo estado de jogo.
	public Board() {
		reset();
	}
	
	//Cria uma cópia exata do tabuleiro. Quaisquer alterações feitas na cópia não afetarão o objeto atual.
	public Board copy() {
		Board copy = new Board();
		copy.state = state.clone();
		return copy;
	}
	
	//Redefine o tabuleiro de damas para o estado original do jogo com damas pretas na parte superior e brancas na parte inferior. Existem 12 damas pretas e 12 damas brancas.
	public void reset() {

		// Redefinir o estado
		this.state = new int[3];
		for (int i = 0; i < 12; i ++) {
			set(i, BLACK_CHECKER);
			set(31 - i, WHITE_CHECKER);
		}
	}
	
	//Pesquisa no tabuleiro de damas e encontra blocos pretos que correspondem ao ID especificado.
	//id: o ID a ser pesquisado.
	//uma lista de pontos no quadro com o ID especificado. Se nenhum existir, uma lista vazia será retornada.
	public List<Point> find(int id) {
		
		// Encontre todos os ladrilhos pretos com IDs correspondentes
		List<Point> points = new ArrayList<>();
		for (int i = 0; i < 32; i ++) {
			if (get(i) == id) {
				points.add(toPoint(i));
			}
		}
		
		return points;
	}
	
	// Define o ID de um ladrilho preto no tabuleiro no local especificado. Se o local não for um bloco preto, nada será atualizado. Se o ID for menor que 0, o tabuleiro no local será definido como vazio
	// x: a coordenada x no quadro (de 0 a 7 inclusive).
	// y: a coordenada y no quadro (de 0 a 7 inclusive).
	// id: o novo ID para definir o ladrilho preto.
	public void set(int x, int y, int id) {
		set(toIndex(x, y), id);
	}
	
	// Define o ID de um ladrilho preto no tabuleiro no local especificado. Se o local não for um bloco preto, nada será atualizado. Se o ID for menor que 0, o tabuleiro no local será definido como vazio. 
	// index: o índice do ladrilho preto (de 0 a 31 inclusive).
	// id: o novo ID para definir o ladrilho preto.
	public void set(int index, int id) {
		
		// Fora de alcance
		if (!isValidIndex(index)) {
			return;
		}
		
		// ID inválido, então apenas defina como VAZIO
		if (id < 0) {
			id = EMPTY;
		}
		
		// Definir os bits de estado
		for (int i = 0; i < state.length; i ++) {
			boolean set = ((1 << (state.length - i - 1)) & id) != 0;
			this.state[i] = setBit(state[i], index, set);
		}
	}
	
	// Obtém o ID correspondente ao ponto especificado no tabuleiro de damas.
	// x: a coordenada x no quadro (de 0 a 7 inclusive).
	// y: a coordenada y no quadro (de 0 a 7 inclusive).
	// o ID no local especificado ou INVÁLIDO se o local não estiver no tabuleiro ou for um ladrilho branco.
	
	public int get(int x, int y) {
		return get(toIndex(x, y));
	}
	
	// Obtém o ID correspondente ao ponto especificado no tabuleiro de damas.
	// index: o índice do ladrilho preto (de 0 a 31 inclusive).
	// o ID no local especificado ou INVALID se o local não estiver no quadro.
	
	public int get(int index) {
		if (!isValidIndex(index)) {
			return INVALID;
		}
		return getBit(state[0], index) * 4 + getBit(state[1], index) * 2
				+ getBit(state[2], index);
	}
	
	// Converte um índice de bloco preto (0 a 31 inclusive) em um ponto (x, y), de modo que o índice 0 seja (1, 0), o índice 1 seja (3, 0), ... o índice 31 seja (7, 7 ).
	// index: o índice do ladrilho preto a ser convertido em um ponto.
	// o ponto (x, y) correspondente ao índice do ladrilho preto ou o ponto (-1, -1) se o índice não estiver entre 0 - 31 (inclusive).
	
	public static Point toPoint(int index) {
		int y = index / 4;
		int x = 2 * (index % 4) + (y + 1) % 2;
		return !isValidIndex(index)? new Point(-1, -1) : new Point(x, y);
	}
	
	// Converte um ponto em um índice de um ladrilho preto no tabuleiro de damas, de modo que (1, 0) é o índice 0, (3, 0) é o índice 1, ... (7, 7) é o índice 31.
	// x: a coordenada x no quadro (de 0 a 7 inclusive).
	// y: a coordenada y no quadro (de 0 a 7 inclusive).
	// o índice do ladrilho preto ou -1 se o ponto não for um ladrilho preto.
	
	public static int toIndex(int x, int y) {
		
		// Inválido (x, y) (ou seja, fora do tabuleiro ou ladrilho branco)
		if (!isValidPoint(new Point(x, y))) {
			return -1;
		}
		
		return y * 4 + x / 2;
	}
	
	// Converte um ponto em um índice de um ladrilho preto no tabuleiro de damas, de modo que (1, 0) é o índice 0, (3, 0) é o índice 1, ... (7, 7) é o índice 31.
	// p: o ponto a ser convertido em um índice.
	// o índice do ladrilho preto ou -1 se o ponto não for um ladrilho preto.
	
	public static int toIndex(Point p) {
		return (p == null)? -1 : toIndex(p.x, p.y);
	}
	
	
	 // Define ou limpa o bit especificado no valor de destino e retorna o valor atualizado. 
	 // target: o valor de destino a ser atualizado.
	 // bit: o bit a atualizar (de 0 a 31 inclusive).
	 // set: verdadeiro para definir o bit, false para limpar o bit.
	 // o valor alvo atualizado com o bit definido ou limpo.

	public static int setBit(int target, int bit, boolean set) {
		
		// Nada para fazer
		if (bit < 0 || bit > 31) {
			return target;
		}
		
		// Defina o bit
		if (set) {
			target |= (1 << bit);
		}
		
		// Limpe o bit
		else {
			target &= (~(1 << bit));
		}
		
		return target;
	}
	
	// Obtém o estado de um bit e determina se está definido (1) ou não (0). 
	// target: o valor de destino para obter o bit.
	// bit: o bit a ser obtido (de 0 a 31 inclusive).
	// 1 se e somente se o bit especificado estiver definido, 0 caso contrário.
	 
	public static int getBit(int target, int bit) {
		
		// Fora de alcance
		if (bit < 0 || bit > 31) {
			return 0;
		}
		
		return (target & (1 << bit)) != 0? 1 : 0;
	}
	
	
	// Obtém o ponto médio no tabuleiro de damas entre dois pontos. 
	// p1: o primeiro ponto de uma peça preta no tabuleiro de damas.
	// p2: o segundo ponto de uma peça preta no tabuleiro de damas.
	// o ponto médio entre dois pontos ou (-1, -1) se os pontos não estiverem no tabuleiro, não estiverem distantes 2 um do outro em x e y ou estiverem em um ladrilho branco.
	public static Point middle(Point p1, Point p2) {
		
		// Um ponto não é inicializado
		if (p1 == null || p2 == null) {
			return new Point(-1, -1);
		}
		
		return middle(p1.x, p1.y, p2.x, p2.y);
	}
	
	// Obtém o ponto médio no tabuleiro entre dois pontos. 
	// index1: o índice do primeiro ponto (de 0 a 31 inclusive).
	// index2: o índice do segundo ponto (de 0 a 31 inclusive).
	// o ponto médio entre dois pontos ou (-1, -1) se os pontos não estiverem no tabuleiro, não estiverem distantes 2 um do outro em x e y ou estiverem em um ladrilho branco.

	public static Point middle(int index1, int index2) {
		return middle(toPoint(index1), toPoint(index2));
	}
	
	// Obtém o ponto médio no tabuleiro entre dois pontos.
	// x1: a coordenada x do primeiro ponto.
	// y1: a coordenada y do primeiro ponto.
	// x2: a coordenada x do segundo ponto.
	// y2: a coordenada y do segundo ponto.
	// o ponto médio entre dois pontos ou (-1, -1) se os pontos não estiverem no tabuleiro, não estiverem distantes 2 um do outro em x e y ou estiverem em um ladrilho branco.

	public static Point middle(int x1, int y1, int x2, int y2) {
		
		// Verifique as coordenadas
		int dx = x2 - x1, dy = y2 - y1;
		if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0 || // Não está no tabuleiro
				x1 > 7 || y1 > 7 || x2 > 7 || y2 > 7) {
			return new Point(-1, -1);
		} else if (x1 % 2 == y1 % 2 || x2 % 2 == y2 % 2) { // Ladrilho branco
			return new Point(-1, -1);
		} else if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) != 2) {
			return new Point(-1, -1);
		}
		
		return new Point(x1 + dx / 2, y1 + dy / 2);
	}
	
	// Verifica se um índice corresponde a um ladrilho preto no tabuleiro de damas.
	// testIndex: o índice para verificar.
	// true se e somente se o índice estiver entre 0 e 31 inclusive.
	public static boolean isValidIndex(int testIndex) {
		return testIndex >= 0 && testIndex < 32;
	}
	
	// Verifica se um ponto corresponde a uma peça preta no tabuleiro de damas. 
	// testPoint: o ponto a verificar.
	// verdadeiro se e somente se o ponto estiver no tabuleiro, especificamente em uma peça preta.

	public static boolean isValidPoint(Point testPoint) {
		
		if (testPoint == null) {
			return false;
		}
		
		// Verifique se está na placa
		final int x = testPoint.x, y = testPoint.y;
		if (x < 0 || x > 7 || y < 0 || y > 7) {
			return false;
		}
		
		// Verifique se está em um ladrilho preto
		if (x % 2 == y % 2) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		String obj = getClass().getName() + "[";
		for (int i = 0; i < 31; i ++) {
			obj += get(i) + ", ";
		}
		obj += get(31);
		
		return obj + "]";
	}
}
