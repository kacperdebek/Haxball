package Client;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Client side of connection
 */
public class PlayerClient extends StateBasedGame {

	public static String userName;
	public PlayerClient(String title) {
		super(title);
	}

	/**
	 * Method for initializing list of states such as menu and gameplay state
	 * @param container Game container from lwjgl
	 */
	public void initStatesList(GameContainer container) {
		this.addState(new MenuState());
		this.addState(new MultiPlayerState());
	}

	public static void main(String[] args) throws SlickException {
		userName = getUserName();
		AppGameContainer app = new AppGameContainer(new PlayerClient("Soccer"));
		app.setAlwaysRender(true);
		app.setTargetFrameRate(60);
		app.setDisplayMode(800, 400, false);
		app.setShowFPS(false);
		app.setVSync(true);
		app.start();
	}

	/**
	 * Method for recieving username from user input
	 * @return returns user inputted username
	 */
	private static String getUserName() {
		String s = "";
		JLabel label_login = new JLabel("Username:");
		JTextField login = new JTextField();
		Object[] array = {label_login, login};
		int res = JOptionPane.showConfirmDialog(null, array, "Login", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if(res == JOptionPane.OK_OPTION) {
			System.out.println("username: " + login.getText().trim());
			s = login.getText().trim();
		} else {System.exit(1);}
		return s;
	}
}
