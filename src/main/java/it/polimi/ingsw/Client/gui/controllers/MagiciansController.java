package it.polimi.ingsw.Client.gui.controllers;

import it.polimi.ingsw.Client.gui.GUI;
import it.polimi.ingsw.Constants.Magician;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import javax.net.ssl.HostnameVerifier;
import java.net.URL;
import java.util.*;
import java.util.List;

public class MagiciansController extends GUIController {

    GUI gui;
    private final HashMap<String, Image> magiciansImage = new HashMap<>();
    private final ArrayList<Pane> magiciansPane = new ArrayList<>();

    @FXML
    Label description; //wizard, king, witch and sage label

    @FXML
    Pane Wizard, King, Witch, Sage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        magiciansImage.put(Magician.KING.name(), new Image(getClass().getResourceAsStream("/graphics/magicians/king.png")));
        magiciansImage.put(Magician.WIZARD.name(), new Image(getClass().getResourceAsStream("/graphics/magicians/wizard.png")));
        magiciansImage.put(Magician.WITCH.name(), new Image(getClass().getResourceAsStream("/graphics/magicians/witch.png")));
        magiciansImage.put(Magician.SAGE.name(), new Image(getClass().getResourceAsStream("/graphics/magicians/sage.png")));

        magiciansPane.addAll(List.of(King, Wizard, Witch, Sage));
        description.setFont(font);

    }

    /**
     * this function must be called after the controller has been initialized
     */
    public void init(){
        showMagicians();
    }

    @Override
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    @FXML
    public void selectedMagician(MouseEvent mouseEvent) {

      //  HashMap ownMag = new HashMap();
      //  Magician mag = null;
        ImageView selection = (ImageView) mouseEvent.getSource();
        String magician = selection.getId();

//        switch (magician.toLowerCase()){
//            case "king" -> mag= Magician.KING;
//            case "wizard" -> mag= Magician.WIZARD;
//            case "sage" -> mag= Magician.SAGE;
//            case "witch" -> mag= Magician.WITCH;
//        }
//
//        ownMag.put(gui.getModelView().getPlayerName(),mag);
//        gui.getModelView().setPlayerMapMagician(ownMag);

        // send setup option
        String message = "MAGICIAN " + magician;

        Platform.runLater(() -> {
            gui.getListeners().firePropertyChange("action", null, message);
        });
        gui.changeScene("loading.fxml");
    }

    public void showMagicians() {

        List<String> mgcns = gui.getModelView().getAvailableMagiciansStr(); //TODO check the bind from Model and scene builder
        int i = 0;
        for (String mag: mgcns) {

            ImageView view = new ImageView(magiciansImage.get(mag));
            view.setFitHeight(404);
            view.setFitWidth(350);

            view.setOnMouseClicked(this::selectedMagician);
            view.setOnMouseEntered(this::showDescription);
            view.setOnMouseExited(this::showDescription);
            view.setId(mag);
            magiciansPane.get(i).getChildren().clear();
            magiciansPane.get(i).getChildren().add(view);
            i++;
        }
    }

    @FXML
    public void showDescription(MouseEvent mouseEvent) {
        ImageView selection = (ImageView) mouseEvent.getSource();
        String id = selection.getId();

        if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
            switch (id.toLowerCase()) {
                case "king" -> {
                    selection.setStyle("-fx-opacity: 0.4");
                    description.setText("The king of all kings!");
                }
                case "witch" -> {
                    selection.setStyle("-fx-opacity: 0.5");
                    description.setText("The most powerful witch in the world!");
                }
                case "sage" -> {
                    selection.setStyle("-fx-opacity: 0.4");
                    description.setText("The wise old formidable strategist!");
                }
                case "wizard" -> {
                    selection.setStyle("-fx-opacity: 0.4");
                    description.setText("The grand master of sorcerers!");
                }
            }
        } else {
            selection.setStyle("-fx-opacity: 1");
            description.setText("");
        }
    }
}





