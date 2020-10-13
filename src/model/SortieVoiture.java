package model;

import java.time.LocalDateTime;
import model.Stationnement;

/**
 * 
 * @author Fatima EL-AMRI
 *
 */

public class SortieVoiture{
	private String place;
	private Integer duree;
	
	/**
	 *
	 ** @param place
	 * @param duree
	 */
	public SortieVoiture(String place, Integer duree) {
		this.place = place;
		this.duree = duree;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Integer getDuree() {
		return duree;
	}

	public void setDuree(Integer duree) {
		this.duree = duree;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((duree == null) ? 0 : duree.hashCode());
		result = prime * result + ((place == null) ? 0 : place.hashCode());
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
		SortieVoiture other = (SortieVoiture) obj;
		if (duree == null) {
			if (other.duree != null)
				return false;
		} else if (!duree.equals(other.duree))
			return false;
		if (place == null) {
			if (other.place != null)
				return false;
		} else if (!place.equals(other.place))
			return false;
		return true;
	}

	@Override
	public String toString() {
		
		return "SortieVoiture [place=" + place + ", duree=" + duree + "]";
	}

	public SortieVoiture get() {
		SortieVoiture sortieV= new SortieVoiture(place, duree);
		
		return sortieV;
	}

	
}
