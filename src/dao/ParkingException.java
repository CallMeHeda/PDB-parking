package dao;

public class ParkingException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int code;
    
	public ParkingException(String msg,int code) {
		super(msg);
		this.code=code;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

}
