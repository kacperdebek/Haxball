package Client;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

/**
 * Main menu class
 */
public class MenuState extends BasicGameState {

	private static final int ID_MULTIPLAYERSTATE = 2;

	static String ipAddress;
	static int port;
	private Image bg;
	private static Font font = new Font("Century Gothic", Font.PLAIN, 18);
	private static TrueTypeFont gothic = new TrueTypeFont(font, true);
	private int posX;
	private int posY;

	static Input inputHandler;

    /**
     * Method for initializing resources
     * @param container Game container from lwjgl
     * @param sbg State based game from lwjgl
     * @throws SlickException exception thrown when unsuccessful
     */
	public void init(GameContainer container, StateBasedGame sbg) throws SlickException {
		inputHandler = container.getInput();
		bg = new Image("res/menubg.jpg");
	}

    /**
     *
     * @param container Game container from lwjgl
     * @param sbg State based game from lwjgl
     */
	public void update(GameContainer container, StateBasedGame sbg, int delta) {
		posX = Mouse.getX();
		posY = Mouse.getY();
		if((posX > 361 && posX < 466) && (posY > 208 && posY < 228)) {
			if(Mouse.isButtonDown(0)) {
				JLabel label_ipAddress = new JLabel("IP Address:");
				JTextField textField_ipAddress = new JTextField();

				JLabel label_port = new JLabel("Port:");
				JTextField textField_port = new JTextField();

				Object[] array = {label_ipAddress, textField_ipAddress, label_port, textField_port};

				int res = JOptionPane.showConfirmDialog(null, array, "Join:", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);

				if(res == JOptionPane.OK_OPTION) {
					ipAddress = textField_ipAddress.getText().trim();
					port = Integer.parseInt(textField_port.getText().trim());
					sbg.enterState(ID_MULTIPLAYERSTATE, new FadeOutTransition(), new FadeInTransition());
				}
			}
		}
		if((posX > 361 && posX < 466) && (posY > 149 && posY < 170)) {
			if(Mouse.isButtonDown(0)) {
				System.exit(0);
			}
		}
	}

	/**
	 * Method used for rendering menu graphics and content
	 * @param container Game container from lwjgl
	 * @param sbg State based game from lwjgl
	 * @param g Graphics used for drawing objects
	 */
	public void render(GameContainer container, StateBasedGame sbg, Graphics g) {
		g.drawImage(bg, 0 ,0);
		g.setColor(Color.black);
		g.setFont(gothic);
		g.drawString("Play", 368, 170);
		g.drawString("Exit", 368, 230);

		if((posX > 361 && posX < 466) && (posY > 208 && posY < 228)) {
			g.setColor(new Color(0, 255, 0, 150));
			g.fillRect(330, 170, 120, 25);
		}
		if((posX > 361 && posX < 466) && (posY > 149 && posY < 169)) {
			g.setColor(new Color(0, 255, 0, 150));
			g.fillRect(330, 230, 120, 25);
		}
	}

	/**
	 * Getter for state id
	 * @return returns 0 - menu id
	 */
	public int getID() {
		return 0;
	}

}
