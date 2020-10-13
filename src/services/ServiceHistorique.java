package services;

import static org.testng.Assert.assertEquals;

import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;

import dao.DAOFactory;
import dao.IHistoriqueDAO;
import dao.IStationnementDAO;
import dao.ParkingException;
import dao.ParkingPKException;
import model.Historique;
import model.Place;
import model.SortieVoiture;
import model.Stationnement;

public class ServiceHistorique implements IServiceHistorique {
	private IHistoriqueDAO daoHistorique;
	private IStationnementDAO daoStationnement;
	private DAOFactory factory;
	
	// permet de publier des messages
	private SubmissionPublisher<Message<Historique>> publisher = new SubmissionPublisher<>();
	private SortieVoiture sortieV;
	private final PropertyChangeSupport pcsPlace;

	public ServiceHistorique(DAOFactory factory) {
		
		this.factory = factory;
		
		daoHistorique = factory.getHistoriqueDAO();
		daoStationnement = factory.getStationnementDAO();
		
		pcsPlace = new PropertyChangeSupport(Place.class);
		
	}

	@Override
	public List<Historique> getListeHistorique() {
		//return daoHistorique.getListe("");
		IHistoriqueDAO dao = factory.getHistoriqueDAO();
		List<Historique> l = dao.getListe(null);
		
		return l;
	}
	
	public List<Historique> histo_voit(String plaque) {
		IHistoriqueDAO dao = factory.getHistoriqueDAO();
		List<Historique> histo = dao.historique_voit(plaque);
		
		return histo;

		
	}
	
	@Override
	public SortieVoiture sortieVoit(String plaque) {
		
		try {
		Historique s = factory.getHistoriqueDAO().getFromID(plaque).get();
		
		 sortieV=factory.getStationnementDAO().sortie_voit(plaque).get();
		            pcsPlace.firePropertyChange(sortieV.getPlace(),false,true);
		            publisher.submit(new Message<Historique>(TypeOperation.INSERT, s));
		}catch(Exception e) {
			
		}
		
		return sortieV;
	}
/**
 * Permet de s'enregistrer comme abonné (écouteur)
 */
	public void addObserver(Subscriber<Message<Historique>> obs) {
		//enregistre un écouteur (abonné)
		publisher.subscribe(obs);
	}


	@Override
	public int count() {
		//return daoHistorique.count();
//		IStationnementDAO dao = factory.getStationnementDAO();
//		Integer i=dao.count();
//		return i;
		return daoHistorique.count();
	}
	
}
