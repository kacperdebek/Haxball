package Client;

import java.util.Map;

import NetworkClasses.*;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class PlayerClientListener extends Listener {
	/**
	 * Kryonet method for recieving data from server
	 * @param connection Client connection
	 * @param object Object containing diffirent packets of data
	 */
	public void received(Connection connection, Object object) {
		if (object instanceof LoginResponse) {
			LoginResponse response = (LoginResponse) object;
			System.out.println(response.getResponseText());
			if (response.getResponseText().equalsIgnoreCase("ok")) {
				Log.info("Login Ok");
			} else {
				Log.info("Login failed");
			}
		}
		if (object instanceof PacketAddPlayer) {
			PacketAddPlayer packet = (PacketAddPlayer) object;
			MPPlayer newPlayer = new MPPlayer();
			MultiPlayerState.players.put(packet.id, newPlayer);
		} else if (object instanceof PacketRemovePlayer) {
			PacketRemovePlayer packet = (PacketRemovePlayer) object;
			MultiPlayerState.players.remove(packet.id);
		} else if (object instanceof PacketUpdateX) {
			PacketUpdateX packet = (PacketUpdateX) object;
			MultiPlayerState.players.get(packet.id).x = packet.x;
		} else if (object instanceof PacketUpdateY) {
			PacketUpdateY packet = (PacketUpdateY) object;
			MultiPlayerState.players.get(packet.id).y = packet.y;
		} else if (object instanceof PacketUserName) {
			PacketUserName packet = (PacketUserName) object;
			for (Map.Entry<Integer, MPPlayer> entry : MultiPlayerState.players.entrySet()) {
				if (entry.getKey() == packet.id) {
					entry.getValue().userName = packet.userName;
				}
			}
		}
		if (object instanceof PacketScore) {
			PacketScore packet = (PacketScore) object;
			MultiPlayerState.blueScore = packet.blueScore;
			MultiPlayerState.redScore = packet.redScore;
		}
		if (object instanceof PacketUpdateBall) {
			PacketUpdateBall packet = (PacketUpdateBall) object;
			MultiPlayerState.circle.setCenterX(packet.x);
			MultiPlayerState.circle.setCenterY(packet.y);
		}
		if(object instanceof Message){
			Message packet = (Message) object;
			MultiPlayerState.controlChatSize();
			MultiPlayerState.chatLog.add(packet.userName + ": " + packet.message);
		}
	}
	
}
