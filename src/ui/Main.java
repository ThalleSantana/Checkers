// Descrição: Esta classe contém o método principal para criar a GUI e iniciar o jogo de damas.

package ui;

import javax.swing.UIManager;

public class Main {

	public static void main(String[] args) {
		
		// Defina a aparência para a aparência do sistema operacional
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Crie uma janela para exibir o jogo de damas
		CheckersWindow window = new CheckersWindow();
		window.setDefaultCloseOperation(CheckersWindow.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}
