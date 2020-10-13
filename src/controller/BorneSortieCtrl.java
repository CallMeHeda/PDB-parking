package controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import dao.ParkingException;
import dao.ParkingFullException;
import dao.ParkingPKException;
import dao.ParkingPlaceBusyException;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import model.Place;
import model.SortieVoiture;
import model.Stationnement;
import services.IServiceArrivee;
import services.IServiceHistorique;
import services.IServicePersonnel;
import view.statusBar.StatusBar;
import view.statusBar.StatusBar.TypeMessage;

public class BorneSortieCtrl extends BorderPane implements PropertyChangeListener{
	private ResourceBundle bundle;
	private IServiceArrivee serviceSortie;
	private StatusBar statusBar;
	private final PseudoClass busyClass = PseudoClass.getPseudoClass("busy");

	private Map<String,Button> boutons= new HashMap<>();
	private Button btPlace;

	public BorneSortieCtrl(IServiceArrivee serviceBorneIn, ResourceBundle bundle) {
		super();
		this.serviceSortie = serviceBorneIn;
		this.bundle = bundle;
		create();
	}
	

	private void create()  {
		
		List<Stationnement> voiture_garee = serviceSortie.getListeSta();

		HBox ph = new HBox(5);
		Label txt = new Label(bundle.getString("plaque"));
		TextField plaque = new TextField();
		
		HBox.setHgrow(plaque, Priority.ALWAYS);
		Button btAutoOut = new Button(bundle.getString("autoOut"));
		btAutoOut.setOnAction(e -> {
			try {
				SortieVoiture s = serviceSortie.sortieVoit(plaque.getText());
				String msg = "La place " + s.getPlace() + " " + bundle.getString("placeLibre");
				statusBar.setInfoStatus(msg, TypeMessage.INFO);
			}catch(Exception exc){
				statusBar.setInfoStatus("Entrez l'immatriculation d'une voiture existante et déjè stationnées", 
						TypeMessage.ERROR);
			}
			
		});
		ph.getChildren().addAll(txt, plaque, btAutoOut);
		ph.getStyleClass().add("b-in-hbox");
		//ph.setPadding(new Insets(5));
		setTop(ph);

		// Centre
		GridPane pPlaces = new GridPane();
		pPlaces.setId("gpPlace");
		pPlaces.setPadding(new Insets(5.0));
		pPlaces.setHgap(5);
		pPlaces.setVgap(5);
		pPlaces.setAlignment(Pos.CENTER);
		boutons.clear();
		int ligne = -1;
		int colonne = 0;
		for (Stationnement sta : voiture_garee) {
			// Texte du bouton contient l'immatr
//	        FileInputStream input = new FileInputStream("/view/images/black_red.png");
//	        Image image = new Image(input);
//	        ImageView imageView = new ImageView(image);
			btPlace = new Button(sta.getPersonne().getImmatr()/*, imageView*/);
			btPlace.getStyleClass().add("bt-place");
			boutons.put(sta.getPersonne().getImmatr(),btPlace);
			btPlace.setOnAction(e -> {
				String msg;
				
					SortieVoiture s = serviceSortie.sortieVoit(sta.getPersonne().getImmatr());
					//Cache le bouton quand une voiture sort	

//						if(btPlace.getText()==sta.getPersonne().getImmatr()){
//							btPlace.setVisible(isDisable());
//							System.out.println(btPlace.getText());
//
//						}
					
					msg = sta.getPersonne().getImmatr() + " " + bundle.getString("sortieVoit") + " " + s.getPlace();
					statusBar.setInfoStatus(msg, TypeMessage.INFO);			
			});
			Place p = new Place(sta.getPlace());
			if (p.getTaille() > 5) {
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
		statusBar = new StatusBar();
		setBottom(statusBar);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println(evt.getSource());
		Button bt=boutons.get(evt.getPropertyName());
		if (bt!=null && evt.getNewValue() instanceof Boolean) bt.pseudoClassStateChanged(busyClass, !(Boolean)evt.getNewValue());
	}
	
//	public void setUp(IServiceHistorique serviceHisto) {
//		this.serviceHisto = serviceHisto;
//	}
}
