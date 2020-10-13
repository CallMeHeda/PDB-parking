package dao;

public class ParkingPlaceBusyException extends ParkingException {
	private final String place;

	public ParkingPlaceBusyException(String msg, int code, String place) {
		super(msg, code);
		this.place = place;
	}

	/**
	 * @return la place
	 */
	public String getPlace() {
		return place;
	}

}
