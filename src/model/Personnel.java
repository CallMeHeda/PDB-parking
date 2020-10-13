package model;

/**
 * 
 * @author Didier
 *
 */
public class Personnel {
	private final String immatr;
	private String nom;
	private String prenom;
	private String email;
	
	public Personnel() {
		this.immatr = "";
		
	}

	/**
	 * 
	 * @param immatr
	 * @param nom
	 * @param prenom
	 * @param email
	 */
	public Personnel(String immatr, String nom, String prenom, String email) {
		super();
		this.immatr = immatr;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
	}

	public Personnel(String immatr, String nom, String prenom) {
		super();
		this.immatr = immatr;
		this.nom = nom;
		this.prenom = prenom;
		this.email = null;
	}
	
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImmatr() {
		return immatr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((immatr == null) ? 0 : immatr.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((prenom == null) ? 0 : prenom.hashCode());
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
		Personnel other = (Personnel) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (immatr == null) {
			if (other.immatr != null)
				return false;
		} else if (!immatr.equals(other.immatr))
			return false;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (prenom == null) {
			if (other.prenom != null)
				return false;
		} else if (!prenom.equals(other.prenom))
			return false;
		return true;
	}

	@Override
	public String toString() {
		//return "Personnel [immatr=" + immatr + ", nom=" + nom + ", prenom=" + prenom + ", Email=" + email + "]";
		return immatr;
	}

}
