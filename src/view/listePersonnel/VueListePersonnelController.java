package view.listePersonnel;

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
import services.Message;

public class VueListePersonnelController implements Initializable, Subscriber<Message<Personnel>> {
	// Logger
	static Logger logger = LoggerFactory.getLogger(VueListePersonnelController.class);
	// accès au bundle
	private ResourceBundle bundle;
	// Composants de la vue FXML
	@FXML
	private TableView<Personnel> tblPersonnel;
	@FXML
	private TableColumn<Personnel, String> col_Immatr;
	@FXML
	private TableColumn<Personnel, String> col_Nom;
	@FXML
	private TableColumn<Personnel, String> col_Prenom;
	@FXML
	private TableColumn<Personnel, String> col_Email;
	@FXML
	private TextField ztCpt;
	@FXML
	private RadioButton rbSelection;
	@FXML
	private Button btSuppression;
	
	@FXML
	private Button btMod;
	//private boolean edit=false,add=false;

	private IServicePersonnel service;
	private ObservableList<Personnel> loPersonnel;

	// Observer qui sera initialisé par OnSubscribe
	private Subscription subscription;

	/*
	 * Permet d'envoyer l'accès à la couche de service
	 */
	public void setUp(IServicePersonnel service) {
		this.service = service;
		initialiseDonnees();
		service.addObserver(this);
	}

	private void initialiseDonnees() {
		// charge les serveurs de la BD
		List<Personnel> personnel = service.getListePersonne();
		// Transforme la liste en une liste observable
		loPersonnel = FXCollections.observableList(personnel);
		// Donne la liste observable à la TableView
		tblPersonnel.setItems(loPersonnel);
		// Ajuste la zone de texte avec la taille
		ztCpt.setText(Integer.toString(personnel.size()));

		tblPersonnel.setOnMouseClicked(e -> {
			
		});

	}

	@Override
	public void initialize(URL arg0, ResourceBundle bundle) {
		this.bundle = bundle;
		// spécifie la correspondance des colonnes avec les attributs des serveurs
		col_Immatr.setCellValueFactory(new PropertyValueFactory<Personnel, String>("immatr"));
		col_Nom.setCellValueFactory(new PropertyValueFactory<Personnel, String>("nom"));
		col_Prenom.setCellValueFactory(new PropertyValueFactory<Personnel, String>("prenom"));
		col_Email.setCellValueFactory(new PropertyValueFactory<Personnel, String>("email"));
		// PErmet une sélection multiple
		tblPersonnel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		//active ou non la selection
		btSuppression.disableProperty().bind(rbSelection.selectedProperty().not());
		// Evènement sur delete
		btSuppression.setOnAction(a -> {
			ObservableList<Personnel> liste = tblPersonnel.getSelectionModel().getSelectedItems();
			Alert alert = new Alert(AlertType.CONFIRMATION, bundle.getString("listePer.confirmSuppression"));
			if (alert.showAndWait().get() == ButtonType.OK)
				liste.forEach(

						e -> {
							try {
								service.delete(e.getImmatr());
							} catch (ParkingException e1) {
								logger.error("Impossible de supprimer la voiture "+e.getImmatr());
							}
						});
			;
		});

		tblPersonnel.setEditable(true);
		col_Nom.setCellFactory(TextFieldTableCell.forTableColumn());
		col_Prenom.setCellFactory(TextFieldTableCell.forTableColumn());
		col_Email.setCellFactory(TextFieldTableCell.forTableColumn());
		btMod.disableProperty().bind(rbSelection.selectedProperty().not());
		btMod.setOnAction(a ->{
			// Evènement sur modification
				Personnel persAMod = tblPersonnel.getSelectionModel().getSelectedItem();
								try {
									service.update(persAMod);
								}  catch (Exception e) {
									e.printStackTrace();
								}
							});
	}
	/**
	 * Modification d'une personne
	 * @param modPl
	 * @throws Exception 
	 */
	public void modPersonne(CellEditEvent modPers) throws Exception {
		
		Personnel persAMod = tblPersonnel.getSelectionModel().getSelectedItem();
		//service.update(persAMod);
		persAMod.setNom(modPers.getNewValue().toString());
	
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
	public void onNext(Message<Personnel> item) {
		logger.info(item.getOp() + " sur " + item.getElement());
		switch (item.getOp()) {
		case INSERT:
		Platform.runLater(()->loPersonnel.add(item.getElement()));
			break;
		case DELETE:
			Platform.runLater(()->loPersonnel.remove(item.getElement()));
			break;
		default:
			break;
		}
		Platform.runLater(()->ztCpt.setText(Integer.toString(loPersonnel.size())));
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
