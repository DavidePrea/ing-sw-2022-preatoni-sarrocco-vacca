package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Constants.Constants;
import it.polimi.ingsw.Controller.Actions.Performable;
import it.polimi.ingsw.Controller.Rules.Rules;
import it.polimi.ingsw.Exceptions.GameException;
import it.polimi.ingsw.Exceptions.InvalidPlayerException;
import it.polimi.ingsw.Exceptions.RoundOwnerException;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Cards.CharacterCards.*;
import it.polimi.ingsw.Constants.Color;
import it.polimi.ingsw.Constants.GameState;
import it.polimi.ingsw.Constants.TowerColor;
import it.polimi.ingsw.Model.Islands.BaseIsland;
import it.polimi.ingsw.Model.Islands.Island;
import it.polimi.ingsw.Server.GameHandler;
import it.polimi.ingsw.Server.VirtualClient;
import it.polimi.ingsw.listeners.NextRoundListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

public class GameManager implements PropertyChangeListener {

    public static final String ROUND_CONTROLLER = "roundController";
    private final Game game;
    private final Rules rules;
    private final GameHandler gameHandler;
    private RoundManager roundManager;
    private boolean isHard_temp = false;
    private final PropertyChangeSupport controllerListeners = new PropertyChangeSupport(this);


    public GameManager(Game game, GameHandler gameHandler) {

        this.game = game;   //new Game(new Bag(Constants.INITIAL_BAG_SIZE))
        rules = new Rules();
        this.roundManager = new RoundManager(this);
        this.roundManager.addListener(this);
        this.gameHandler = gameHandler;
    }

//    public void createListener(VirtualClient client){
//        controllerListeners.addPropertyChangeListener(new NextRoundListener(client));
//    }

    public Game getGame() {
        return game;
    }

    protected GameHandler getGameHandler() {
        return gameHandler;
    }

    public Rules getRules() {
        return rules;
    }

    public void addPlayer(Player player) {
        this.game.addPlayer(player);
    }

    public void initGame() {
        initMotherNature();
        initIslands();
        fillBag();
        initSchools();
        initClouds();
        initRoundManager();
        if (game.isExpertMode()) {
            initCharacters();
            game.initBalance(Constants.NUM_COINS);
            initPlayersBalance();
        }
        game.fireInitalState();
    }

    private void initCharacters() {
        List<CharacterCard> characters = new ArrayList<>();
        characters.add(new CentaurCharacter(""));
        characters.add(new FarmerCharacter(""));
        characters.add(new HeraldCharacter(""));
        characters.add(new JokerCharacter("", game.getBag()));
        characters.add(new KnightCharacter(""));
        characters.add(new MushroomCharacter(""));
        characters.add(new PostmanCharacter(""));
        characters.add(new ThiefCharacter(""));
        characters.add(new PrincessCharacter("", game.getBag()));
        characters.add(new MinstrelCharacter(""));
        characters.add(new MonkCharacter("", game.getBag()));
        characters.add(new GrandmaCharacter(""));

        Collections.shuffle(characters);
        List<CharacterCard> extractedCharacters = characters.subList(0, 3);
        extractedCharacters.forEach(CharacterCard::init);
        ArrayList<CharacterCard> gameCharacters = new ArrayList<>(characters.subList(0, 3));
        game.initCharacterCards(gameCharacters);
    }


    private void initSchools() {
        List<Player> players = game.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            // create and fill the school
            Map<Color, Integer> students = game.getBag().extract(Rules.getEntrySize(players.size()));
            TowerColor towerColor = TowerColor.values()[i];
            School school = new School(Rules.getTowersPerPlayer(players.size()), towerColor, students);
            player.setSchool(school);
        }
    }
    private void initRoundManager(){
        game.setPlayersActionPhase(game.getPlayers());
        game.setRoundOwner(game.getPlayers().get(0));
        game.setGameState(GameState.SETUP_CHOOSE_MAGICIAN);
    }

    private void initIslands() {
        int motherNaturePosition = game.getMotherNature().getPosition();
        int opposite = (motherNaturePosition + 6) % 12;
        LinkedList<Island> islands = new LinkedList<>();
        MotherNature motherNature;

        for (int i = 0; i < Constants.MAX_ISLANDS; i++) {
            Island island = new BaseIsland();
            if (i != opposite && i != motherNaturePosition) {
                island.addStudent(game.getBag().extractOne());
            }
            islands.add(island);
        }
        game.initIslands(islands);
    }

    private void initMotherNature() {
        Random rand = new Random();
        int motherNaturePosition = rand.nextInt(1, Constants.MAX_ISLANDS);
        MotherNature motherNature = new MotherNature(motherNaturePosition);
        game.initMotherNature(motherNature);
    }

    private void initPlayersBalance() {
        for (Player p : game.getPlayers()) {
            for(int i = 0; i < Constants.INITIAL_PLAYER_BALANCE; i++){
                game.incrementPlayerBalance(p.getNickname());
            }
        }
    }

    private void fillBag() {
        game.getBag().extendBag(Constants.BAG_SIZE - Constants.INITIAL_BAG_SIZE);
    }

    private void initClouds() {
        int numClouds = game.getPlayers().size();
        LinkedList<Cloud> clouds = new LinkedList<>();
        for (int i = 0; i < numClouds; i++) {
            Cloud cloud = new Cloud();
            clouds.add(cloud);
        }
        game.initClouds(clouds);
        game.refillClouds();

    }

    public void performAction(Performable action) throws InvalidPlayerException, RoundOwnerException, GameException {
        roundManager.performAction(action);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        controllerListeners.firePropertyChange(evt);
    }

    public void handleNewRoundOwnerOnDisconnect(String nickname) {
        roundManager.handleNewRoundOwnerOnDisconnect(nickname);
    }
}

