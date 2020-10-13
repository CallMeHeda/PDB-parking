package view.stationnement;


import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.org.objectweb.asm.Label;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.Stationnement;
import services.IServiceArrivee;
import services.Message;

public class VueStationnnementController implements Initializable, Subscriber<Message<Stationnement>>{
	// Logger
	static Logger logger = LoggerFactory.getLogger(VueStationnnementController.class);
	// accès au bundle
	private ResourceBundle bundle;
	// Composants de la vue FXML
	 @FXML
	 private GridPane paneSta;
	 @FXML
	 private TableView<Stationnement> tblSta;
	 @FXML
	 private TableColumn<Stationnement, String> col_immatr;
	 @FXML
	 private TableColumn<Stationnement, String> col_place;
	 @FXML
	 private TableColumn<Stationnement, LocalDateTime> col_mom;
	 @FXML
	 private ComboBox<String> choiceBox;
	 @FXML
	 private TextField zRech;
	 
	 @FXML
	 private ResourceBundle resources;
	 @FXML
	 private URL location;
	
	private IServiceArrivee service;

	// Observer qui sera initialisé par OnSubscribe
	private Subscription subscription;
	private ObservableList<Stationnement> loSta;
	private Stationnement sta;


	/*
	 * Permet d'envoyer l'accès à la couche de service ServiceArrivee
	 */
	public void setUp(IServiceArrivee service) {
		this.service = service;
		initialiseDonnees();
		service.addObserver(this);
		///rech_sta();
	}

	private void initialiseDonnees() {
		// charge les serveurs de la BD
		List<Stationnement> sta = service.getListeSta();
		// Transforme la liste en une liste observable
		loSta = FXCollections.observableList(sta);
		// Donne la liste observable à la TableView
		tblSta.setItems(loSta);	
		rech_sta();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle bundle) {
		//this.bundle = bundle;
		
		col_immatr.setCellValueFactory(new PropertyValueFactory<Stationnement, String>("personne"));
		col_place.setCellValueFactory(new PropertyValueFactory<Stationnement, String>("place"));
		col_mom.setCellValueFactory(new PropertyValueFactory<Stationnement, LocalDateTime>("MomentA"));
//
//		tblSta.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
	}
	/*
	* Barre de recherche voitures stationnée (main)
	*/
	@FXML
	public void rech_sta() {			
//		col_immatr.setCellValueFactory(new PropertyValueFactory<Stationnement, String>("personne"));
//		col_place.setCellValueFactory(new PropertyValueFactory<Stationnement, String>("place"));
//		col_mom.setCellValueFactory(new PropertyValueFactory<Stationnement, LocalDateTime>("MomentA"));
		
		FilteredList<Stationnement> stat = new FilteredList(loSta, s -> true);//Envoie les données dans le filtre
        tblSta.setItems(stat);

        //ComboBox + recherche
        choiceBox.getItems().add("Immatriculation");
        choiceBox.getItems().add("Place");
        choiceBox.setValue("Immatriculation");

        zRech.setPromptText("Recherche");
        zRech.setOnKeyReleased(r -> {
            switch (choiceBox.getValue()){
            	// Filtre en fonction de l'immatr
                case "Immatriculation":
                    stat.setPredicate(s -> s.getPersonne().getImmatr().toLowerCase().contains(zRech.getText().toLowerCase().trim()));
                    break;
                    // Filtre en fonction de la place
                case "Place":
                    stat.setPredicate(s -> s.getPlace().toLowerCase().contains(zRech.getText().toLowerCase().trim()));//filter table by first name
                    break;
            }
        });

        // reset de la table et bare de recherche vide quand aucune données n'est entrées
        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
            {
                zRech.setText("");
                stat.setPredicate(null);
            }
        });
		
	}
	/****************** GESTION PUBLICATION ********************/

//Gestion des publications
	@Override
	public void onSubscribe(Subscription subscription) {
		this.subscription = subscription;
		subscription.request(10);
		logger.info("Je suis un écouteur de STATIONNEMENT");
	}

//Reception des messages
	@Override
	public void onNext(Message<Stationnement> item) {
		logger.info(item.getOp() + " sur " + item.getElement());
		switch (item.getOp()) {
		case INSERT:
			Platform.runLater(()->loSta.add(item.getElement()));
			break;
		case DELETE:
			Platform.runLater(()->loSta.remove(item.getElement()));
			break;
		default:
			break;
		}
		//Platform.runLater(()->ztCpt.setText(Integer.toString(loSta.size())));
		subscription.request(1);

	}
	
	@Override
	public void onError(Throwable throwable) {
		logger.error("erreur d'abonnement aux modifications de stationnement");

	}

	@Override
	public void onComplete() {
		logger.info(" écouteur de personne On Complete");
	}

//Permet d'avoir la "Subscription pour se désabonner"
	public Subscription getSubscription() {
		return subscription;
	}

}
