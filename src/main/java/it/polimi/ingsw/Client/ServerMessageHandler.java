package it.polimi.ingsw.Client;

import it.polimi.ingsw.Client.cli.CLI;
import it.polimi.ingsw.Client.gui.GUI;
import it.polimi.ingsw.Client.messages.turn.GameStartedMessage;
import it.polimi.ingsw.Constants.GameState;
import it.polimi.ingsw.Server.Answer.*;
import it.polimi.ingsw.Server.Answer.modelUpdate.*;

import java.beans.PropertyChangeSupport;

/**
 * ServerMessageHandler class handles the answers from the server notifying the correct part of the GUI or
 * CLI through property change listeners.
 *
 * @author Federico Sarrocco
 */
public class ServerMessageHandler {

  public static final String GAME_SETUP_LISTENER = "gameSetup";
  public static final String GAME_ERROR_LISTENER = "gameError";
  public static final String REQ_PLAYERS_LISTENER = "reqPlayers";
  public static final String CUSTOM_MESSAGE_LISTER = "customMessage";
  public static final String WIN_MESSAGE_LISTER = "winMessage";
  public static final String NEXT_ROUNDOWNER_LISTENER = "roundOwner";
  public static final String GENERERIC_MODEL_UPDATE_LISTENER = "genericModelUpdate";
  public static final String GAME_STATE_LISTENER = "stateChange";
  public static final String PLAYED_CARD_LISTENER = "playedCard";
  public static final String CONNECTED_PLAYERS_LISTENER = "connectedPlayers";

  private final ModelView modelView;
  private final PropertyChangeSupport view = new PropertyChangeSupport(this);
  private CLI cli;
  private GUI gui;

  /**
   * Constructor of the ServerMessageHandler in case players are using the CLI.
   *
   * @param cli of type CLI - the command line interface reference.
   * @param modelView of type ModelView - the structure, stored into the client, containing simple
   *     logic of the model.
   */
  public ServerMessageHandler(CLI cli, ModelView modelView) {
    this.cli = cli;
    view.addPropertyChangeListener(cli);
    this.modelView = modelView;
  }

  /**
   * Constructor of the ServerMessageHandler in case players are using the GUI.
   *
   * @param gui of type GUI - the graphical user interface reference.
   * @param modelView of type ModelView - the structure, stored into the client, containing simple
   *     logic of the model.
   */
  public ServerMessageHandler(GUI gui, ModelView modelView) {
    this.gui = gui;
    view.addPropertyChangeListener(gui);
    this.modelView = modelView;
  }

  /**
   * Method answerHandler handles the answer received from the server. It calls the client interface
   * passing values relying on the type of answer the server has sent.
   */
  public void answerHandler() {
    Answer answer = modelView.getServerAnswer();
    if (answer instanceof ConnectionMessage connectionMessage) {
      System.out.println(connectionMessage.getMessage());
    }
    else if (answer instanceof ReqPlayersMessage){
      view.firePropertyChange(REQ_PLAYERS_LISTENER,null, answer);
    }
    else if(answer instanceof ModelMessage){
      handleGameMessage((ModelMessage) answer);
      view.firePropertyChange(GENERERIC_MODEL_UPDATE_LISTENER,null, answer);
    }
    else if(answer instanceof GameError){
      view.firePropertyChange(GAME_ERROR_LISTENER,null, answer);
    }
    else if(answer instanceof CustomMessage){
      view.firePropertyChange(CUSTOM_MESSAGE_LISTER,null, answer);
    }
    else if(answer instanceof WinMessage){
      view.firePropertyChange(WIN_MESSAGE_LISTER,null, answer);
    }

  }

  void handleGameMessage(ModelMessage answer){
    if(answer instanceof BalanceMessage){
      modelView.setBalance(((BalanceMessage) answer).getMessage());
    }
    else if(answer instanceof CloudsMessage){
      modelView.setClouds(((CloudsMessage)answer).getMessage());
    }
    else if(answer instanceof HandMessage){
      modelView.setHand(((HandMessage)answer).getMessage());
    }
    else if(answer instanceof IslandsMessage){
      modelView.setIslandContainer(((IslandsMessage)answer).getMessage());
    }
    else if(answer instanceof MotherMessage){
      modelView.setMotherNature(((MotherMessage)answer).getMessage());
    }
    else if(answer instanceof PlayedCardMessage message){
      modelView.setPlayedCard(message.getPlayer(), message.getMessage());
      view.firePropertyChange(PLAYED_CARD_LISTENER, null, message); // todo remove previousstate to trigger (also in game model)
    }
    else if(answer instanceof ProfsMessage){
      modelView.setProfessors(((ProfsMessage)answer).getMessage());
    }
    else if(answer instanceof SchoolMessage message){
      modelView.setPlayerSchool(message.getPlayer(), message.getMessage());
    }
    else if(answer instanceof RoundOwnerMessage message){
      String previousOwner = modelView.getRoundOwner();
      modelView.setRoundOwner(message.getMessage());
      view.firePropertyChange(NEXT_ROUNDOWNER_LISTENER, previousOwner , message.getMessage());
    }
    else if(answer instanceof MagicianMessage message){
      modelView.setAvailableMagicians(message.getMessage());
    }
    else if(answer instanceof GameStateMessage message){
      GameState previousState = modelView.getGameState();
      modelView.setGameState(message.getMessage());
      view.firePropertyChange(GAME_STATE_LISTENER, null, message.getMessage()); // todo remove previousstate to trigger (also in game model)
    }
    else if(answer instanceof ModeMessage message){
      modelView.setExpert(message.getMessage());
    }
    else if(answer instanceof CharactersMessage message){
      modelView.setCharacterCards(message.getMessage());
    }
    else if(answer instanceof ConnectedPlayersMessage message) {
      modelView.setConnectedPlayers(message.getMessage());
      view.firePropertyChange(CONNECTED_PLAYERS_LISTENER, null, message.getMessage());
    }
  }

  public void setupMessageHandler(Answer answer){
    if(answer instanceof ReqPlayersMessage){
      view.firePropertyChange(GAME_SETUP_LISTENER, null, "ReqPlayers");  // ((ReqPlayersMessage)answer).getClass().getSimpleName() TODO this should also work
    } else if (answer instanceof ReqMagicianMessage) {
      view.firePropertyChange(GAME_SETUP_LISTENER, null, "ReqMagician");
    }
    else if (answer instanceof GameStartedMessage) {
      view.firePropertyChange("gameStarted", null, null);
    }

  }


}