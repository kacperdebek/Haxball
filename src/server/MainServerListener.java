package server;

import java.util.HashMap;
import java.util.Map;

import NetworkClasses.*;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import Client.PlayerChar;

public class MainServerListener extends Listener {

	public static Map<Integer, PlayerChar> players = new HashMap<Integer, PlayerChar>();

	/**
	 * Method for maintaining user connections and creating new player entities
	 * @param connection
	 */
	public void connected(Connection connection) {
		PlayerChar player = new PlayerChar();
		player.c = connection;

		PacketAddPlayer addPacket = new PacketAddPlayer();
		addPacket.id = connection.getID();
		MainServer.server.sendToAllExceptTCP(connection.getID(), addPacket);

		for (PlayerChar p : players.values()) {
			PacketAddPlayer addPacket2 = new PacketAddPlayer();
			addPacket2.id = p.c.getID();
			connection.sendTCP(addPacket2);
		}
		players.put(connection.getID(), player);
		MainServer.jTextArea.append(connection.getID() + " (ID) joined the server");
		MainServer.jTextArea.append("\n");
	}

	/**
	 * Method for handing clients disconnections
	 * @param connection
	 */
	public void disconnected(Connection connection) {
		players.remove(connection.getID());
		NetworkClasses.PacketRemovePlayer removePacket = new NetworkClasses.PacketRemovePlayer();
		removePacket.id = connection.getID();
		MainServer.server.sendToAllExceptTCP(connection.getID(), removePacket);
		MainServer.jTextArea.append(connection.getID() + " (ID) left the server");
		MainServer.jTextArea.append("\n");
	}

	/**
	 * Method for handling packets recieved from clients
	 * @param connection Connection by which server can identify clients
	 * @param object Object containing certain packets information
	 */
	public void received(Connection connection, Object object) {
		if(object instanceof LoginRequest) {
			LoginRequest request = (LoginRequest) object;
			LoginResponse response = new LoginResponse();
			response.setResponseText("ok");
			connection.sendTCP(response);
			PacketUserName packetUserName = new PacketUserName();
			packetUserName.id = connection.getID();
			packetUserName.userName = request.getUserName();
			MainServer.server.sendToAllExceptUDP(connection.getID(), packetUserName);

			players.get(connection.getID()).userName = request.getUserName();
			for (PlayerChar p : players.values()) {
				PacketUserName packetUserName2 = new PacketUserName();
				packetUserName2.id = p.c.getID();
				packetUserName2.userName = p.userName;
				connection.sendUDP(packetUserName2);
			}
		}
		if(object instanceof NetworkClasses.PacketUpdateX) {
			NetworkClasses.PacketUpdateX packet = (NetworkClasses.PacketUpdateX) object;
			players.get(connection.getID()).x = packet.x;
			packet.id = connection.getID();
			MainServer.server.sendToAllExceptUDP(connection.getID(), packet);
		} else if(object instanceof NetworkClasses.PacketUpdateY) {
			NetworkClasses.PacketUpdateY packet = (NetworkClasses.PacketUpdateY) object;
			players.get(connection.getID()).y = packet.y;
			packet.id = connection.getID();
			MainServer.server.sendToAllExceptUDP(connection.getID(), packet);
		}
		
		if(object instanceof NetworkClasses.PacketScore){
			NetworkClasses.PacketScore packet = (NetworkClasses.PacketScore) object;
			packet.id = connection.getID();
			MainServer.server.sendToAllExceptTCP(connection.getID(), packet);
		}
		if(object instanceof PacketUpdateBall){
			PacketUpdateBall packet = (PacketUpdateBall) object;
			MainServer.server.sendToAllExceptUDP(connection.getID(), packet);
		}
		if(object instanceof NetworkClasses.Message){
			NetworkClasses.Message packet = (NetworkClasses.Message) object;
			MainServer.server.sendToAllExceptTCP(connection.getID(), packet);
		}
	}

}
