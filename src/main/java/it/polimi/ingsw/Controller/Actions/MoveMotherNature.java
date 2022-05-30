package it.polimi.ingsw.Controller.Actions;

import it.polimi.ingsw.Controller.Rules.Rules;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Cards.CharacterCards.CharacterCard;
import it.polimi.ingsw.Model.Cards.CharacterCards.GrandmaCharacter;
import it.polimi.ingsw.Constants.GameState;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Model.Islands.Island;
import it.polimi.ingsw.Model.Islands.IslandContainer;
import it.polimi.ingsw.Model.Player;

import java.util.Optional;

public class MoveMotherNature extends Performable {
    private final int movement;

    public MoveMotherNature(String player, int movement) {
        super(player);
        this.movement = movement;
    }

    @Override
    protected void canPerform(Game game, Rules rules) throws InvalidPlayerException, RoundOwnerException, GameException {
        // Simple check that verifies that there is a player with the specified name, and that he/she is the roundOwner
        super.canPerform(game, rules);

        Player player = getPlayer(game);

        if (!game.getGameState().equals(GameState.ACTION_MOVE_MOTHER)) {
            throw new WrongStateException("action phase, when you move mother nature.");
        }

        // is action legal check
        int playerCardMaxMoves = rules.getDynamicRules().computeMotherMaxMoves(player.getPlayedCard());
        if (movement < 1 || movement > playerCardMaxMoves) {
            throw new InvalidIndexException("mother nature movement", 1, playerCardMaxMoves, movement);
        }
    }

    @Override
    public void performMove(Game game, Rules rules) throws InvalidPlayerException, RoundOwnerException, GameException {
        canPerform(game, rules);
        game.moveMotherNature(movement);
        int newMotherPosition = game.getMotherNature().getPosition();
        Island island = game.getIslandContainer().get(newMotherPosition);
        if (!island.isBlocked()) {
            // set owner ( put the Tower )
            Optional<String> islandNewOwner_opt = rules.getDynamicRules().computeIslandInfluence(game, island);
            if (islandNewOwner_opt.isPresent()) {
                String islandPrevOwner = island.getOwner();
                if (!islandNewOwner_opt.get().equals(islandPrevOwner)) {
                    game.setIslandOwner(newMotherPosition, islandNewOwner_opt.get());
                    // remove tower to the player
                    Optional<Player> islandOwnerPlayer_opt = game.getPlayerByNickname(islandNewOwner_opt.get());
                    islandOwnerPlayer_opt.ifPresent(owner -> owner.getSchool().decreaseTowers());
                    // give back the tower to the previous owner
                    Optional<Player> islandPrevPlayer_opt = game.getPlayerByNickname(islandPrevOwner);
                    islandPrevPlayer_opt.ifPresent(owner -> owner.getSchool().increaseTowers());
                }
            }
            // SuperIsland creation
            IslandContainer islandContainer = game.getIslandContainer();
            Island prevIsland = islandContainer.prevIsland(newMotherPosition);
            if (Island.checkJoin(prevIsland, game.getIslandContainer().get(newMotherPosition))) {
                game.joinPrevIsland(newMotherPosition);
                game.moveMotherNature(-1);
            }

            // SuperIsland creation
            newMotherPosition = game.getMotherNature().getPosition();
            Island nextIsland = islandContainer.nextIsland(newMotherPosition);
            if (Island.checkJoin(game.getIslandContainer().get(newMotherPosition), nextIsland)) {
                game.joinNextIsland(newMotherPosition);
                if(newMotherPosition == islandContainer.size()){
                    game.moveMotherNature(-1);
                }
            }
        } else {
            game.setIslandBlock(newMotherPosition,false);
            Optional<CharacterCard> card = game.getCharacterCards().stream().filter(characterCard -> characterCard instanceof GrandmaCharacter).findFirst();
            if (card.isEmpty()) {
                return;
            }
            GrandmaCharacter grandma = (GrandmaCharacter) card.get();
            grandma.addBlockingCard();
        }
    }

    @Override
    public GameState nextState(Game game, Rules rules){
        return GameState.ACTION_CHOOSE_CLOUD;
    }
}
