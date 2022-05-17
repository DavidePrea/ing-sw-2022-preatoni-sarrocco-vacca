package it.polimi.ingsw.Controller.Actions;

import it.polimi.ingsw.Constants.GameState;
import it.polimi.ingsw.Controller.Rules.Rules;
import it.polimi.ingsw.Exceptions.GameException;
import it.polimi.ingsw.Exceptions.InvalidPlayerException;
import it.polimi.ingsw.Exceptions.RoundOwnerException;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Model.Player;

import java.util.stream.IntStream;

/**
 * It is used when a player decide not to go on with the effect of the active character Card (per i fino a)
 */
public class DeactivateCard extends Performable {

    public DeactivateCard(String player) {
        super(player);
    }

    @Override
    protected void canPerform(Game game, Rules rules) throws InvalidPlayerException, RoundOwnerException, GameException {
        // Simple check that verifies that there is a player with the specified name, and that he/she is the roundOwner
        super.canPerform(game, rules);
        if (!game.getActiveCharacter().isPresent()){
            throw new GameException("You must have activated a card in order to use this action.");
        }
    }

    @Override
    public void performMove(Game game, Rules rules) throws InvalidPlayerException, RoundOwnerException, GameException {
        canPerform(game, rules);
        // TODO DEACTIVATE A SINGLE CARD
        IntStream.range(0, game.getCharacterCards().size()).filter(i -> game.getCharacterCards().get(i).isActive()).forEach(i->game.deactivateCharacterCard(i,rules));
    }


}
