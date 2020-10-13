package view.place;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import dao.ParkingConstraintException;
import dao.ParkingException;
import dao.ParkingFullException;
import dao.ParkingPKException;
import dao.ParkingPlaceBusyException;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import model.Personnel;
import model.Place;
import services.IServicePersonnel;
import services.IServicePlace;
import view.statusBar.StatusBar.TypeMessage;

public class VuePlaceController implements Initializable {

	private final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
	private final PseudoClass activeClass = PseudoClass.getPseudoClass("activeBtn");

	private IServicePlace service;
//La personne à créer
	private Place p;

	@FXML
	private TextField codeP;
	
	@FXML
	private Spinner<Integer> tailleP;
	
	@FXML
	private RadioButton etatLibre;
	
	@FXML
	private RadioButton etatP2;

	@FXML
	private Button btOk;

	@FXML
	private Button btCancel;

	private ResourceBundle bundle;
	private ToggleGroup radioBtEtat;
	
	//final Spinner<Integer> spinner = new Spinner<Integer>();
	
	//final int initialValue = 5;
	
	

	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		this.bundle = bundle;
	
		// Initialise taille de la place
		SpinnerValueFactory<Integer> taillePlvalueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 5);
		this.tailleP.setValueFactory(taillePlvalueFactory);
		
		// Initialise l'etat de place
		radioBtEtat = new ToggleGroup();
		this.etatLibre.setToggleGroup(radioBtEtat);
		this.etatP2.setToggleGroup(radioBtEtat);
	}
	
//	public void radioButtonChanged() {
//		if(this.radioBtEtat.getSelectedToggle().equals(this.etatLibre)) {
//			
//		}
//	}
	
	private String showErreur(ParkingException e) {
		if (e instanceof ParkingConstraintException)
			return bundle.getString("listePl.erreurPl");
//		if (e instanceof ParkingPlaceBusyException)
//			return bundle.getString("PlaceBusy");
		if (e instanceof ParkingPKException)
			return bundle.getString("listePl.erreurPl");
//		if (e instanceof ParkingException)
//			return bundle.getString("Unknown");
		return "ERROR X";
	}
	
	@FXML
	void valider(ActionEvent event) {
		//this.bundle = bundle;
		p = new Place(codeP.getText(), tailleP.getValueFactory().getValue(), radioBtEtat.getSelectedToggle().isSelected());

		try {// Remet la pseudoclass des éléments error à faux
			resetPseudoClass();
			
			if(this.radioBtEtat.getSelectedToggle().equals(this.etatLibre)) {
				p.setLibre(true);
			}else {
				p.setLibre(false);
			}
			// Msg d'erreur si le champ et vide ou incorrecte
			if(!codeP.getText().matches("[A-Z][0-9]{2}")){
				codeP.setPromptText(p.getCode() + " : Mauvaise synthaxe ! Ex -> P01");
				if(codeP.getText().isEmpty()) {
					codeP.setPromptText("Veuillez entrer une place SVP");
			}
		}else {
			service.insert(p);
			codeP.setPromptText("");
		}
	
			//}
			// reset les zones de texte car ici tout c'est bien passé
			resetFields();

			// Récupère la fenêtre pour la fermer
			//((Button) event.getSource()).getParent().getScene().getWindow().hide();

		} catch (ParkingException pl) {
			// Ajuste la pseudo classe pour chaque élément en fonction de l'exception
//			if (pl instanceof ParkingPKException)
//				codeP.pseudoClassStateChanged(errorClass, true);
//			else if (pl instanceof ParkingConstraintException)
//				switch (((ParkingConstraintException) pl).getChamp()) {
//				case "CODE_PLA":
//					codeP.pseudoClassStateChanged(errorClass, true);
//					break;
//				default:
//					break;
//				}

		} catch (Exception e) {
			
		}
	}

	private void resetFields() {
		codeP.clear();
//		tailleP.clear();
//		etatLibre.clear();
//		etatP2.clear();
	}

	private void resetPseudoClass() {
		codeP.pseudoClassStateChanged(errorClass, false);
		tailleP.pseudoClassStateChanged(errorClass, false);
		etatLibre.pseudoClassStateChanged(errorClass, false);
		etatP2.pseudoClassStateChanged(errorClass, false);
	}

	@FXML
	void annuler(ActionEvent event) {
		//Reset les pseudoclass
		resetPseudoClass();
		//reset les champs à vide
		//resetFields();
		codeP.setPromptText("");
		
		// Récupère la fenêtre conteneur pour la fermer
		((Button) event.getSource()).getParent().getScene().getWindow().hide();
	}

	/**
	 * Permet de fournir l'accès à la couche de service
	 */
	public void setUp(IServicePlace service) {
		this.service = service;
	}


}
