package controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import dao.DAOFactory;
import dao.IPersonnelDAO;
import dao.IStationnementDAO;
import dao.ParkingException;
import dao.ParkingFullException;
import dao.ParkingPKException;
import dao.ParkingPlaceBusyException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import model.Personnel;
import model.Place;
import model.Stationnement;
import services.IServiceArrivee;
import services.IServicePersonnel;
import services.Message;
import services.TypeOperation;
import view.statusBar.StatusBar;
import view.statusBar.StatusBar.TypeMessage;

public class BorneEntreeCtrl extends BorderPane implements PropertyChangeListener{
//	private DAOFactory factory;
//	private IStationnementDAO daoStationnement;
//	private IPersonnelDAO daoPersonnel;
	
	private ResourceBundle bundle;
	private IServiceArrivee service;
	private StatusBar statusBar;
	//Pseudo class pour l'occupation d'une place
	private final PseudoClass busyClass = PseudoClass.getPseudoClass("busy");
	//Maintient un lien entre chaque bouton et le code sa place
	private Map<String,Button> boutons= new HashMap<>();
	
//	private ObservableList<Personnel> loPers;
//	private ObservableList<String> loSta2;
	//private ComboBox <String> combo;
	//private IServicePersonnel serviceP;
	
	/**
	 * 
	 * @param service
	 */
	public BorneEntreeCtrl(IServiceArrivee service, ResourceBundle bundle) {
		super();
		this.service = service;
		this.bundle = bundle;
		create();
	}


	private String showErreur(ParkingException e) {
		if (e instanceof ParkingFullException)
			return bundle.getString("Plein");
		if (e instanceof ParkingPlaceBusyException)
			return bundle.getString("PlaceBusy");
		if (e instanceof ParkingPKException)
			return bundle.getString("AlreadyIn");
		if (e instanceof ParkingException)
			return bundle.getString("Unknown");
		return "ERROR X";
	}

	/**
	 * Crée les éléments de la vue
	 */
	private void create() {
		// Récupère les places
		List<Place> places = service.getListePlace();

		// panneau haut
		HBox ph = new HBox(5);
		Label txt = new Label(bundle.getString("plaque"));
		TextField plaque = new TextField();
		
		plaque.setPromptText("Entrez une plaque d'immatriculation");
		// permet de rendre la zone de texte extensible avec la fenêtre
		HBox.setHgrow(plaque, Priority.ALWAYS);
		
		Button btAutoIn = new Button(bundle.getString("autoIn"));
		btAutoIn.setOnAction(e -> {
			try {
//				if (!plaque.getText().matches("[A-Z]{3}[-]{1}[0-9]{3}")) {
//					plaque.setText("");
//				}
				Stationnement s = service.arriveeVoit(plaque.getText(), null);
				
				// Affiche l'information dans la barre de status
				String msg = s.getPersonne().getImmatr() + " " + bundle.getString("VoitPlace") + s.getPlace();
				statusBar.setInfoStatus(msg, TypeMessage.INFO);
			} catch (ParkingException exc) {
				statusBar.setInfoStatus(showErreur(exc), TypeMessage.ERROR);
			}
		});
		ph.getChildren().addAll(txt, plaque, btAutoIn);
		ph.getStyleClass().add("b-in-hbox");
		setTop(ph);
		

		// Panneau centre
		GridPane pPlaces = new GridPane();
		pPlaces.setId("gpPlace");
		pPlaces.setPadding(new Insets(5.0));
		pPlaces.setHgap(5);
		pPlaces.setVgap(5);
		pPlaces.setAlignment(Pos.CENTER);
		boutons.clear();//Vide la map
		int ligne = 0;// ligne de départ pour le bouton
		int colonne = 0;// colonne de départ pour place
		for (Place p : places) {
			Button btPlace;
			// Texte du bouton contient le code de la place
			btPlace = new Button(p.getCode());
			btPlace.pseudoClassStateChanged(busyClass, !p.getLibre());
			btPlace.getStyleClass().add("bt-place");
			boutons.put(p.getCode(),btPlace);
			
			btPlace.setOnAction(e -> {
				String msg;
				try {
					Stationnement s = service.arriveeVoit(plaque.getText(), p.getCode());
					msg = s.getPersonne().getImmatr() + " " + bundle.getString("VoitPlace") + s.getPlace();
					// Affiche l'information dans la barre de status
					statusBar.setInfoStatus(msg, TypeMessage.INFO);
				} catch (ParkingException exc) {
					statusBar.setInfoStatus(showErreur(exc), TypeMessage.ERROR);
				}
			});
			
			if (p.getTaille() > 5) {
				//List<Place> places1 = service.getListePlace();

				// Grande place
				ligne++;
				GridPane.setHalignment(btPlace, HPos.CENTER);
				btPlace.setPrefWidth(155);
				pPlaces.add(btPlace, 0, ligne, 2, 1);
				colonne = 0;
			} else {
				btPlace.setPrefWidth(75);
				if (colonne == 0)
					ligne++;
				pPlaces.add(btPlace, colonne, ligne, 1, 1);
				colonne = (colonne + 1) % 2;
			}
		}

		setCenter(pPlaces);
		// panneau status
		statusBar = new StatusBar();
		setBottom(statusBar);
	}
	

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println(evt.getSource());
		Button bt=boutons.get(evt.getPropertyName());
		if (bt!=null && evt.getNewValue() instanceof Boolean) bt.pseudoClassStateChanged(busyClass, !(Boolean)evt.getNewValue());
	}
}
