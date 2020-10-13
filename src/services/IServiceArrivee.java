package services;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Flow.Subscriber;

import controller.ParkingCtrl;
import dao.ParkingException;
import model.Personnel;
import model.Place;
import model.SortieVoiture;
import model.Stationnement;
import view.stationnement.VueStationnnementController;

public interface IServiceArrivee {

	List<Place> getListePlace();
	
	SortieVoiture sortieVoit(String plaque);

	Stationnement arriveeVoit(String plaque, String string) throws ParkingException;

	void removeEtatPlaceChangeListener(PropertyChangeListener listener);

	void addEtatPlaceChangeListener(PropertyChangeListener listener);
	
	List<Stationnement> getListeSta();

	public void addObserver(Subscriber<Message<Stationnement>> obs);

	
	//public void addObserverS(Subscriber<Message<SortieVoiture>> obs);

}
