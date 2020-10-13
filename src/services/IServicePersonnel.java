package services;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.Flow.Subscriber;

import dao.ParkingException;
import model.Personnel;

public interface IServicePersonnel {
	List<Personnel> getListePersonne();

	void insert(Personnel p) throws ParkingException, Exception;
	
	void delete(String p) throws ParkingException;
	public void addObserver(Subscriber<Message<Personnel>> obs);
	//public void removeObserver(Observer o);

	public int count();

	boolean update(Personnel p) throws Exception;
}
