package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Controller.Rules.Rules;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Enumerations.Color;
import it.polimi.ingsw.Model.Enumerations.GameState;
import it.polimi.ingsw.Model.Enumerations.TowerColor;
import it.polimi.ingsw.Model.Islands.BaseIsland;
import it.polimi.ingsw.Model.Islands.Island;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameManager {

    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 3;
    private final Game gameInstance;
    private final Rules rules;
    //private RoundManager roundManager;

    public GameManager() {

        this.gameInstance = new Game(new Bag(Rules.initialBagSize));
        this.gameInstance.setGameState(GameState.GAME_ROOM);
        rules = new Rules();

        //this.roundManager = new RoundManager(this);
    }


    public Game getGameInstance() {
        return gameInstance;
    }

    public Rules getRules() {
        return rules;
    }

    public void addPlayer(Player player) {
        this.gameInstance.addPlayer(player);
    }

    public void initGame() {
        // init Model
        // TODO canGameBeInitialized() ? ok : throwExce (ad esempio se non ci sono abbastanza giocatori )
        initMotherNature();
        initIslands();
        fillBag();
        initSchools();
        initClouds();
        // TODO initCharacters this can be made in anyway
    }


    private void initSchools() {
        List<Player> players = gameInstance.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            // create and fill the school
            Map<Color, Integer> students = gameInstance.getBag().extract(Rules.getEntrySize(players.size()));
            TowerColor towerColor = TowerColor.values()[i];
            School school = new School(Rules.getTowersPerPlayer(players.size()), towerColor, students);
            player.setSchool(school);
        }
    }

    private void initIslands() {
        int motherNaturePosition = gameInstance.getMotherNature().getPosition();
        int opposite = (motherNaturePosition + 6) % 12;
        LinkedList<Island> islands = new LinkedList<>();
        MotherNature motherNature;

        for (int i = 0; i < Rules.maxIslands; i++) {
            Island island = new BaseIsland();
            if (i != opposite && i != motherNaturePosition) {
                island.addStudent(gameInstance.getBag().extractOne());
            }
            islands.add(island);
        }
        gameInstance.initIslands(islands);
    }

    private void initMotherNature() {
        Random rand = new Random();
        int motherNaturePosition = rand.nextInt(1, Rules.maxIslands);
        MotherNature motherNature = new MotherNature(motherNaturePosition);
        gameInstance.initMotherNature(motherNature);
    }

    private void fillBag() {
        gameInstance.getBag().extendBag(Rules.bagSize - Rules.initialBagSize);
    }

    private void initClouds() {
        int numClouds = gameInstance.getPlayers().size();
        LinkedList<Cloud> clouds = new LinkedList<>();

        for (int i = 0; i < numClouds; i++) {
            Cloud cloud = new Cloud();
            cloud.addStudents(gameInstance.getBag().extract(Rules.getStudentsPerTurn(gameInstance.numPlayers()))); //init fill
            clouds.add(cloud);
        }
        gameInstance.initClouds(clouds);

    }

}

