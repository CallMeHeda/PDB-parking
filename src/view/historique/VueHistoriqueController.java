package view.historique;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.Message;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Historique;
import model.Personnel;
import model.SortieVoiture;
import model.Stationnement;
import services.IServiceHistorique;
import view.listePersonnel.VueListePersonnelController;

public class VueHistoriqueController implements Initializable, Subscriber<Message<Historique>>{
	
	static Logger logger = LoggerFactory.getLogger(VueHistoriqueController.class);

	    @FXML
	    private TableView<Historique> tableHisto;

	    @FXML
	    private TableColumn<Historique, String> col_immatr;

	    @FXML
	    private TableColumn<Historique, LocalDateTime> col_momentA;

	    @FXML
	    private TableColumn<Historique, String> col_place;
	    

	    @FXML
	    private TableColumn<Historique, LocalDateTime> col_momS;
	    
	    @FXML
	    private TextField zRech;

	    @FXML
	    private ComboBox<String> combo;

	    @FXML
	    private DatePicker rechDate;

		private IServiceHistorique service;


		private ObservableList<Historique> loHistorique;
		//private ObservableList<Historique> loSort;
		
		@FXML
		private TextField ztCpt;

		private Subscription subscription;
		
	    /*
		 * Permet d'envoyer l'accès à al couche de service
		 */
		public void setUp(IServiceHistorique serviceHisto) {
			this.service = serviceHisto;
			initialiseDonnees();
			serviceHisto.addObserver(this);
		}

		private void initialiseDonnees() {
			// charge les serveurs de la BD
			List<Historique> liste = service.getListeHistorique();
			// Transforme la liste en une liste observable
			loHistorique = FXCollections.observableList(liste);			
			// Donne la liste observable à la TableView
			tableHisto.setItems(loHistorique);
			// Ajuste la zone de texte avec la taille
			//ztCpt.setText(Integer.toString(histo.size()));
			rech_sta();
		}

		@Override
		public void initialize(URL url, ResourceBundle bundle) {
			//this.bundle = bundle;
			// spécifie la correspondance des colonnes avec les attributs des serveurs
			col_immatr.setCellValueFactory(new PropertyValueFactory<Historique, String>("FKPersonne"));
			col_momentA.setCellValueFactory(new PropertyValueFactory<Historique, LocalDateTime>("MomentA"));
			col_place.setCellValueFactory(new PropertyValueFactory<Historique, String>("FKPlace"));
			col_momS.setCellValueFactory(new PropertyValueFactory<Historique, LocalDateTime>("MomentS"));			
		}
		
		@FXML
		public void rech_sta() {			
			
			
			FilteredList<Historique> histo = new FilteredList(loHistorique, h -> true);//Envoie les données dans le filtre
	        tableHisto.setItems(histo);

	        //ComboBox + recherche
	        combo.getItems().add("Immatriculation");
	        combo.getItems().add("Place");
	        combo.setValue("Immatriculation");

	        zRech.setPromptText("Recherche");
	        zRech.setOnKeyReleased(r -> {
	            switch (combo.getValue()){
	            	// Filtre en fonction de l'immatr
	                case "Immatriculation":
	                    histo.setPredicate(h -> h.getFKPersonne().toLowerCase().contains(zRech.getText().toLowerCase().trim()));
	                    
	                    //VERSION AVEC LA METHODE HISTO_VOIT CREER DANS SQLHISTORIQUE
//	                    List<Historique> liste = service.histo_voit(zRech.getText());
//	        			loHistorique = FXCollections.observableList(liste);			
//	        			tableHisto.setItems(loHistorique);
	                    
	                    break;
	                     //Filtre en fonction de la place
	                case "Place":
	                    histo.setPredicate(s -> s.getFKPlace().toLowerCase().contains(zRech.getText().toLowerCase().trim()));//filter table by first name
	                    break;
	            }
	        });

	        // reset de la table et bare de recherche vide quand aucune données n'est entrées
	        combo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
	            if (newVal != null)
	            {
	                zRech.setText("");
	                histo.setPredicate(null);
	            }
	        });
		}
	    
	   /****************************************************************/
		
		//Gestion des publications
		@Override
		public void onSubscribe(Subscription subscription) {
			this.subscription = subscription;
			subscription.request(10);
			logger.info("Je suis un écouteur de histo");
		}

	//Reception des messages
		@Override
		public void onNext(Message<Historique> item) {
			logger.info(item.getOp() + " sur " + item.getElement());
			switch (item.getOp()) {
			case INSERT:
			Platform.runLater(()->loHistorique.add(item.getElement()));
				break;
			case DELETE:
				Platform.runLater(()->loHistorique.remove(item.getElement()));
				break;
			default:
				break;
			}
			Platform.runLater(()->ztCpt.setText(Integer.toString(loHistorique.size())));
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
//
//		@Override
//		public void propertyChange(PropertyChangeEvent evt) {
//			System.out.println(evt.getSource());
//			//Button bt=boutons.get(evt.getPropertyName());
//			//if (bt!=null && evt.getNewValue() instanceof Boolean) bt.pseudoClassStateChanged(busyClass, !(Boolean)evt.getNewValue());
//			;
//		}


}
