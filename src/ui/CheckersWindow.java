// Descrição: Esta classe é uma janela usada para jogar damas. Ele também contém um componente para alterar as opções do jogo.

package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Player;

// A classe é responsável por gerenciar uma janela. Esta janela contém um jogo de damas e também opções para alterar as configurações do jogo
public class CheckersWindow extends JFrame {

	private static final long serialVersionUID = 8782122389400590079L;
	
	// A largura padrão da janela do jogo de damas.
	public static final int DEFAULT_WIDTH = 500;
	
	// A altura padrão da janela do jogo de damas.
	public static final int DEFAULT_HEIGHT = 600;
	
	// O título padrão para a janela do jogo de damas.
	public static final String DEFAULT_TITLE = "Jogo de Damas em Java";
	
	// O componente do tabuleiro de damas jogando o jogo atualizável.
	private CheckerBoard board;
	
	private OptionPanel opts;
	
	public CheckersWindow() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TITLE);
	}
	
	public CheckersWindow(Player player1, Player player2) {
		this();
		setPlayer1(player1);
		setPlayer2(player2);
	}
	
	public CheckersWindow(int width, int height, String title) {
		
		// Configurar a janela
		super(title);
		super.setSize(width, height);
		super.setLocationByPlatform(true);
		
		// Configure os componentes
		JPanel layout = new JPanel(new BorderLayout());
		this.board = new CheckerBoard(this);
		this.opts = new OptionPanel(this);
		layout.add(board, BorderLayout.CENTER);
		layout.add(opts, BorderLayout.SOUTH);
		this.add(layout);
	
	}
	
	public CheckerBoard getBoard() {
		return board;
	}

	// Atualiza o tipo de jogador que está sendo usado para o jogador 1.
	// player1: a nova instância do jogador para controlar o jogador 1.
	public void setPlayer1(Player player1) {
		this.board.setPlayer1(player1);
		this.board.update();
	}
	
	// Atualiza o tipo de jogador que está sendo usado para o jogador 2.
	// player2: a nova instância do jogador para controlar o jogador 2.
	public void setPlayer2(Player player2) {
		this.board.setPlayer2(player2);
		this.board.update();
	}
	
	// Redefine o jogo de damas na janela.
	public void restart() {
		this.board.getGame().restart();
		this.board.update();
	}
	
	public void setGameState(String state) {
		this.board.getGame().setGameState(state);
	}
}
