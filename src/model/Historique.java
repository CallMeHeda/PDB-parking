package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 
 * @author Fatima EL-AMRI
 *
 */
public class Historique {
	private final String FKPersonne;
	private final LocalDateTime MomentA;
	private String FKPlace;
	private LocalDateTime MomentS;
	private long minutes;

	/**
	 * 
	 */
	public Historique(String FKPers, LocalDateTime momentArr, String FKPla, LocalDateTime momentSor) {
		super();
		this.FKPersonne = FKPers;
		this.MomentA = momentArr;
		this.FKPlace = FKPla;
		this.MomentS = momentSor;
	}
	
	public Historique(String FKPers, LocalDateTime momentArr, String FKPla, LocalDateTime momentSor, long duree) {
		super();
		this.FKPersonne = FKPers;
		this.MomentA = momentArr;
		this.FKPlace = FKPla;
		this.MomentS = momentSor;
		this.minutes = duree;
	}
	
	public String getFKPlace() {
		return FKPlace;
	}

	public void setFKPlace(String fKPlace) {
		FKPlace = fKPlace;
	}

	public LocalDateTime getMomentS() {
		return MomentS;
	}

	public void setMomentS(LocalDateTime momentS) {
		MomentS = momentS;
	}

	public String getFKPersonne() {
		return FKPersonne;
	}

	public LocalDateTime getMomentA() {
		return MomentA;
	}
	
	public long getMinutes() {
		return minutes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((FKPersonne == null) ? 0 : FKPersonne.hashCode());
		result = prime * result + ((MomentA == null) ? 0 : MomentA.hashCode());
		result = prime * result + ((FKPlace == null) ? 0 : FKPlace.hashCode());
		result = prime * result + ((MomentS == null) ? 0 : MomentS.hashCode());
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
		Historique other = (Historique) obj;
		if (FKPersonne == null) {
			if (other.FKPersonne != null)
				return false;
		} else if (!FKPersonne.equals(other.FKPersonne))
			return false;
		if (MomentA == null) {
			if (other.MomentA != null)
				return false;
		} else if (!MomentA.equals(other.MomentA))
			return false;
		if (FKPlace == null) {
			if (other.FKPlace != null)
				return false;
		} else if (!FKPlace.equals(other.FKPlace))
			return false;
		if (MomentS == null) {
			if (other.MomentS != null)
				return false;
		} else if (!MomentS.equals(other.MomentS))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Historique [Plaque d'immatricualation de la voiture = " + FKPersonne + ", Moment d'arrivee = " + MomentA + ", "
				+ "Place = " + FKPlace + ", Moment de sortie = " + MomentS + ", Duree = " + minutes + "]";
	}

}

