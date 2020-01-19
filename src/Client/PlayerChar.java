package Client;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import com.esotericsoftware.kryonet.Connection;

public class PlayerChar {

	public float x;
	public float y;

	float netX;
	float netY;

	int width;
	int height;
	public int id;
	public String userName;

	public Connection c;

	public PlayerChar() {
		Random rand = new Random();
		x = rand.nextInt(800);
		y = rand.nextInt(400);
		width = 32;
		height = 32;
	}

	void update(int delta, GameContainer container) {
		float tempX = x;
		float tempY = y;

		delta = delta * 5;
		if((MenuState.inputHandler.isKeyDown(Input.KEY_UP))) {
			y -= 60 * delta / 1000f;
		}
		if((MenuState.inputHandler.isKeyDown(Input.KEY_DOWN))) {
			y += 60 * delta / 1000f;
		}
		if((MenuState.inputHandler.isKeyDown(Input.KEY_LEFT))) {
			x -= 60 * delta / 1000f;
		}
		if((MenuState.inputHandler.isKeyDown(Input.KEY_RIGHT))) {
			x += 60 * delta / 1000f;
		}
		if(y - height/2.0f < 0) y = height/2.0f;
		if(x - width/2.0f < 0) x = width/2.0f;
		if(y + height/2.0f > container.getHeight()) y = container.getHeight() - height/2.0f;
		if(x + width/2.0f > container.getWidth()) x = container.getWidth() - width/2.0f;
		checkRightGoalCollision();
		checkLeftGoalCollision();
		for (MPPlayer mp : MultiPlayerState.players.values()) {
			if(MultiPlayerState.isCollision(this, mp)) {
				this.x = tempX;
				this.y = tempY;
			}
		}
	}

	public void render(Graphics g, Color c) {
		g.setColor(c);
//		if(y > 3 && userName != null) g.drawString(this.userName, x - this.userName.length() * 4, y - height);
//		else if(y <= 3 && userName != null) g.drawString(this.userName, x - this.userName.length() * 4, y + height);
		g.fillOval(x - width/2.0f, y - height/2.0f, width, height);
	}
	private void checkRightGoalCollision(){
		if(x > 720 && y > 90 && y < 115){
			y = 90;
		}
		if(x > 720 && y < 140 && y > 110){
			y = 140;
		}
		if(y > 124 && y < 250 && x > 762){
			x = 762;
		}
		if(x > 762 && y <= 140 && y > 115){
			y = 140;
			x = 762;
		}
		if(x > 762 && y >= 265 && y < 280){
			y = 265;
			x = 762;
		}
		if(x > 720 && y > 265 && y < 280){
			y = 265;
		}
		if(x > 720 && y < 315 && y > 285){
			y = 315;
		}
	}
	private void checkLeftGoalCollision(){
		if(x < 80 && y > 90 && y < 115){
			y = 90;
		}
		if(x < 80 && y < 140 && y > 110){
			y = 140;
		}
		if(y > 124 && y < 265 && x < 40){
			x = 40;
		}
		if(x < 40 && y <= 140 && y > 115){
			y = 140;
			x = 40;
		}
		if(x < 40 && y >= 265 && y < 280){
			y = 265;
			x = 40;
		}
		if(x < 80 && y > 265 && y < 280){
			y = 265;
		}
		if(x < 80 && y < 315 && y > 285){
			y = 315;
		}
	}
}
