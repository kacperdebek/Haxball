package server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import NetworkClasses.PacketUpdateBall;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import Client.PlayerChar;
import NetworkClasses.LoginRequest;
import NetworkClasses.LoginResponse;
import NetworkClasses.Message;

public class MainServer {

	private int tcpPort;
	private int udpPort;
	static Server server;
	private Kryo kryo;

	private static JFrame jFrame;
	static JTextArea jTextArea;
	private static MainServerListener listener = new MainServerListener();

    /**
     * Server constructor
     * @param tcpPort port for tcp
     * @param udpPort port for udp
     */
	public MainServer(int tcpPort, int udpPort) {
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		server = new Server();
		kryo = server.getKryo();
		registerKryoClasses();
	}

    /**
     * Method for starting the server
     */
	private void startServer() {
		Log.info("Starting Server");
		jTextArea.append("Starting Server...");
		jTextArea.append("\n");
		server.start();
		try {
			server.bind(tcpPort, udpPort);
			server.addListener(listener);
			jTextArea.append("Server online! \n");
			jTextArea.append("----------------------------");
			jTextArea.append("\n");
			update();
		} catch (IOException e) {
			Log.info("Port already used");
			jTextArea.append("Port already in use");
			jTextArea.append("\n");
			e.printStackTrace();
		}
	}

    /**
     * Method used for stopping the server
     */
	private static void stopServer() {
		Log.info("Server stopped");
		jTextArea.append("Server stopped.");
		jTextArea.append("\n");
		server.stop();
	}

	private void update() {
		while (true) {

		}
	}

    /**
     * Method used for registering kryo classes
     */
	private void registerKryoClasses() {
		kryo.register(LoginRequest.class);
		kryo.register(LoginResponse.class);
		kryo.register(Message.class);
		kryo.register(PlayerChar.class);
		kryo.register(org.newdawn.slick.geom.Rectangle.class);
		kryo.register(float[].class);
		kryo.register(NetworkClasses.PacketUpdateX.class);
		kryo.register(NetworkClasses.PacketUpdateY.class);
		kryo.register(NetworkClasses.PacketAddPlayer.class);
		kryo.register(NetworkClasses.PacketRemovePlayer.class);
		kryo.register(NetworkClasses.PacketUserName.class);
		kryo.register(NetworkClasses.PacketScore.class);
		kryo.register(PacketUpdateBall.class);
	}

    /**
     * Creates server interface
     */
	private static void createServerInterface() {
		jFrame = new JFrame("GameServerInterface");
		jTextArea = new JTextArea();
		jTextArea.append("\n");
		jTextArea.setLineWrap(true);
		jTextArea.setEditable(false);

		jFrame.add(jTextArea);
		jFrame.setSize(500, 500);
		jFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int i = JOptionPane.showConfirmDialog(null, "You want to shut down the server?");
				if(i == 0) {
					stopServer();
					System.exit(0);
				}
			}
		});
		JScrollPane scrollPane = new JScrollPane(jTextArea);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jFrame.add(scrollPane);
	}

	public static void main(String[] args) {
		Log.set(Log.LEVEL_INFO);
		MainServer main = new MainServer(4070, 4070);
		createServerInterface();
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setResizable(false);
		jFrame.setVisible(true);
		main.startServer();
	}

}
