//
//package services;
//
//import java.beans.PropertyChangeListener;
//import java.util.List;
//import java.util.concurrent.Flow.Subscriber;
//
//import model.Historique;
//import model.SortieVoiture;
//import model.Stationnement;
//import view.historique.VueHistoriqueController;
//
//public interface IServiceSortie {
//
//	//List<Stationnement> getListeVoiture();
//
//	SortieVoiture sortieVoit(String plaque);
//	
//	void addEtatPlaceChangeListener(PropertyChangeListener listener);
//
//	void removeEtatPlaceChangeListener(PropertyChangeListener listener);
//
//	//void addObserver(VueHistoriqueController vueHistoriqueController);
//	
//	public void addObserverS(Subscriber<Message<SortieVoiture>> obs);
//
//
//	public int count();
//	
//	
//
//}