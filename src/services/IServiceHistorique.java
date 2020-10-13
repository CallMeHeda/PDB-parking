package services;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.Flow.Subscriber;

import dao.ParkingException;
import model.Historique;
import model.Personnel;
import model.SortieVoiture;

public interface IServiceHistorique {

	List<Historique> getListeHistorique();
	public List<Historique> histo_voit(String plaque);
	public SortieVoiture sortieVoit(String plaque);
	
	public void addObserver(Subscriber<Message<Historique>> obs);
	//public void removeObserver(Observer o);

	public int count();


}
