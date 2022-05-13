package it.polimi.ingsw.Controller.Actions;

import it.polimi.ingsw.Controller.Rules.Rules;
import it.polimi.ingsw.Constants.Color;
import it.polimi.ingsw.Constants.GameState;
import it.polimi.ingsw.Exceptions.GameException;
import it.polimi.ingsw.Exceptions.InvalidPlayerException;
import it.polimi.ingsw.Exceptions.RoundOwnerException;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Model.Player;

public class MoveStudentFromEntryToHall extends MoveStudentFromEntry {

    MoveStudentFromEntryToHall(String player, Color color) {
        super(player, color);
    }

    @Override
    public void performMove(Game game, Rules rules) throws InvalidPlayerException, RoundOwnerException, GameException {
        canPerform(game, rules);
        Player player = getPlayer(game);

        player.getSchool().moveStudentFromEntryToHall(color);   // model modification

        // coin
        int hallPosition = player.getSchool().getStudentsHall().getOrDefault(color, 0);
        if (Rules.checkCoin(hallPosition)) {
            game.incrementPlayerBalance(player.getNickname());
        }
        //compute the new professors
        game.setProfessors(rules.getDynamicRules().getProfessorInfluence(game));

        if (Rules.getEntrySize(game.numPlayers()) - player.getSchool().getEntryStudentsNum() >= Rules.getStudentsPerTurn(game.numPlayers())) {
            game.setGameState(GameState.ACTION_MOVE_MOTHER);
        }
    }
}
