package services;

import java.util.List;
import java.util.concurrent.Flow.Subscriber;

import dao.ParkingException;
import model.Place;

public interface IServicePlace {

	void insert(Place p) throws Exception,ParkingException;
	boolean update(Place p) throws Exception;
	void delete(String p) throws ParkingException;

	List<Place> getListePlace();
	public int count();
	
	public void addObserver(Subscriber<Message<Place>> obs);

}
