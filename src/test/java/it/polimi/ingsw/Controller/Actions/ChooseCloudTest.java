package it.polimi.ingsw.Controller.Actions;

import it.polimi.ingsw.Controller.GameManager;
import it.polimi.ingsw.Controller.Rules.Rules;
import it.polimi.ingsw.Model.Enumerations.Color;
import it.polimi.ingsw.Model.Enumerations.GameState;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Model.Player;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChooseCloudTest {

    @Test
    void canPerformExt() {

        GameManager gameManager = new GameManager();
        Player p1 = new Player("Palkia");
        Player p2 = new Player("Kyogre");

        gameManager.addPlayer(p1);
        gameManager.addPlayer(p2);
        gameManager.initGame();
        Game gameInstance = gameManager.getGameInstance();
        gameInstance.setRoundOwner(p2);


        int choice = 1; // get right choice
        int choice2 = 6;


        Performable choiceCloudRIGHT = new ChooseCloud("Kyogre", choice);
        Performable choiceCloudWRONG = new ChooseCloud("Scaccia", choice);


        // base case
        gameInstance.setGameState(GameState.ACTION_CHOOSE_CLOUD);
        assertTrue(choiceCloudRIGHT.canPerformExt(gameInstance, gameManager.getRules()));

        //wrong game phase
        gameInstance.setGameState(GameState.PLANNING_CHOOSE_CARD);
        assertFalse(choiceCloudWRONG.canPerformExt(gameInstance, gameManager.getRules()));

        // wrong player ( no player with that nickname )
        gameInstance.setGameState(GameState.ACTION_CHOOSE_CLOUD);
        assertFalse(choiceCloudWRONG.canPerformExt(gameInstance, gameManager.getRules()));

        // wrong player is not your turn
        gameInstance.setRoundOwner(p1);
        assertFalse(choiceCloudRIGHT.canPerformExt(gameInstance, gameManager.getRules()));
        gameInstance.setRoundOwner(p2);


        // The choice doesn't exist
        Performable action = new ChooseCloud("Kyogre",choice2);
        gameInstance.setGameState(GameState.ACTION_CHOOSE_CLOUD);
        assertFalse(action.canPerformExt(gameInstance, gameManager.getRules()));

        // all Clouds are empty

        Performable action2 = new ChooseCloud("Kyogre",choice);
        gameInstance.getClouds().get(choice).pickStudents();
        assertFalse(action2.canPerformExt(gameInstance, gameManager.getRules()));

    }

    @Test
    void performMove() {

        GameManager gameManager = new GameManager();
        Player p1 = new Player("Palkia");
        Player p2 = new Player("Kyogre");

        gameManager.addPlayer(p1);
        gameManager.addPlayer(p2);
        gameManager.initGame();
        Game gameInstance = gameManager.getGameInstance();
        gameInstance.setGameState(GameState.ACTION_CHOOSE_CLOUD);
        gameInstance.setRoundOwner(p2);

        Random random = new Random();
        //random choice between 1 - maxClouds
        int choice = random.nextInt(gameInstance.numPlayers());

        Performable ChooseClouds = new ChooseCloud("Kyogre", choice);
        assertTrue(ChooseClouds.canPerformExt(gameInstance, gameManager.getRules()));

        // previous state
        int weight = Rules.getStudentsPerTurn(gameInstance.numPlayers());
        Map<Color, Integer> prevEntry = new EnumMap<Color, Integer>(p2.getSchool().getStudentsEntry()); //empty?
        //perform move
        
        ChooseClouds.performMove(gameInstance, gameManager.getRules());

        Map<Color, Integer> postEntry = new EnumMap<Color, Integer>(p2.getSchool().getStudentsEntry());

        int count = 0;
        for(Color c: Color.values()) {
            if (prevEntry.get(c) != null) {
                count = count + prevEntry.get(c);
            }
        }

        int postCounter = 0;
        for(Color c: Color.values()) {
            if (postEntry.get(c) != null) {
                postCounter = postCounter + postEntry.get(c);
            }
        }

        assertTrue(count + weight == postCounter);

    }


}