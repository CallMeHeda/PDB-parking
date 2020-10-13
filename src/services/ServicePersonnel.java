package services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;

import dao.IPersonnelDAO;
import dao.ParkingException;
import dao.ParkingPKException;
import model.Personnel;

public class ServicePersonnel implements IServicePersonnel {
	private IPersonnelDAO daoPersonne;
	
	// private PropertyChangeSupport pcsPersonne;
	// permet de publier des messages
	private SubmissionPublisher<Message<Personnel>> publisher = new SubmissionPublisher<>();

	public ServicePersonnel(IPersonnelDAO daoPersonne) {
		
		//Garde l'accès au DAO
		this.daoPersonne = daoPersonne;
		
	}

	@Override
	public List<Personnel> getListePersonne() {
		return daoPersonne.getListe("");
	}

	@Override
	public void insert(Personnel p) throws Exception {
		//ajoute une personne
		p = daoPersonne.insert(p);
		//publie l'évènement (message insert avec la personne rajoutée)
		publisher.submit(new Message<Personnel>(TypeOperation.INSERT, p));

	}
	
//	/**
//	 * Modifier les données d'une personne
//	 */
	@Override
	public boolean update(Personnel p) throws Exception {
		boolean pMod = daoPersonne.update(p);
		
		publisher.submit(new Message<Personnel>(TypeOperation.UPDATE, p));
		return pMod;

	}
/**
 * Permet de s'enregistrer comme abonné (écouteur)
 */
	public void addObserver(Subscriber<Message<Personnel>> obs) {
		//enregistre un écouteur (abonné)
		publisher.subscribe(obs);
	}

	@Override
	/**
	 * Permet de supprimer une personne
	 */
	public void delete(String code) throws ParkingException {
		//recherche la personne
		Optional<Personnel> p=daoPersonne.getFromID(code);
		//si elle existe on la supprime sinon exception PArking
		if (p.isEmpty()) throw new ParkingPKException("Code INEXISTANT",0,"IMMATR");
		try {
			daoPersonne.delete(p.get());
			//prévient les abonnés (message delete avec la personne supprimée)
			publisher.submit(new Message<Personnel>(TypeOperation.DELETE, p.get()));
		} catch (Exception e) {//dipatch l'exception en ParkingException
			if (e instanceof ParkingException) throw (ParkingException)e;
			//au cas où ce n'est pas une exception Parking,  transforme en ParkingException 
			throw new ParkingException("ERREUR ?", 0);
		}
		
	}

	@Override
	public int count() {
		return daoPersonne.count();
	}
	
}
