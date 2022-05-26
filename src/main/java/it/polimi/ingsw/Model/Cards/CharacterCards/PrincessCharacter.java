package it.polimi.ingsw.Model.Cards.CharacterCards;

import it.polimi.ingsw.Controller.Rules.Rules;
import it.polimi.ingsw.Model.Bag;
import it.polimi.ingsw.Constants.Color;
import it.polimi.ingsw.Constants.GameState;
import it.polimi.ingsw.Model.Game;

import java.util.EnumMap;
import java.util.Map;

public class PrincessCharacter extends CharacterCard {

    private Map<Color, Integer> students;
    private GameState previousState;
    private Bag bag;


    public PrincessCharacter(String imagePath, Bag bag) {
        super(imagePath);
        price = 2;
        this.bag = bag;
        students = new EnumMap<Color, Integer>(Color.class);
    }

    @Override
    public void init() {
        for (int i = 0; i < 4; i++) {
            Color student = bag.extractOne();
            this.students.put(student, this.students.getOrDefault(student, 0) + 1);
        }
    }

    @Override
    public void activate(Rules rules, Game game) {
        super.activate(rules, game);
        previousState = game.getGameState();
        game.setGameState(GameState.PRINCESS_MOVE_STUDENT);

    }

    @Override
    public void deactivate(Rules rules, Game game) {
        super.deactivate(rules, game);
        game.setGameState(previousState);
    }

    public void moveStudent(Color student) {
        if (students.get(student) != null) {
            students.put(student, students.get(student) - 1);
            Color refill = bag.extractOne();
            students.put(refill, this.students.getOrDefault(refill, 0) + 1);
        }
    }

    public Map<Color, Integer> getStudents() {
        return students;
    }


}
