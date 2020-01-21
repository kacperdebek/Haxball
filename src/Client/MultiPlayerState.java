package Client;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;

import NetworkClasses.PacketUpdateBall;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.SelectTransition;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;

import NetworkClasses.LoginRequest;
import NetworkClasses.LoginResponse;
import NetworkClasses.Message;


public class MultiPlayerState extends BasicGameState {
	private int tcpPort;
	private int udpPort;
	private int timeout;
	private int flag = 0;
	public static Client client;
	private Kryo kryo;
	public static int blueScore = 0;
	public static int redScore = 0;
	public static ArrayList<String> chatLog = new ArrayList<>(5);
	private int xKickedCounter = 0;
	private int yKickedCounter = 0;
	private boolean spacePressed = false;
	private static PlayerChar player = new PlayerChar();
	static Map<Integer, MPPlayer> players = new HashMap<Integer, MPPlayer>();
	static Shape circle;
	private Image bg;
	private Image ball;
	private TrueTypeFont ttf32;
	private TrueTypeFont ttf16;
	private TextField chatBox;
	MultiPlayerState() {
	}

	/**
	 * Method used for register kryo classes
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
	 * Connects new client to the multiplayer gameplay
	 * @param ip clients ip
	 */
	private void connect(String ip) {
		try {
			Log.info("connecting...");
			client.start();
			client.connect(timeout, ip, tcpPort, udpPort);
			client.addListener(new PlayerClientListener());
			LoginRequest request = new LoginRequest();
			request.setUserName(PlayerClient.userName);
			client.sendTCP(request);
			Log.info("Connected.");
		} catch (IOException e) {
			Log.info("Server offline");
			e.printStackTrace();
		}
	}

	/**
	 * Initializes client for multiplayer gameplay
	 * @param container Game container from lwjgl
	 * @param sbg State based game from lwjgl
	 */
	public void enter(GameContainer container, StateBasedGame sbg) {
		String ip = MenuState.ipAddress;
		this.udpPort = MenuState.port;
		this.tcpPort = MenuState.port;
		this.timeout = 500000;
		player.userName = PlayerClient.userName;

		client = new Client();
		kryo = client.getKryo();
		registerKryoClasses();
		connect(ip);
	}

	/**
	 * Initializes client resources
	 * @param container Game container from lwjgl
	 * @param sbg State based game from lwjgl
	 * @throws SlickException exception thrown when something goes wrong
	 */
	public void init(GameContainer container, StateBasedGame sbg) throws SlickException {
		circle = new Circle(400, 200, 8);
		bg = new Image("res/background.jpg");
		ball = new Image("res/balltexture.png");
		Font font32 = new Font("Verdana", Font.BOLD, 32);
		Font font16 = new Font("Verdana", Font.BOLD, 16);
		ttf32 = new TrueTypeFont(font32, true);
		ttf16 = new TrueTypeFont(font16, true);
		chatBox = new TextField(container, container.getDefaultFont(), 10, 365, 780, 20);
	}

	/**
	 * Checks for collision between players
	 * @param player Player controlled by user
	 * @param mpPlayer Other player in multiplayer scenario
	 * @return true if collision occurs false otherwise
	 */
	static boolean isCollision(PlayerChar player, MPPlayer mpPlayer) {
		double dist = Math.sqrt(Math.pow(player.x - mpPlayer.x, 2) + Math.pow(player.y - mpPlayer.y, 2));
		return dist < (mpPlayer.height / 2.0f + player.height / 2.0f);
	}

	/**
	 * Substracts two points from each other
	 * @param p1 First point
	 * @param p2 Second point
	 * @return Point after subtraction
	 */
	private static Point2D.Double subtract(Point2D.Double p1, Point2D.Double p2) {
		return new Point2D.Double(p1.x - p2.x, p1.y - p2.y);
	}

	/**
	 * Multiplies points and a double value
	 * @param val double value
	 * @param p2 Point
	 * @return Resulting point
	 */
	private static Point2D.Double multiply(double val, Point2D.Double p2) {
		return new Point2D.Double(val * p2.x, val * p2.y);
	}

	/**
	 * Adds two points to each other
	 * @param p1 First point
	 * @param p2 Second point
	 * @return Point after addition
	 */
	private static Point2D.Double add(Point2D.Double p1, Point2D.Double p2) {
		return new Point2D.Double(p1.x + p2.x, p1.y + p2.y);
	}

	/**
	 * Updates game status between frames
	 * @param container Game container from lwjgl
	 * @param sbg State based game from lwjgl
	 */

	public void update(GameContainer container, StateBasedGame sbg, int delta) {

		if(flag == 0) {
			circle = new Circle(400, 200, 8);
			flag = 1;
		}
		if(container.getInput().isKeyDown(Input.KEY_ESCAPE)) {
			sbg.enterState(0, new FadeInTransition(), new SelectTransition());
		}
		double dist = Math.sqrt(Math.pow(player.x - circle.getCenterX(), 2) + Math.pow(player.y - circle.getCenterY(), 2));
		if(dist < (circle.getWidth()/2.0f + player.height/2.0f)){
			moveBallByPlayer();
		}
		if((MenuState.inputHandler.isKeyDown(Input.KEY_SPACE))) {
			spacePressed = true;
		}
		handleBallKicking(dist);
		runBallKickedAnimation();
		handleLeftGoalCollision();
		handleRightGoalCollision();
		checkBoundariesCollision(container);
		changeBallPosition(circle.getCenterX(), circle.getCenterY());
		int isGoal = checkForScoredGoal();
		handleScorePackets(isGoal);
		player.update(delta, container);
		spacePressed = false;
		updatePackets();
	}

	/**
	 * Handles smooth ball traveling animation
	 */
	private void runBallKickedAnimation(){
		if(xKickedCounter != 0){
			if(xKickedCounter > 0){
				circle.setCenterX(circle.getCenterX() + xKickedCounter);
				xKickedCounter--;
			}
			else{
				circle.setCenterX(circle.getCenterX() + xKickedCounter);
				xKickedCounter++;
			}
		}
		if(yKickedCounter != 0){
			if(yKickedCounter > 0){
				circle.setCenterY(circle.getCenterY() + yKickedCounter);
				yKickedCounter--;
			}
			else{
				circle.setCenterY(circle.getCenterY() + yKickedCounter);
				yKickedCounter++;
			}
		}
	}

	/**
	 * Sends all needed packets for updating
	 * @param isGoal parameter determining whether a goal was scored, and if yes which goal was the culprit
	 */
	private void handleScorePackets(int isGoal){
		if(isGoal != 0){
			if(isGoal == 1)
				blueScore++;
			else if(isGoal == 2)
				redScore++;
			xKickedCounter = 0;
			yKickedCounter = 0;
			NetworkClasses.PacketScore packet = new NetworkClasses.PacketScore();
			packet.blueScore = blueScore;
			packet.redScore = redScore;
			packet.id = client.getID();
			client.sendTCP(packet);
			changeBallPosition(400, 200);
		}
	}

	/**
	 * Check for ball collision with boundaries
	 * @param container Game container from lwjgl
	 */
	private void checkBoundariesCollision(GameContainer container){
		if(circle.getCenterX() < circle.getWidth()/2.0f + 5){
			circle.setCenterX(circle.getWidth() + 10);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if (circle.getCenterY() < circle.getHeight()/2.0f + 5){
			circle.setCenterY(circle.getHeight() + 10);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getCenterX() > container.getWidth() - circle.getWidth()/2.0f - 5){
			circle.setCenterX(container.getWidth() - 34);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getCenterY() > container.getHeight() - circle.getHeight()/2.0f - 5){
			circle.setCenterY(container.getHeight() - 34);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
	}

    /**
     * Determine kick direction and set animation variables
     * @param p3 point of collision between ball and player
     */
	private void handleCollisionFromAllSides(Point2D.Double p3){
		if ((int) p3.x > (circle.getCenterX())) {
			if ((int) p3.y > (circle.getCenterY() + 2)) {
				yKickedCounter = -20;
				xKickedCounter = -20;
			} else if ((int) p3.y < (circle.getCenterY() - 2)) {
				yKickedCounter = 20;
				xKickedCounter = -20;
			} else {
				xKickedCounter = -20;
			}
		} else if ((int) p3.x < (circle.getCenterX())) {
			if ((int) p3.y > (circle.getCenterY() + 2)) {
				yKickedCounter = -20;
				xKickedCounter = 20;
			} else if ((int) p3.y < (circle.getCenterY() - 2)) {
				yKickedCounter = 20;
				xKickedCounter = 20;
			} else {
				xKickedCounter = 20;
			}
		} else {
			if ((int) p3.y > (circle.getCenterY() + 2)) {
				yKickedCounter = -20;
			} else {
				yKickedCounter = 20;
			}
		}
	}

    /**
     * Upon collision move the ball in correct direction
     */
	private void moveBallByPlayer(){
		if (circle.getCenterX() > player.x + 5) {
			circle.setCenterX(circle.getCenterX() + 5);
		}
		if (circle.getCenterX() < player.x - 5) {
			circle.setCenterX(circle.getCenterX() - 5);
		}
		if (circle.getCenterY() > player.y + 5) {
			circle.setCenterY(circle.getCenterY() + 5);
		}
		if (circle.getCenterY() < player.y - 5) {
			circle.setCenterY(circle.getCenterY() - 5);
		}
	}

    /**
     * Method for calcutaling the point of collision between ball and player
     * @param dist distance between player and the ball
     */
	private void handleBallKicking(double dist){
		if(dist < (player.width/2.0f + circle.getWidth()/2.0f) + 5){
			if(spacePressed) {
				double a = (25 + 256 + Math.pow(dist, 2)) / (2 * dist);
				Point2D.Double p1 = new Point2D.Double(player.x, player.y);
				Point2D.Double p2 = new Point2D.Double(circle.getCenterX(), circle.getCenterY());
				Point2D.Double p3 = add(p1, (multiply((a / dist), (subtract(p2, p1)))));
				handleCollisionFromAllSides(p3);
			}
		}
	}

    /**
     * check whether the ball collides with the left side goal
     */
	private void handleLeftGoalCollision(){
		if(circle.getCenterX() < 82 && circle.getCenterY() > 95 && circle.getCenterY() < 105){
			circle.setCenterY(95);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getCenterX() < 82 && circle.getCenterY() < 130 && circle.getCenterY() > 110){
			circle.setCenterY(130);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getCenterY() > 124 && circle.getCenterY() < 272 && circle.getCenterX() < 33){
			circle.setCenterX(33);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getCenterX() < 33 && circle.getCenterY() <= 130 && circle.getCenterY() > 105){
			circle.setCenterY(130);
			circle.setCenterX(33);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getCenterX() < 33 && circle.getCenterY() >= 272 && circle.getCenterY() < 277){
			circle.setCenterY(272);
			circle.setCenterX(33);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getCenterX() < 82 && circle.getCenterY() > 272 && circle.getCenterY() < 285){
			circle.setCenterY(272);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getCenterX() < 82 && circle.getCenterY() < 304 && circle.getCenterY() > 294){
			circle.setCenterY(304);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
	}

    /**
     * check whether the ball collides with the right goal
     */
	private void handleRightGoalCollision(){
		if(circle.getX() > 704 && circle.getY() > 95 && circle.getY() < 105){
			circle.setY(95);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getX() > 704 && circle.getY() < 124 && circle.getY() > 97){
			circle.setY(124);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getY() > 124 && circle.getY() < 265 && circle.getX() > 768){
			circle.setX(768);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getX() > 768 && circle.getY() <= 124 && circle.getY() > 105){
			circle.setY(124);
			circle.setX(768);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getX() > 768 && circle.getY() >= 265 && circle.getY() < 270){
			circle.setY(265);
			circle.setX(768);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getX() > 704 && circle.getY() > 265 && circle.getY() < 275){
			circle.setY(265);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
		if(circle.getX() > 704 && circle.getY() < 294 && circle.getY() > 284){
			circle.setY(294);
			yKickedCounter = 0;
			xKickedCounter = 0;
		}
	}

    /**
     * check if the ball is within the boundaries of goal and determine if the goal was scored or not
     * @return 1 if goal scored at the left goal, 2 if the goal was scored at the right goal, 0 otherwise
     */
	public int checkForScoredGoal(){
		if(circle.getCenterX() < 82 && circle.getCenterY() < 273 && circle.getCenterY() > 130){
			return 1;
		}
		else if(circle.getCenterX() > 725 && circle.getCenterY() < 273 && circle.getCenterY() > 130){
			return 2;
		}
		return 0;
	}

    /**
     * Method for rendering chat interface
     * @param container Game container from lwjgl
     * @param g Graphics for drawing graphics
     */
	private void renderChat(GameContainer container, Graphics g) {
		g.setColor(new Color(117, 117, 117, 150));
		g.fillRect(0, 0, 800, 400);
		g.setColor(new Color(255, 255, 255, 150));
		for(int i = 0 ; i < chatLog.size(); i++){
			g.setFont(ttf16);
			g.drawString(chatLog.get(i), 10, 10 + 18*i);
		}
		chatBox.render(container, g);
		if(MenuState.inputHandler.isKeyPressed(Input.KEY_ENTER)){
			controlChatSize();
			String message = chatBox.getText();
			chatLog.add(player.userName + ": " + message);
			chatBox.setText("");
			sendMessagePacket(message, player.userName);
		}
	}

	/**
	 * Method for controlling the chat size, removing excess messages starting from the top
	 */
	public static void controlChatSize(){
		if(chatLog.size() > 17){
			chatLog.remove(0);
		}
	}

	/**
	 * Method for sending chat message message packet to the server
	 * @param message Message contents
	 * @param userName Username of the author
	 */
	private void sendMessagePacket(String message, String userName){
		NetworkClasses.Message mPacket = new NetworkClasses.Message();
		mPacket.message = message;
		mPacket.userName = userName;
		client.sendTCP(mPacket);
	}

	/**
	 * Method used for changing the ball position
	 * @param x new center point x coord for the ball
	 * @param y new center point y coord for the ball
	 */
	private void changeBallPosition(float x, float y) {
		circle.setCenterX(x);
		circle.setCenterY(y);
		PacketUpdateBall packet = new PacketUpdateBall();
		packet.x = circle.getCenterX();
		packet.y = circle.getCenterY();
		client.sendUDP(packet);
	}

	/**
	 * Method sending updated information about players to the server
	 */
	private void updatePackets(){
		if(player.netX != player.x) {
			NetworkClasses.PacketUpdateX packetX = new NetworkClasses.PacketUpdateX();
			packetX.x = player.x;
			client.sendUDP(packetX);
			player.netX = player.x;
		}
		if(player.netY != player.y) {
			NetworkClasses.PacketUpdateY packetY = new NetworkClasses.PacketUpdateY();
			packetY.y = player.y;
			client.sendUDP(packetY);
			player.netY = player.y;
		}
	}

	/**
	 * Method for rendering all the game graphics
	 * @param container Game container from lwjgl
	 * @param sbg State based game from lwjgl
	 * @param g Graphics for drawing graphics
	 */
	public void render(GameContainer container, StateBasedGame sbg, Graphics g) {
		g.drawImage(bg, 0, 0);
		g.setFont(ttf32);
		g.drawString(redScore + " : " + blueScore, container.getWidth() / 2 - 38, container.getHeight() / 16);
		player.render(g, Color.red);
		for (MPPlayer mp : players.values()) {
			mp.render(g, Color.blue);
		}
		g.setColor(Color.orange);
		//g.fill(circle);
		g.texture(circle, ball, true);
		if(MenuState.inputHandler.isKeyDown(Input.KEY_TAB)) {
			renderChat(container, g);
		}
	}

	/**
	 * Getter for current state id
	 * @return returns 2
	 */
	public int getID() {
		return 2;
	}

}
