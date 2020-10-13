package view.listePlace;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.ParkingException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import model.Personnel;
import model.Place;
import services.IServicePersonnel;
import services.IServicePlace;
import services.Message;

public class VueListePlaceController implements Initializable, Subscriber<Message<Place>> {
	// Logger
	static Logger logger = LoggerFactory.getLogger(VueListePlaceController.class);
	// accès au bundle
	private ResourceBundle bundle;
	// Composants de la vue FXML
    @FXML
    private TableView<Place> tblPlace;

    @FXML
    private TableColumn<Place, String> col_Code;

    @FXML
    private TableColumn<Place, Integer> col_Taille;

    @FXML
    private TableColumn<Place, Boolean> col_Etat;

    @FXML
    private TextField ztCpt;

    @FXML
    private Button btSuppression;

    @FXML
    private RadioButton rbSelection;
    
	private IServicePlace service;
	private ObservableList<Place> loPlace;

	// Observer qui sera initialisé par OnSubscribe
	private Subscription subscription;

	/*
	 * Permet d'envoyer l'accès à la couche de service
	 */
	public void setUp(IServicePlace service) {
		this.service = service;
		initialiseDonnees();
		service.addObserver(this);
	}

	private void initialiseDonnees() {
		// charge les serveurs de la BD
		List<Place> place = service.getListePlace();
		// Transforme la liste en une liste observable
		loPlace = FXCollections.observableList(place);
		// Donne la liste observable à la TableView
		tblPlace.setItems(loPlace);
		// Ajuste la zone de texte avec la taille
		ztCpt.setText(Integer.toString(place.size()));

//		tblPlace.setOnMouseClicked(e -> {
//
//		});

	}

	@Override
	public void initialize(URL arg0, ResourceBundle bundle) {
		this.bundle = bundle;
		// spécifie la correspondance des colonnes avec les attributs des serveurs
		col_Code.setCellValueFactory(new PropertyValueFactory<Place, String>("code"));
		col_Taille.setCellValueFactory(new PropertyValueFactory<Place, Integer>("taille"));
		col_Etat.setCellValueFactory(new PropertyValueFactory<Place, Boolean>("libre"));
	
		// PErmet une sélection multiple
		tblPlace.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		//active ou non la selection
		btSuppression.disableProperty().bind(rbSelection.selectedProperty().not());
		// Evènement sur delete
		btSuppression.setOnAction(a -> {
			ObservableList<Place> liste = tblPlace.getSelectionModel().getSelectedItems();
			Alert alert = new Alert(AlertType.CONFIRMATION, bundle.getString("listePl.confirmSuppr"));
			if (alert.showAndWait().get() == ButtonType.OK)
				liste.forEach(

						e -> {
							try {
								service.delete(e.getCode());
							} catch (ParkingException e1) {
								logger.error("Impossible de supprimer la voiture "+e.getCode());
							}
						});
			;
		});		
	}
	
	/****************** GESTION PUBLICATION ********************/

//Gestion des publications
	@Override
	public void onSubscribe(Subscription subscription) {
		this.subscription = subscription;
		subscription.request(10);
		logger.info("Je suis un écouteur de personne");
	}

//Reception des messages
	@Override
	public void onNext(Message<Place> item) {
		logger.info(item.getOp() + " sur " + item.getElement());
		switch (item.getOp()) {
		case INSERT:
		Platform.runLater(()->loPlace.add(item.getElement()));
			break;
		case DELETE:
			Platform.runLater(()->loPlace.remove(item.getElement()));
			break;
		default:
			break;
		}
		Platform.runLater(()->ztCpt.setText(Integer.toString(loPlace.size())));
		subscription.request(1);

	}

	@Override
	public void onError(Throwable throwable) {
		logger.error("erreur d'abonnement aux modifications de personne");

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
