package services;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;

import dao.DAOFactory;
import dao.IPlaceDAO;
import dao.IStationnementDAO;
import dao.ParkingException;
import model.Place;
import model.SortieVoiture;
import model.Stationnement;

public class ServiceArrivee implements IServiceArrivee {
	private DAOFactory factory;
	private IStationnementDAO daoStationnement;

	private final PropertyChangeSupport pcsPlace;

	private SubmissionPublisher<Message<Stationnement>> publisher = new SubmissionPublisher<>();
	//private SubmissionPublisher<Message<Stationnement>> publisher_sortie = new SubmissionPublisher<>();

	private SortieVoiture sortieV;



	public ServiceArrivee(DAOFactory factory) {
		this.factory = factory;
		
		daoStationnement = factory.getStationnementDAO();

		pcsPlace = new PropertyChangeSupport(Place.class);

	}

	@Override
	public List<Place> getListePlace() {
		//return places;
		IPlaceDAO dao = factory.getPlaceDAO();
		List<Place> listePl = dao.getListe(null);
		for(int i = 0; i<listePl.size();i++) {
			System.out.println(listePl.get(i).toString());
		}
		return listePl;
	}
	
	@Override
	public List<Stationnement> getListeSta() {
		IStationnementDAO dao = factory.getStationnementDAO();
		List<Stationnement> l = dao.getListe(null);
		
		return l;
	}


	@Override
	public Stationnement arriveeVoit(String plaque, String place) throws ParkingException {
		Stationnement s = null;
		try {
			s = daoStationnement.arrivee_voit(plaque, place);
			pcsPlace.firePropertyChange(s.getPlace(), true, false);
			publisher.submit(new Message<Stationnement>(TypeOperation.INSERT, s));

		} catch (Exception e) {
			if (e instanceof ParkingException)
				throw (ParkingException) e;
		}
		return s;
	}
	
	@Override
	public SortieVoiture sortieVoit(String plaque) {
		
		try {
		Stationnement s = factory.getStationnementDAO().getFromID(plaque).get();
		
		 sortieV=factory.getStationnementDAO().sortie_voit(plaque).get();
		            pcsPlace.firePropertyChange(sortieV.getPlace(),false,true);
		            publisher.submit(new Message<Stationnement>(TypeOperation.DELETE, s));
		}catch(Exception e) {
			
		}
		
		return sortieV;
		
		
//		SortieVoiture s = null;
//		s = daoStationnement.sortie_voit(plaque);
//		try {
//		Stationnement sortieV = null;	
//			//if(s instanceof Stationnement){
//				//sortieV = s;
//				pcsPlace.firePropertyChange(s.getPlace(), false, true);
////				sortieV.setMomentA(s.getMomentA());
////				sortieV.setPlace(s.getPlace());
//				//pcsHisto.firePropertyChange("plaque",plaque , plaque);
//					publisher.submit(new Message<Stationnement>(TypeOperation.DELETE, s));
//				
//			//}
//		}catch(Exception e) {
//			System.out.println("NOPE");
//		}
//		
	}
	
	@Override
	public void addEtatPlaceChangeListener(PropertyChangeListener listener) {
		System.out.println("Ajoute un écouteur: "+ listener.getClass());
		pcsPlace.addPropertyChangeListener(listener);
	}

	// se désinscrit de l’écoute des changements
	@Override
	public void removeEtatPlaceChangeListener(PropertyChangeListener listener) {
		System.out.println("Retire un écouteur: "+ listener.getClass());
		pcsPlace.removePropertyChangeListener(listener);
	}
	
	/**
	 * Permet de s'enregistrer comme abonné (écouteur)
	 */
		public void addObserver(Subscriber<Message<Stationnement>> obs) {
			//enregistre un écouteur (abonné)
			publisher.subscribe(obs);
		}

//	@Override
//	public void addObserver(VueHistoriqueController vueHistoriqueController) {
//		
//	}

//
//	@Override
//	public void addObserverS(Subscriber<Message<SortieVoiture>> obs) {
//		publisher_sortie.subscribe(obs);
//	}
	
}
