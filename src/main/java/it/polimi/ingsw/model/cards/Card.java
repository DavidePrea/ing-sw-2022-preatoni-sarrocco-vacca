package it.polimi.ingsw.model.cards;

import java.io.Serializable;

public abstract class Card implements Serializable {

    private final String imagePath;

    public Card(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

}