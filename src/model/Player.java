// Descrição: Esta classe representa um player do sistema.

package model;

// A classe é uma classe abstrata que representa um jogador em um jogo de damas.
public abstract class Player {

	// Determina como o jogo é atualizado. Se verdadeiro, o usuário deve interagir com a interface do usuário para fazer um movimento. Caso contrário, o jogo é atualizado via
	// retorna verdadeiro se este jogador representa um usuário.
	public abstract boolean isHuman();
	
	// Atualiza o estado do jogo para fazer uma jogada para o jogador atual. Se houver um movimento disponível com vários saltos, ele pode ser executado de uma vez por este método ou um salto de cada vez.
	// game: o jogo para atualizar.
	public abstract void updateGame(Game game);
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[isHuman=" + isHuman() + "]";
	}
}
