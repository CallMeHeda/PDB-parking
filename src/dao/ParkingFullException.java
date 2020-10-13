package dao;

public class ParkingFullException extends ParkingException {
	private final int code;
	
	public ParkingFullException(String msg, int code) {		
		super(msg,code);
		this.code=code;
	}

	/**
	 * @return le code
	 */
	public int getCode() {
		return code;
	}
}
