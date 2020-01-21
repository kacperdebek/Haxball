package Client;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class MPPlayer {

	float x = 256, y = 256;
	public String userName;
	
	int width = 32;
	int height = 32;

	/**
	 * Renders other player in multiplayer scenario
	 * @param g Graphics used for rendering
	 * @param c Color used for rendering the player
	 */
	void render(Graphics g, Color c) {
		g.setColor(c);
		g.fillOval(x - width/2.0f, y - height/2.0f, width, height);
	}
}
