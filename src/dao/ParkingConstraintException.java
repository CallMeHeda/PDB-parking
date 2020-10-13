package dao;

public class ParkingConstraintException extends ParkingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String champ;

	public ParkingConstraintException(String msg, int code, String champ) {
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
