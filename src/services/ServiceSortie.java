//package services;
//
////import static org.testng.Assert.assertEquals;
//
//import java.beans.PropertyChangeListener;
//import java.beans.PropertyChangeSupport;
//import java.util.List;
//import java.util.concurrent.SubmissionPublisher;
//import java.util.concurrent.Flow.Subscriber;
//
//import dao.DAOFactory;
//import dao.IHistoriqueDAO;
//import dao.IStationnementDAO;
//import model.Historique;
//import model.Personnel;
//import model.SortieVoiture;
//import model.Stationnement;
//import view.historique.VueHistoriqueController;
//
//public class ServiceSortie implements IServiceSortie, {
//	private DAOFactory factory;
//	//private List<Place> places;
//	private IStationnementDAO daoStationnement;
//
//	private final PropertyChangeSupport pcsPlace;
//	private SubmissionPublisher<Message<SortieVoiture>> publisher = new SubmissionPublisher<>();
//	private PropertyChangeSupport pcsHisto;
//
//	public ServiceSortie(DAOFactory factory) {
//		this.factory = factory;
//		
//		daoStationnement = factory.getStationnementDAO();
//
//		pcsPlace = new PropertyChangeSupport(SortieVoiture.class);
//		pcsHisto = new PropertyChangeSupport(Historique.class);
//	}
//
//	@Override
//	public List<Stationnement> getListeVoiture() {
//		//return places;
//		IStationnementDAO dao = factory.getStationnementDAO();
//		List<Stationnement> listeVoitGaree = dao.getListe(null);
//		for(int i = 0; i<listeVoitGaree.size();i++) {
//			System.out.println(listeVoitGaree.get(i).toString());
//		}
//		return listeVoitGaree;
//	}
//
//	@Override
//	public SortieVoiture sortieVoit(String plaque) {
//		SortieVoiture s = null;
//		
//			s = daoStationnement.sortie_voit(plaque);
//			pcsPlace.firePropertyChange(s.getPlace(), false, true);
//			pcsHisto.firePropertyChange("plaque",plaque , plaque);;
//
//			
//			//publisher.submit(new Message<SortieVoiture>(TypeOperation.INSERT, s));
//
//		return s;
//	}
//	
//	
//	/**
//	 * Permet de s'enregistrer comme abonné (écouteur)
//	 */
//		public void addObserver(Subscriber<Message<SortieVoiture>> obs) {
//			//enregistre un écouteur (abonné)
//			publisher.subscribe(obs);
//		}
//
//	@Override
//	public void addEtatPlaceChangeListener(PropertyChangeListener listener) {
//		System.out.println("Ajoute un écouteur: "+ listener.getClass());
//		pcsPlace.addPropertyChangeListener(listener);
//	}
//
//	// se désinscrit de l’écoute des changements de prix
//	@Override
//	public void removeEtatPlaceChangeListener(PropertyChangeListener listener) {
//		System.out.println("Retire un écouteur: "+ listener.getClass());
//		pcsPlace.removePropertyChangeListener(listener);
//	}
//	
//	public void addEtatPlaqueChangeListener(PropertyChangeListener listener) {
//		System.out.println("Ajoute un écouteur: "+ listener.getClass());
//		pcsHisto.addPropertyChangeListener(listener);
//	}
//
//	@Override
//	public void addObserver(VueHistoriqueController vueHistoriqueController) {
//		// TODO Auto-generated method stub
//		
//	}
//	
//	@Override
//	public int count() {
//		IHistoriqueDAO dao = factory.getHistoriqueDAO();
//		return dao.count();
//	}
//
//}
