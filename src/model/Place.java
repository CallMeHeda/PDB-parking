package model;

/**
 * @author Faima EL-AMRI
 *
 */
public class Place {
	
	private final String code;
	private int taille;
	private boolean libre;
	
	/**
	 * 
	 * @param code
	 * @param taille
	 * @param libre
	 */
	
	public Place(String code, int taille, boolean libre) {
		super();
		this.code = code;
		this.taille = taille;
		this.libre = libre;
	}

	public Place(String code) {
		super();
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public int getTaille() {
		return taille;
	}

	public void setTaille(int taille) {
		this.taille = taille;
	}

	public boolean getLibre() {
		return libre;
	}

	public void setLibre(boolean libre) {
		this.libre = libre;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + (libre ? 1231 : 1237);
		result = prime * result + taille;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Place other = (Place) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (libre != other.libre)
			return false;
		if (taille != other.taille)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Place [code=" + code + ", taille=" + taille + ", libre=" + libre + "]";
	}
}
