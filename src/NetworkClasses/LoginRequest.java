package NetworkClasses;

public class LoginRequest {

	private String userName;

	/**
	 * Method for getting users name in the packet
	 * @return returns users name as string
	 */
	public String getUserName(){
		return userName;
	}

	/**
	 * setter for the users name in the packet
	 * @param userName string username to be set as packets username
	 */
	public void setUserName(String userName){
		this.userName = userName;
	}
	
}
