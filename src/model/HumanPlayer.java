// Descrição: Esta classe representa um jogador humano (ou seja, um usuário) que pode interagir com o sistema.

package model;

// A classe representa um usuário do jogo de damas que pode atualizar o jogo clicando nas peças do tabuleiro.
public class HumanPlayer extends Player {

	@Override
	public boolean isHuman() {
		return true;
	}
// Não executa nenhuma atualização no jogo. Como jogadores humanos podem interagir com a interface do usuário para atualizar o jogo.

	@Override
	public void updateGame(Game game) {}

}
