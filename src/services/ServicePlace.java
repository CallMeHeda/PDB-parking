package services;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;

import dao.IPersonnelDAO;
import dao.IPlaceDAO;
import dao.ParkingException;
import dao.ParkingPKException;
import model.Personnel;
import model.Place;

public class ServicePlace implements IServicePlace {
	private IPlaceDAO daoPlace;
	
	// private PropertyChangeSupport pcsPersonne;
	// permet de publier des messages
	private SubmissionPublisher<Message<Place>> publisher = new SubmissionPublisher<>();

	public ServicePlace(IPlaceDAO daoPlace) {
		
		//Garde l'accès au DAO
		this.daoPlace = daoPlace;
		
	}

	@Override
	public List<Place> getListePlace() {
		return daoPlace.getListe("");
	}

	@Override
	public void insert(Place p) throws Exception {
		//ajoute une personne
		p = daoPlace.insert(p);
		//publie l'évènement (message insert avec la personne rajoutée)
		publisher.submit(new Message<Place>(TypeOperation.INSERT, p));

	}
	@Override
	public boolean update(Place p) throws Exception {
		//ajoute une personne
		boolean plMod = daoPlace.update(p);
		//publie l'évènement (message insert avec la personne rajoutée)
		publisher.submit(new Message<Place>(TypeOperation.UPDATE, p));
		
		return plMod;

	}
/**
 * Permet de s'enregistrer comme abonné (écouteur)
 */
	public void addObserver(Subscriber<Message<Place>> obs) {
		//enregistre un écouteur (abonné)
		publisher.subscribe(obs);
	}

	@Override
	/**
	 * Permet de supprimer une personne
	 */
	public void delete(String code) throws ParkingException {
		//recherche la personne
		Optional<Place> p=daoPlace.getFromID(code);
		//si elle existe on la supprime sinon exception PArking
		if (p.isEmpty()) throw new ParkingPKException("Code INEXISTANT",0,"IMMATR");
		try {
			daoPlace.delete(p.get());
			//prévient les abonnés (message delete avec la personne supprimée)
			publisher.submit(new Message<Place>(TypeOperation.DELETE, p.get()));
		} catch (Exception e) {//dipatch l'exception en ParkingException
			if (e instanceof ParkingException) throw (ParkingException)e;
			//au cas où ce n'est pas une exception Parking,  transforme en ParkingException 
			throw new ParkingException("ERREUR ?", 0);
		}
		
	}

	@Override
	public int count() {
		return daoPlace.count();
	}
	
}
