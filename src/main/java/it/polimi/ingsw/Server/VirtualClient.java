package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.Answer.Answer;
import it.polimi.ingsw.Server.Answer.PingMessage;
import it.polimi.ingsw.Server.Answer.SerializedAnswer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * Virtual client interface; this is a representation of the virtual instance of the client, which
 * is connected through server socket. It's used for preparing the answer to be sent and for general
 * operations on the client too.
 *
 * @author Federico Sarrocco, Alessandro Vacca
 * @see PropertyChangeListener
 */

public class VirtualClient implements PropertyChangeListener {
    private int clientID;
    private String nickname;
    private SocketClientConnection socketClientConnection;
    private GameHandler gameHandler;

    /**
     * Constructor VirtualClient creates a new VirtualClient instance.
     *
     * @param clientID of type int - the client ID.
     * @param nickname of type String - the player's nickname.
     * @param socketClientConnection of type SocketClientConnection - the class linking client to
     *     server via socket.
     * @param gameHandler of type GameHandler - GameHandler reference.
     */
    public VirtualClient(
            int clientID,
            String nickname,
            SocketClientConnection socketClientConnection,
            GameHandler gameHandler) {
        this.nickname = nickname;
        this.clientID = clientID;
        this.socketClientConnection = socketClientConnection;
        this.gameHandler = gameHandler;
    }

    /**
     * Method send prepares the answer for sending it through the network, putting it in a serialized
     * package, called SerializedMessage, then sends the packaged answer to the transmission protocol,
     * located in the socket-client handler.
     *
     * @see it.polimi.ingsw.Server.SocketClientConnection for more details.
     * @param serverAnswer of type Answer - the answer to be sent to the user.
     */
    public void send(Answer serverAnswer) {
        if(socketClientConnection.isActive()) {
            SerializedAnswer message = new SerializedAnswer();
            message.setServerAnswer(serverAnswer);
            socketClientConnection.sendSocketMessage(message);
        }
    }

    /**
     * Method sendAll sends the message to all playing clients, thanks to the GameHandler sendAll
     * method. It's triggered from the model's listeners after a player action.
     *
     * @param serverAnswer of type Answer - the message to be sent.
     */
    public void sendAll(Answer serverAnswer) {
        gameHandler.sendAll(serverAnswer);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public SocketClientConnection getConnection() {
        return socketClientConnection;
    }

    public String getNickname() {
        return nickname;
    }

    public int getClientID() {
        return clientID;
    }

    public boolean isConnected(){
        socketClientConnection.ping();
//        send(0);
        return socketClientConnection.isActive();      // TODO check the correctness
    }

    public void setSocketClientConnection(SocketClientConnection connection){
        socketClientConnection = connection;
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }
}
