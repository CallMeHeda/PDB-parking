package dao;

import java.util.List;

import model.Historique;

public interface IHistoriqueDAO extends IDAO<Historique, String>{
	public List<Historique> historique_voit(String immatr);
}