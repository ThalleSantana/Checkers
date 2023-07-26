// Descrição: Esta classe é a representação gráfica da interface do usuário de um jogo de damas. É responsável por desenhar o tabuleiro de damas e permitir que as jogadas sejam feitas. Ele não fornece um método para permitir que o usuário altere as configurações do jogo ou reinicie-o.

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.Timer;

import logic.MoveGenerator;
import model.Board;
import model.Game;
import model.HumanPlayer;
import model.Player;


// A classe é um componente de interface gráfica do usuário capaz de desenhar qualquer estado de jogo de damas. Ele também lida com os turnos do jogador. Para jogadores humanos, isso significa interagir e selecionar peças no tabuleiro de damas. Para jogadores não humanos, isso significa usar a lógica implementada pelo próprio objeto do jogador especificado.
public class CheckerBoard extends JButton {

	private static final long serialVersionUID = -6014690893709316364L;
	
	// A quantidade de milissegundos antes que um jogador de computador faça uma jogada.
	private static final int TIMER_DELAY = 1000;
	
	// O número de pixels de preenchimento entre a borda deste componente e o tabuleiro real desenhado.
	private static final int PADDING = 16;

	// O jogo de damas que está sendo jogado neste componente.
	private Game game;
	
	// A janela que contém este componente de interface do usuário do tabuleiro de damas.
	private CheckersWindow window;
	
	// O jogador no controle das damas pretas.
	private Player player1;
	
	// O jogador no controle das damas brancas.
	private Player player2;
	
	// O último ponto que o jogador atual selecionou no tabuleiro de damas.
	private Point selected;
	
	// O sinalizador para determinar a cor do ladrilho selecionado. Se a seleção for válida, uma cor verde é usada para destacar o ladrilho. Caso contrário, uma cor vermelha é usada.
	private boolean selectionValid;
	
	// A cor dos ladrilhos claros.
	private Color lightTile;

	// A cor dos ladrilhos escura.
	private Color darkTile;
	
	// Uma bandeira de conveniência para verificar se o jogo acabou.
	private boolean isGameOver;
	
	// O cronômetro para controlar o quão rápido um jogador de computador faz um movimento.
	private Timer timer;
	
	public CheckerBoard(CheckersWindow window) {
		this(window, new Game(), null, null);
	}
	
	public CheckerBoard(CheckersWindow window, Game game, Player player1, Player player2) {
		
		// Configure o componente
		super.setBorderPainted(false);
		super.setFocusPainted(false);
		super.setContentAreaFilled(false);
		super.setBackground(Color.LIGHT_GRAY);
		this.addActionListener(new ClickListener());
		
		// Configure o jogo
		this.game = (game == null)? new Game() : game;
		this.lightTile = Color.WHITE;
		this.darkTile = Color.BLACK;
		this.window = window;
		setPlayer1(player1);
		setPlayer2(player2);
	}
	
	// Verifica se o jogo acabou e redesenha os componentes gráficos.
	public void update() {
		runPlayer();
		this.isGameOver = game.isGameOver();
		repaint();
	}
	
	private void runPlayer() {
		
		// Nada para fazer
		Player player = getCurrentPlayer();
		if (player == null || player.isHuman()) {
			return;
		}
		
		// Definir um cronômetro para executar
		this.timer = new Timer(TIMER_DELAY, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getCurrentPlayer().updateGame(game);
				timer.stop();
				update();
			}
		});
		this.timer.start();
	}
	
	
	public synchronized boolean setGameState(boolean testValue,
			String newState, String expected) {
		
		// Teste o valor se solicitado
		if (testValue && !game.getGameState().equals(expected)) {
			return false;
		}
		
		// Atualize o estado do jogo
		this.game.setGameState(newState);
		repaint();
		
		return true;
	}
	
	
	// Desenha o estado atual do jogo de damas.
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		Game game = this.game.copy();
		
		// Realizar cálculos
		final int BOX_PADDING = 4;
		final int W = getWidth(), H = getHeight();
		final int DIM = W < H? W : H, BOX_SIZE = (DIM - 2 * PADDING) / 8;
		final int OFFSET_X = (W - BOX_SIZE * 8) / 2;
		final int OFFSET_Y = (H - BOX_SIZE * 8) / 2;
		final int CHECKER_SIZE = Math.max(0, BOX_SIZE - 2 * BOX_PADDING);
		
		// Desenhar tabuleiro de damas
		g.setColor(Color.BLACK);
		g.drawRect(OFFSET_X - 1, OFFSET_Y - 1, BOX_SIZE * 8 + 1, BOX_SIZE * 8 + 1);
		g.setColor(lightTile);
		g.fillRect(OFFSET_X, OFFSET_Y, BOX_SIZE * 8, BOX_SIZE * 8);
		g.setColor(darkTile);
		for (int y = 0; y < 8; y ++) {
			for (int x = (y + 1) % 2; x < 8; x += 2) {
				g.fillRect(OFFSET_X + x * BOX_SIZE, OFFSET_Y + y * BOX_SIZE,
						BOX_SIZE, BOX_SIZE);
			}
		}
		
		// Destaque o bloco selecionado se for válido
		if (Board.isValidPoint(selected)) {
			g.setColor(selectionValid? Color.GREEN : Color.RED);
			g.fillRect(OFFSET_X + selected.x * BOX_SIZE,
					OFFSET_Y + selected.y * BOX_SIZE,
					BOX_SIZE, BOX_SIZE);
		}
		
		// Desenhe as damas
		Board b = game.getBoard();
		for (int y = 0; y < 8; y ++) {
			int cy = OFFSET_Y + y * BOX_SIZE + BOX_PADDING;
			for (int x = (y + 1) % 2; x < 8; x += 2) {
				int id = b.get(x, y);
				
				// Vazio, apenas pule
				if (id == Board.EMPTY) {
					continue;
				}
				
				int cx = OFFSET_X + x * BOX_SIZE + BOX_PADDING;
				
				// Peça preta
				if (id == Board.BLACK_CHECKER) {
					g.setColor(Color.DARK_GRAY);
					g.fillOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.BLACK);
					g.fillOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
				}
				
				// Dama preta
				else if (id == Board.BLACK_KING) {
					g.setColor(Color.DARK_GRAY);
					g.fillOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.fillOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.BLACK);
					g.fillOval(cx - 1, cy - 2, CHECKER_SIZE, CHECKER_SIZE);
				}
				
				// Peça branca
				else if (id == Board.WHITE_CHECKER) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.WHITE);
					g.fillOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
				}
				
				// Dama branca
				else if (id == Board.WHITE_KING) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.WHITE);
					g.fillOval(cx - 1, cy - 2, CHECKER_SIZE, CHECKER_SIZE);
				}
				
				// Qualquer dama (adicione alguns destaques extras)
				if (id == Board.BLACK_KING || id == Board.WHITE_KING) {
					g.setColor(new Color(255, 240,0));
					g.drawOval(cx - 1, cy - 2, CHECKER_SIZE, CHECKER_SIZE);
					g.drawOval(cx + 1, cy, CHECKER_SIZE - 4, CHECKER_SIZE - 4);
				}
			}
		}
		
		// Desenhe o sinal de turno do jogador
		String msg = game.isP1Turn()? "Turno do Jogador 1" : "Turno do Jogador 2";
		int width = g.getFontMetrics().stringWidth(msg);
		Color back = game.isP1Turn()? Color.BLACK : Color.WHITE;
		Color front = game.isP1Turn()? Color.WHITE : Color.BLACK;
		g.setColor(back);
		g.fillRect(W / 2 - width / 2 - 5, OFFSET_Y + 8 * BOX_SIZE + 2,
				width + 10, 15);
		g.setColor(front);
		g.drawString(msg, W / 2 - width / 2, OFFSET_Y + 8 * BOX_SIZE + 2 + 11);
		
		// Desenhe um sinal de fim de jogo
		if (isGameOver) {
			g.setFont(new Font("Arial", Font.BOLD, 20));
			msg = "Fim de Jogo!";
			width = g.getFontMetrics().stringWidth(msg);
			g.setColor(new Color(240, 240, 255));
			g.fillRoundRect(W / 2 - width / 2 - 5,
					OFFSET_Y + BOX_SIZE * 4 - 16,
					width + 10, 30, 10, 10);
			g.setColor(Color.RED);
			g.drawString(msg, W / 2 - width / 2, OFFSET_Y + BOX_SIZE * 4 + 7);
		}
	}
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = (game == null)? new Game() : game;
	}

	public CheckersWindow getWindow() {
		return window;
	}

	public void setWindow(CheckersWindow window) {
		this.window = window;
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = (player1 == null)? new HumanPlayer() : player1;
		if (game.isP1Turn() && !this.player1.isHuman()) {
			this.selected = null;
		}
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = (player2 == null)? new HumanPlayer() : player2;
		if (!game.isP1Turn() && !this.player2.isHuman()) {
			this.selected = null;
		}
	}
	
	public Player getCurrentPlayer() {
		return game.isP1Turn()? player1 : player2;
	}

	public Color getLightTile() {
		return lightTile;
	}

	public void setLightTile(Color lightTile) {
		this.lightTile = (lightTile == null)? Color.WHITE : lightTile;
	}

	public Color getDarkTile() {
		return darkTile;
	}

	public void setDarkTile(Color darkTile) {
		this.darkTile = (darkTile == null)? Color.BLACK : darkTile;
	}

	// Manipula um clique neste componente no ponto especificado. Se o jogador atual não for humano, esse método não fará nada. Caso contrário, o ponto selecionado é atualizado e um movimento é tentado se o último clique e este estiverem em ladrilhos pretos.
	// x: a coordenada x do clique neste componente.
	// y: a coordenada y do clique neste componente.
	private void handleClick(int x, int y) {
		
		// O jogo acabou ou o jogador atual não é humano
		if (isGameOver || !getCurrentPlayer().isHuman()) {
			return;
		}
		
		Game copy = game.copy();
		
		// Determine qual quadrado (se houver) foi selecionado
		final int W = getWidth(), H = getHeight();
		final int DIM = W < H? W : H, BOX_SIZE = (DIM - 2 * PADDING) / 8;
		final int OFFSET_X = (W - BOX_SIZE * 8) / 2;
		final int OFFSET_Y = (H - BOX_SIZE * 8) / 2;
		x = (x - OFFSET_X) / BOX_SIZE;
		y = (y - OFFSET_Y) / BOX_SIZE;
		Point sel = new Point(x, y);
		
		// Determinar se um movimento deve ser tentado
		if (Board.isValidPoint(sel) && Board.isValidPoint(selected)) {
			boolean change = copy.isP1Turn();
			String expected = copy.getGameState();
			boolean move = copy.move(selected, sel);
			boolean updated = (move?
					setGameState(true, copy.getGameState(), expected) : false);
			if (updated) {
			}
			change = (copy.isP1Turn() != change);
			this.selected = change? null : sel;
		} else {
			this.selected = sel;
		}
		
		// Verifique se a seleção é válida
		this.selectionValid = isValidSelection(
				copy.getBoard(), copy.isP1Turn(), selected);
		
		update();
	}
	
	// Verifica se um ponto selecionado é válido no contexto da vez do jogador atual.
	// b: a diretoria atual.
	// isP1Turn: a bandeira indicando se é a vez do jogador 1.
	// selected: o ponto a testar.
	// verdadeiro se e somente se o ponto selecionado for um verificador que teria permissão para fazer um movimento no turno atual.
	private boolean isValidSelection(Board b, boolean isP1Turn, Point selected) {

		// casos triviais
		int i = Board.toIndex(selected), id = b.get(i);
		if (id == Board.EMPTY || id == Board.INVALID) { // nenhuma peça aqui
			return false;
		} else if(isP1Turn ^ (id == Board.BLACK_CHECKER ||
				id == Board.BLACK_KING)) { // peça errada
			return false;
		} else if (!MoveGenerator.getSkips(b, i).isEmpty()) { // pular disponível
			return true;
		} else if (MoveGenerator.getMoves(b, i).isEmpty()) { // sem movimentos
			return false;
		}
		
		// Determine se há um pulo disponível para outra peça
		List<Point> points = b.find(
				isP1Turn? Board.BLACK_CHECKER : Board.WHITE_CHECKER);
		points.addAll(b.find(
				isP1Turn? Board.BLACK_KING : Board.WHITE_KING));
		for (Point p : points) {
			int checker = Board.toIndex(p);
			if (checker == i) {
				continue;
			}
			if (!MoveGenerator.getSkips(b, checker).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	// A classe é responsável por responder a eventos de clique no componente do tabuleiro de damas. Ele usa as coordenadas do mouse em relação ao local do componente do tabuleiro de damas.
	private class ClickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// Obtenha as novas coordenadas do mouse e manipule o clique
			Point m = CheckerBoard.this.getMousePosition();
			if (m != null) {
				handleClick(m.x, m.y);
			}
		}
	}
}
