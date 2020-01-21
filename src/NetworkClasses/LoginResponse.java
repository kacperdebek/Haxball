package NetworkClasses;

/**
 * Login response packet
 */
public class LoginResponse {

	private String responseText;

	/**
	 * Gets login response update
	 * @return
	 */
	public String getResponseText(){
		return responseText;
	}

	/**
	 * sets login response status
	 * @param responseText response message to be set
	 */
	public void setResponseText(String responseText){
		this.responseText = responseText;
	}
	
}
