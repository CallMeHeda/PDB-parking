package controller;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;


import dao.DAOFactory;
import dao.DAOFactory.TypePersistance;
import database.connexion.ConnexionFromFile;
import database.connexion.ConnexionSingleton;
import database.connexion.PersistanceException;
import database.uri.Databases;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.IServiceArrivee;
import services.IServiceHistorique;
import services.IServicePersonnel;
import services.IServicePlace;
import services.ServiceArrivee;
import services.ServiceHistorique;
import services.ServicePersonnel;
import services.ServicePlace;
import view.historique.VueHistoriqueController;
import view.listePersonnel.VueListePersonnelController;
import view.listePlace.VueListePlaceController;
import view.personnel.VuePersonnelController;
import view.place.VuePlaceController;
import view.stationnement.VueStationnnementController;

public class ParkingCtrl extends Application {

	// Locale par défaut de l'application
	private final Locale localFR = new Locale("fr", "BE");
	private final Locale localEN = new Locale("en", "EN");
	// Vues
	private ResourceBundle bundle;
	private Stage mainStage;

	@FXML
	private GridPane paneSta;

	/********** Services ************/
	private IServiceArrivee serviceBorneIn;
	//private IServiceSortie serviceBorneOut;
	private IServicePersonnel servicePersonnel;
	private IServiceHistorique serviceHistorique;
	private IServicePlace servicePlace;
	//private IServiceArrivee serviceSta;
	/********** Vues ****************/
	// vue des personnes
	private Stage vuePersonnel = null; // ajout
	private Stage vueListePersonnel = null; // liste et suppression
	private Stage vueHistorique = null;
	private Stage vuePlace = null;
	private Stage vueListePlace = null;


	private DAOFactory factory;

	// Elements pour la vue stationnement
	private final PseudoClass busyClass = PseudoClass.getPseudoClass("busy");

	@Override
	public void start(Stage primaryStage) {
		// Choix de la local par défaut
		Locale.setDefault(localFR);
		// Locale.setDefault(localEN);

		// Crée la connexion à la bd et renvoie la factory
		factory = createConnexion("./resources/connexionParking_Test.properties", Databases.FIREBIRD);
		// couche)
		serviceBorneIn = new ServiceArrivee(factory);
		// couche de service pour géré le personnel
		servicePersonnel = new ServicePersonnel(factory.getPersonnelDAO());
		// Couche pour l'historique
		serviceHistorique = new ServiceHistorique(factory);
		// Couche pour la place
		servicePlace = new ServicePlace(factory.getPlaceDAO());
		// Couche stationnement

		// Mémorise la stage principale
		mainStage = primaryStage;

		// Construction du conteneur principale de la mainstage
		BorderPane cp = new BorderPane();
		
		Label nomPark = new Label("PARKING DE BRUXELLES");
		nomPark.setId("nomPark");
		nomPark.prefWidthProperty().bind(cp.widthProperty());
		nomPark.setAlignment(Pos.CENTER);		
		cp.setTop(nomPark);

		// Conteneur des boutons (gauche)
		TilePane paneBoutons = new TilePane();

		Button btBorneIn = new Button("Borne D'Entrée");
		btBorneIn.setId("bouton_menu");
		btBorneIn.setOnAction(a -> {
			showBorneEntree(serviceBorneIn);
		});

		Button btBorneOut = new Button("Borne De Sortie");
		btBorneOut.setId("bouton_menu");
		btBorneOut.setOnAction(a -> {
			showBorneSortie(serviceBorneIn);
		});

		Button btVuePersonnel = new Button("Nouvelle Personne");
		Tooltip toolP = new Tooltip("Ajouter Une Nouvelle Personne");
		Tooltip.install(btVuePersonnel, toolP);
		btVuePersonnel.setId("bouton_menu");
		btVuePersonnel.setOnAction(a -> {
			showVuePersonnel(servicePersonnel);
		});

		Button btListePersonnel = new Button("Liste des Personnes");
		Tooltip toolVP = new Tooltip("Consulter La Liste des Personnes");
		Tooltip.install(btListePersonnel, toolVP);
		btListePersonnel.setId("bouton_menu");
		btListePersonnel.setOnAction(a -> {
			showVueListePersonnel(servicePersonnel);
		});

		Button btHisto = new Button("Historique");
		btHisto.setId("bouton_menu");
		btHisto.setOnAction(a -> {
			showVueHistorique(serviceHistorique);
		});

		Button btPlaceM = new Button("Ajouter Une Place");
		btPlaceM.setId("bouton_menu");
		btPlaceM.setOnAction(a -> {
			showVuePlace(servicePlace);
		});

		Button btListePlace = new Button("Liste Des Places");
		Tooltip toolPl = new Tooltip("Consulter La Liste des Places");
		Tooltip.install(btListePlace, toolPl);
		btListePlace.setId("bouton_menu");
		btListePlace.setOnAction(a -> {
			showVueListePlace(servicePlace);
		});

		// ajout des boutons au conteneur TilePane
		paneBoutons.getChildren().addAll(btBorneIn, btBorneOut, btVuePersonnel, btListePersonnel,
				btPlaceM, btListePlace, btHisto);

		paneBoutons.setHgap(20);
		paneBoutons.setVgap(20);


		// ajoute le conteneur de boutons sur le coté gauche du conteneur principal
		cp.setLeft(paneBoutons);
		
		//paneSta.setStyle("-fx-alignement: center;");
//		paneSta.prefHeightProperty().bind(cp.heightProperty());
//		paneSta.prefWidthProperty().bind(cp.widthProperty());
		//cp.setScene(new Scene(paneSta,300,300));
		
		cp.setCenter(showVueStationnement(paneSta));
		

		// Création de la scène
		Scene scene = new Scene(cp, 900, 600);
		// ajout du fichier css
		scene.getStylesheets().add("./view/css/parking.css");
		// Ajoute la scene à la primaryStage
		primaryStage.setScene(scene);

		// Affiche la vue
		primaryStage.show();

	}

	/****************************************************************************************************************/
	private Pane showVueStationnement(Pane pane) {
		ResourceBundle bundle;
		// Charger la vue
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/stationnement/VueStationnnement.fxml"));
		try {
			// charge le fichier i18n
			bundle = ResourceBundle.getBundle("view.stationnement.bundle.VueStationnement");
			loader.setResources(bundle);
			// Obtenir la traduction du titre dans la locale
		} catch (Exception e) {
			showErreur(e.getMessage());// vue minimaliste pour afficher les erreurs
		}
		//Pane root;
		try {
			// Stock la vue dans un pane
			//root = loader.load();
			pane = loader.load();
			VueStationnnementController ctrl = loader.getController();
			ctrl.setUp(serviceBorneIn);
			
			
			pane.getStylesheets().add("./view/css/parking.css");
			pane.setStyle("-fx-padding: 10 200 0 100;");

			//pane = root;

			//mainStage.show();
		} catch (IOException e) {
			showErreur("ERREUR STATIONNEMENT: " + e.getMessage());
//			root = null;
			pane = null;
		}
		
		
		return pane;
	}

	/*****************************************************************************************************************/

	/******************************************************************************************************************/

	/**
	 * Ouvre une vue avec une liste des voitures du personnel cette liste est à
	 * l'écoute des changements Cette vue sera créée lors du première appel. Lors
	 * d'une fermeture (hide), son adresse sera maintenue pour une prochaine
	 * ouverture et ceci jusqu'à la fin de l'application
	 * 
	 * @param servicePersonnel l'accès aux données
	 */
	private void showVueListePersonnel(IServicePersonnel servicePersonnel) {
		// vérifie si la vue existe déjà
		if (vueListePersonnel != null)
			vueListePersonnel.show();
		else {
			// Gestion i18n
			ResourceBundle bundle;
			// Crée une stage (fenêtre pour la vue)
			Stage stage = new Stage();
			stage.initOwner(this.mainStage); // son parent
			// position lors de l'ouverture ppr à l'écran
			stage.setX(100);
			stage.setY(200);

			// Crée un loader pour charger la vue FXML
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/listePersonnel/VueListePersonnel.fxml"));
			try {
				// charge le fichier i18n
				bundle = ResourceBundle.getBundle("view.listePersonnel.bundle.VueListePersonnel");
				loader.setResources(bundle);
				// Obtenir la traduction du titre dans la locale
				stage.setTitle(bundle.getString("listePer.titre"));
			} catch (Exception e) {
				showErreur(e.getMessage());// vue minimaliste pour afficher les erreurs
				stage.setTitle("Vue Liste Personnel");
			}
			// Charge la vue à partir du Loader
			// et initialise son contenu en appelant la méthode setUp du controleur
			AnchorPane root;
			try {
				// charge la vue (le conteneur)
				root = loader.load();
				// récupère le ctrl (après l'initialisation)
				VueListePersonnelController ctrl = loader.getController();
				// fourni la couche de service au ctrl pour charger les voitures
				ctrl.setUp(servicePersonnel);
				// charge le Pane dans une scene
				Scene scene = new Scene(root, 400, 400);
				// attribue un fichier css à la vue
				scene.getStylesheets().add("./view/css/parking.css");
				// ajoute la vue à la scène et affiche la vue non modale
				stage.setScene(scene);
				stage.show();
			} catch (IOException e) {
				showErreur("Impossible de charger la liste du personnel: " + e.getMessage());
				stage = null;
			}
			vueListePersonnel = stage;
		}

	}

	/**
	 * Méthode qui ouvre une fenêtre de dialogue pour ajouter/modifier une personne
	 * si la vue n'existe pas encore elle va la créée
	 * 
	 * @param servicePersonnel
	 */
	private void showVuePersonnel(IServicePersonnel servicePersonnel) {
		// vérifie si la vue existe déjà
		if (vuePersonnel != null)
			// affiche la vue modale
			vuePersonnel.showAndWait();
		else {
			ResourceBundle bundle;
			// Crée une stage
			Stage stage = new Stage();
			stage.initOwner(this.mainStage);
			// rend la fenêtre modale (Windows_modal ou si on veut Application_modal)
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setX(150);
			stage.setY(100);

			// Crée un loader pour charger la vue FXML
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/personnel/VuePersonnel.fxml"));
			try {
				// charge le fichier i18n
				bundle = ResourceBundle.getBundle("view.personnel.bundle.VuePersonnel");
				loader.setResources(bundle);
				// Obtenir la traduction du titre dans la locale
				stage.setTitle(bundle.getString("vueP.titre"));
			} catch (Exception e) {
				showErreur(e.getMessage());
				stage.setTitle("Vue Personnel");
			}
			// Charge la vue à partir du Loader
			// et initialise son contenu en appelant la méthode setUp du controleur
			BorderPane root;
			try {
				root = loader.load();
				// récupère le ctrl (après l'initialisation)
				VuePersonnelController ctrl = loader.getController();
				// fourni l'accès à la couche de service
				ctrl.setUp(servicePersonnel);
				// charge le Pane dans la Stage
				Scene scene = new Scene(root, 400, 400);
				// ajoute un fichier css à la vue
				scene.getStylesheets().add("./view/css/parking.css");
				stage.setScene(scene);
				// bloque le processus car modale
				stage.showAndWait();
			} catch (IOException e) {
				showErreur("Impossible de charger la vue personnel: " + e.getMessage());
				stage = null;
			}
			vuePersonnel = stage;
		}
	}

	/**
	 * Méthode qui ouvre une fenêtre de dialogue pour ajouter/modifier une place si
	 * la vue n'existe pas encore elle va la créée
	 * 
	 * @param servicePlace
	 */
	private void showVuePlace(IServicePlace servicePlace) {
		// vérifie si la vue existe déjà
		if (vuePlace != null)
			// affiche la vue modale
			vuePlace.showAndWait();
		else {
			ResourceBundle bundle;
			// Crée une stage
			Stage stage = new Stage();
			stage.initOwner(this.mainStage);
			// rend la fenêtre modale (Windows_modal ou si on veut Application_modal)
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setX(150);
			stage.setY(100);

			// Crée un loader pour charger la vue FXML
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/place/VuePlace.fxml"));
			try {
				// charge le fichier i18n
				bundle = ResourceBundle.getBundle("view.listePlace.bundle.VueListePlace");
				loader.setResources(bundle);
				// Obtenir la traduction du titre dans la locale
				// stage.setTitle(bundle.getString("listePl.titre"));
			} catch (Exception e) {
				showErreur(e.getMessage());
				stage.setTitle("Vue Place");
			}
			// Charge la vue à partir du Loader
			// et initialise son contenu en appelant la méthode setUp du controleur
			BorderPane root;
			try {
				root = loader.load();
				// récupère le ctrl (après l'initialisation)
				VuePlaceController ctrl = loader.getController();
				// fourni l'accès à la couche de service
				ctrl.setUp(servicePlace);
				// charge le Pane dans la Stage
				Scene scene = new Scene(root, 400, 400);
				// ajoute un fichier css à la vue
				scene.getStylesheets().add("./view/css/parking.css");
				stage.setScene(scene);
				// bloque le processus car modale
				stage.showAndWait();
			} catch (IOException e) {
				showErreur("Impossible de charger la vue place: " + e.getMessage());
				stage = null;
			}
			vuePlace = stage;
		}
	}

	private void showVueListePlace(IServicePlace servicePlace) {
		// vérifie si la vue existe déjà
		if (vueListePlace != null)
			vueListePlace.show();
		else {
			// Gestion i18n
			ResourceBundle bundle;
			// Crée une stage (fenêtre pour la vue)
			Stage stage = new Stage();
			stage.initOwner(this.mainStage); // son parent
			// position lors de l'ouverture ppr à l'écran
			stage.setX(100);
			stage.setY(200);

			// Crée un loader pour charger la vue FXML
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/listePlace/VueListePlace.fxml"));
			try {
				// charge le fichier i18n
				bundle = ResourceBundle.getBundle("view.listePlace.bundle.VueListePlace");
				loader.setResources(bundle);
				// Obtenir la traduction du titre dans la locale
				stage.setTitle(bundle.getString("listePl.titre"));
			} catch (Exception e) {
				showErreur(e.getMessage());// vue minimaliste pour afficher les erreurs
				stage.setTitle("Vue Liste Place");
			}
			// Charge la vue à partir du Loader
			// et initialise son contenu en appelant la méthode setUp du controleur
			AnchorPane root;
			try {
				// charge la vue (le conteneur)
				root = loader.load();
				// récupère le ctrl (après l'initialisation)
				VueListePlaceController ctrl = loader.getController();
				// fourni la couche de service au ctrl pour charger les voitures
				ctrl.setUp(servicePlace);
				// charge le Pane dans une scene
				Scene scene = new Scene(root, 400, 400);
				// attribue un fichier css à la vue
				scene.getStylesheets().add("./view/css/parking.css");
				// ajoute la vue à la scène et affiche la vue non modale
				stage.setScene(scene);
				stage.show();
			} catch (IOException e) {
				showErreur("Impossible de charger la liste des places: " + e.getMessage());
				stage = null;
			}
			vueListePlace = stage;
		}

	}

	/**
	 * Méthode qui va créer la connexion et renvoyer la factory
	 * 
	 * @param filename le fichier de configuration pour la BD
	 * @param le       type de BD
	 * @return la farique créée // en cas de problème sortie de l'application
	 */
	private DAOFactory createConnexion(String filename, Databases db) {
		DAOFactory factory = null;
		try {
			ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(filename, db));
			// Réinitialise la base de données dans son état initial
			factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		} catch (PersistanceException e) {
			showErreur(e.getMessage());
			Platform.exit();
		}
		return factory;
	}

	/**
	 * Ouvre une nouvelle borne d'entrée
	 * 
	 * @param service l'accès à la couche de service pour les in/out de voitures
	 */
	private void showBorneEntree(IServiceArrivee service) {

		// Créé une fenêtre avec une vue pour une borne d'entrée
		Stage stage = new Stage();
		stage.initOwner(this.mainStage);
		stage.setX(100);
		stage.setY(50);
		// fichier i18n
		ResourceBundle bundle = ResourceBundle.getBundle("view.bundles.BorneEntree");
		// Renvoie le pane (conteneur) pour une borne d'entrée
		BorneEntreeCtrl pane = new BorneEntreeCtrl(service, bundle);
		// S'enregistre pour être avertit des évènements de chgmt d'état de place
		service.addEtatPlaceChangeListener(pane);
		// scène, css
		Scene scene = new Scene(pane, 350, 300);
		scene.getStylesheets().add("./view/css/parking.css");
		stage.setTitle((bundle.getString("titre")));
		stage.setScene(scene);
		// se désenregistre comme écouteur d'évènements lors de la fermeture
		stage.setOnCloseRequest(e -> service.removeEtatPlaceChangeListener(pane));
		// affiche la fenêtre
		stage.show();
	}

	private void showBorneSortie(IServiceArrivee serviceSortie) {

		// Créé une fenêtre avec une vue pour une borne d'entrée
		Stage stage = new Stage();
		stage.initOwner(this.mainStage);
		stage.setX(100);
		stage.setY(50);
		// fichier i18n
		ResourceBundle bundle = ResourceBundle.getBundle("view.bundles.BorneSortie");
		// Renvoie le pane (conteneur) pour une borne de sortie
		BorneSortieCtrl pane = new BorneSortieCtrl(serviceBorneIn, bundle);
		// S'enregistre pour être avertit des évènements de chgmt d'état de place
		//serviceSortie.removeEtatPlaceChangeListener(pane);
		// scène, css
		Scene scene = new Scene(pane, 350, 300);
		scene.getStylesheets().add("./view/css/parking.css");
		stage.setTitle((bundle.getString("titre")));
		stage.setScene(scene);
		// se désenregistre comme écouteur d'évènements lors de la fermeture
		stage.setOnCloseRequest(e -> serviceSortie.removeEtatPlaceChangeListener(pane));
		// affiche la fenêtre
		stage.show();

	}

	private void showVueHistorique(IServiceHistorique serviceHistorique) {
		// vérifie si la vue existe déjà
		if (vueHistorique != null)
			vueHistorique.show();
		else {
			// Gestion i18n
			ResourceBundle bundle;
			// Crée une stage (fenêtre pour la vue)
			Stage stage = new Stage();
			stage.initOwner(this.mainStage); // son parent
			// position lors de l'ouverture ppr à l'écran
			stage.setX(100);
			stage.setY(200);

			// Crée un loader pour charger la vue FXML
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/historique/VueHistorique.fxml"));
			try {
				// charge le fichier i18n
				bundle = ResourceBundle.getBundle("view.historique.bundle.VueHistorique");
				loader.setResources(bundle);
				// Obtenir la traduction du titre dans la locale
				stage.setTitle(bundle.getString("histo.titre"));
			} catch (Exception e) {
				showErreur(e.getMessage());// vue minimaliste pour afficher les erreurs
				stage.setTitle("Vue Liste Historique");
			}
			// Charge la vue à partir du Loader
			// et initialise son contenu en appelant la méthode setUp du controleur
			AnchorPane root;
			try {
				// charge la vue (le conteneur)
				root = loader.load();
				// récupère le ctrl (après l'initialisation)
				VueHistoriqueController ctrl = loader.getController();
				// fourni la couche de service au ctrl pour charger les voitures
				ctrl.setUp(serviceHistorique);
				// charge le Pane dans une scene
				Scene scene = new Scene(root, 400, 400);
				// attribue un fichier css à la vue
				scene.getStylesheets().add("./view/css/parking.css");
				// ajoute la vue à la scène et affiche la vue non modale
				stage.setScene(scene);
				stage.show();
			} catch (IOException e) {
				showErreur("Impossible de charger l'Historique: " + e.getMessage());
				stage = null;
			}
			vueHistorique = stage;
		}

	}

	/**
	 * Vue basique pour afficher les messages d'erreur
	 * 
	 * @param message
	 */
	static private void showErreur(String message) {
		System.out.println(message);
		Alert a = new Alert(AlertType.ERROR, message);
		a.showAndWait();
	}

	/**
	 * Démarrage de l'application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
