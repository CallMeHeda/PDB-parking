package dao;

public class ParkingPKException extends ParkingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String champ;

	public ParkingPKException(String msg, int code, String champ) {
		super(msg, code);
		this.champ = champ;

	}

	/**
	 * @return the champ
	 */
	public String getChamp() {
		return champ;
	}

}
