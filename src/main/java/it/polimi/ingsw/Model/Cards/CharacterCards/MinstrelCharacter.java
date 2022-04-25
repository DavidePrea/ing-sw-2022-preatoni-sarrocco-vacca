package it.polimi.ingsw.Model.Cards.CharacterCards;

import it.polimi.ingsw.Controller.Rules.Rules;
import it.polimi.ingsw.Model.Bag;
import it.polimi.ingsw.Model.Enumerations.GameState;
import it.polimi.ingsw.Model.Game;

public class MinstrelCharacter extends  CharacterCard{


    private GameState previousState;
    private Bag bag;
    private int swappedStudents;
    public static final int maxSwaps = 2;

    public MinstrelCharacter(String imagePath) {
        super(imagePath);
        price = 1;
        isActive = false;
    }

    @Override
    public void activate(Rules rules, Game game) {
        super.activate(rules, game);
        game.setGameState(GameState.MINISTREL_SWAP_STUDENTS);
    }

    @Override
    public void deactivate(Rules rules, Game game) {
        super.deactivate(rules, game);
        game.setGameState(previousState);
    }
    public int getSwappedStudents() {
        return swappedStudents;
    }


}