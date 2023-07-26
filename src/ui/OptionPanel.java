// Descrição: Esta classe é uma interface de usuário para interagir com uma janela de jogo de damas.

package ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.ComputerPlayer;
import model.HumanPlayer;
import model.Player;

// A classe fornece um componente de interface do usuário para controlar as opções do jogo de damas que está sendo jogado na janela.
public class OptionPanel extends JPanel {

	private static final long serialVersionUID = -4763875452164030755L;

	// A janela do verificador para atualizar quando uma opção é alterada.
	private CheckersWindow window;
	
	// O botão que ao ser clicado, reinicia o jogo.
	private JButton restartBtn;
	
	// A caixa de combinação que altera o tipo de jogador que é o jogador 1.
	private JComboBox<String> player1Opts;
	
	// O botão para executar uma ação com base no tipo de jogador.
	private JButton player1Btn;

	// A caixa de combinação que altera o tipo de jogador jogador 2.
	private JComboBox<String> player2Opts;
	
	// O botão para executar uma ação com base no tipo de jogador.
	private JButton player2Btn;
	
	// Cria um novo painel de opções para a janela de damas especificada.
	// window: a janela com o jogo de damas para atualizar.
	public OptionPanel(CheckersWindow window) {
		super(new GridLayout(0, 1));
		
		this.window = window;
		
		// Inicializar os componentes
		OptionListener ol = new OptionListener();
		final String[] playerTypeOpts = {"Humano", "Computador"};
		this.restartBtn = new JButton("Recomeçar");
		this.player1Opts = new JComboBox<>(playerTypeOpts);
		this.player2Opts = new JComboBox<>(playerTypeOpts);
		this.restartBtn.addActionListener(ol);
		this.player1Opts.addActionListener(ol);
		this.player2Opts.addActionListener(ol);
		JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel middle = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		// Adicionar componentes ao layout
		top.add(restartBtn);
		middle.add(new JLabel("(Peças Pretas) Player 1: "));
		middle.add(player1Opts);
		bottom.add(new JLabel("(Peças Brancas) Player 2: "));
		bottom.add(player2Opts);
		this.add(top);
		this.add(middle);
		this.add(bottom);
	}

	public CheckersWindow getWindow() {
		return window;
	}

	public void setWindow(CheckersWindow window) {
		this.window = window;
	}
	
	// Obtém uma nova instância do tipo de jogador selecionado para a caixa de combinação especificada.
	// playerOpts: a caixa de combinação com as opções do player.
	// uma nova instância de objeto que corresponde ao tipo de jogador selecionado.
	private static Player getPlayer(JComboBox<String> playerOpts) {
		
		Player player = new HumanPlayer();
		if (playerOpts == null) {
			return player;
		}
		
		// Determine o tipo
		String type = "" + playerOpts.getSelectedItem();
		if (type.equals("Computador")) {
			player = new ComputerPlayer();
		}
		
		return player;
	}
	
	// A classe responde aos componentes dentro do painel de opções quando eles são clicados/atualizados.
	private class OptionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// Nenhuma janela para atualizar
			if (window == null) {
				return;
			}
			
			Object src = e.getSource();

			// Lidar com a ação do usuário
			JButton btn = null;
			if (src == restartBtn) {
				window.restart();
				window.getBoard();
			} else if (src == player1Opts) {
				Player player = getPlayer(player1Opts);
				window.setPlayer1(player);
				btn = player1Btn;
			} else if (src == player2Opts) {
				Player player = getPlayer(player2Opts);
				window.setPlayer2(player);
				btn = player2Btn;
			}
			// Atualizar IU
			if (btn != null) {
				btn.repaint();
			}
		}
	}
}
