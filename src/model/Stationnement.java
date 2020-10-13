package model;

//import java.sql.Timestamp;
import java.time.LocalDateTime;
/**
 * 
 * @author Fatima EL-AMRI
 *
 */

public class Stationnement {
	private final Personnel personne;
	private String FKplace;
	private LocalDateTime MomentA;
	
	public Stationnement() {
		this.personne = null;
	}
	
	/**
	 *
	 ** @param FKPersonne
	 * @param FKPlace
	 * @param MomentA
	 */
	public Stationnement(Personnel FKPers, String FKPla, LocalDateTime MomentArr) {
		super();
		this.personne = FKPers;
		this.FKplace = FKPla;
		this.MomentA = MomentArr;
	}

	public String getPlace() {
		return FKplace;
	}

	public void setPlace(String fKPlace) {
		FKplace = fKPlace;
	}

	public LocalDateTime getMomentA() {
		return MomentA;
	}

	public void setMomentA(LocalDateTime momentA) {
		MomentA = momentA;
	}

	public Personnel getPersonne() {
		return personne;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((personne == null) ? 0 : personne.hashCode());
		result = prime * result + ((FKplace == null) ? 0 : FKplace.hashCode());
		result = prime * result + ((MomentA == null) ? 0 : MomentA.hashCode());
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
		Stationnement other = (Stationnement) obj;
		if (personne == null) {
			if (other.personne != null)
				return false;
		} else if (!personne.equals(other.personne))
			return false;
		if (FKplace == null) {
			if (other.FKplace != null)
				return false;
		} else if (!FKplace.equals(other.FKplace))
			return false;
		if (MomentA == null) {
			if (other.MomentA != null)
				return false;
		} else if (!MomentA.equals(other.MomentA))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return "Stationnement [Personne=" + personne.getImmatr() + ", Place=" + FKplace + ", Moment Arrive=" + MomentA + "]";
	}
}
