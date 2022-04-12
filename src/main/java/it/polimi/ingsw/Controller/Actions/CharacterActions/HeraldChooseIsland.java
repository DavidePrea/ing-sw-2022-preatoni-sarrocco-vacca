package it.polimi.ingsw.Controller.Actions.CharacterActions;

import it.polimi.ingsw.Controller.Actions.Performable;
import it.polimi.ingsw.Controller.Rules.Rules;
import it.polimi.ingsw.Model.Enumerations.GameState;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Model.Islands.Island;
import it.polimi.ingsw.Model.Islands.IslandContainer;
import it.polimi.ingsw.Model.Player;

import java.util.Optional;

public class HeraldChooseIsland implements Performable {
    private final String myNickName;
    private int islandIndex;

    HeraldChooseIsland(String player, int islandIndex) {
        this.myNickName = player;
        this.islandIndex = islandIndex;
    }

    @Override
    public boolean canPerformExt(Game game) {
        Optional<Player> player_opt = game.getPlayerByNickname(myNickName);
        if (player_opt.isEmpty())    // if there is no Player with that nick
            return false;
        Player player = player_opt.get();

        if (!game.getRoundOwner().equals(player)) {   // if the player is not the round owner
            return false;
        }

        if (!game.getGameState().equals(GameState.HERALD_ACTIVE)) {
            return false;
        }

        // is action legal check
        if(!game.getIslandContainer().isFeasibleIndex(islandIndex)){
            return false;
        }

        return true;
    }

    @Override
    public void performMove(Game game, Rules rules) {
        Optional<Player> player_opt = game.getPlayerByNickname(myNickName);
        if (player_opt.isEmpty())    // if there is no Player with that nick
            return;
        Player player = player_opt.get();

        Island island = game.getIslandContainer().get(islandIndex);

        // set owner ( put the Tower )
        Optional<String> islandNewOwner_opt = rules.getDynamicRules().computeIslandInfluence(game, islandIndex);
        if (islandNewOwner_opt.isPresent()) {
            String islandPrevOwner = island.getOwner();
            if (!islandNewOwner_opt.get().equals(islandPrevOwner)) {
                island.setOwner(islandNewOwner_opt.get());
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
        Island prevIsland = islandContainer.prevIsland(islandIndex);
        if(Island.checkJoin(prevIsland,island)) {
            islandContainer.joinPrevIsland(islandIndex);
            game.moveMotherNature(-1);
        }
        Island nextIsland = islandContainer.nextIsland(islandIndex);
        if(Island.checkJoin(island,nextIsland)) {
            islandContainer.joinNextIsland(islandIndex);
        }
    }

    @Override
    public String getNickNamePlayer() {
        return myNickName;
    }
}