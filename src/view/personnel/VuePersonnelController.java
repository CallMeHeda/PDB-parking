package view.personnel;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import dao.ParkingConstraintException;
import dao.ParkingException;
import dao.ParkingPKException;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.Personnel;
import model.Place;
import services.IServicePersonnel;

public class VuePersonnelController implements Initializable {

	private final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
	private final PseudoClass activeClass = PseudoClass.getPseudoClass("activeBtn");

	private IServicePersonnel service;
//La personne à créer
	private Personnel p;

	@FXML
	private TextField ztImmatr;
	@FXML
	private TextField ztNom;
	@FXML
	private TextField ztPrenom;
	@FXML
	private TextField ztEmail;

	@FXML
	private Button btOk;

	@FXML
	private Button btCancel;

	private ResourceBundle bundle;

	@FXML
	void valider(ActionEvent event) {
		//this.bundle = bundle;
		p = new Personnel(ztImmatr.getText(), ztNom.getText(), ztPrenom.getText(), ztEmail.getText());

		try {// Remet la pseudoclass des éléments error à faux
			resetPseudoClass();
			
			// Msg d'erreur si le champ et vide ou incorrecte
			if(!ztImmatr.getText().matches("[A-Z]{3}[-]{1}[0-9]{3}")){
				ztImmatr.setPromptText("La syntaxe " + ztImmatr.getText() + " n'est pas valable ! Ex -> AAA-111");
				if(ztImmatr.getText().isEmpty()) {
					ztImmatr.setPromptText("Veuillez entrer une immatriculation SVP");
			}
		}else {
			service.insert(p);
			ztImmatr.setPromptText("");
		}
			// reset les zones de texte car ici tout c'est bien passé
			resetFields();

			// Récupère la fenêtre pour la fermer
			//((Button) event.getSource()).getParent().getScene().getWindow().hide();

		} catch (ParkingException pe) {

			// Ajuste la pseudo classe pour chaque élément en fonction de l'exception
			if (pe instanceof ParkingPKException)
				ztImmatr.pseudoClassStateChanged(errorClass, true);
			else if (pe instanceof ParkingConstraintException)
				switch (((ParkingConstraintException) pe).getChamp()) {
				case "NOM_PER":
					ztNom.pseudoClassStateChanged(errorClass, true);
					break;
				case "PRENOM_PER":
					ztPrenom.pseudoClassStateChanged(errorClass, true);
					break;
				case "EMAIL_PER":
					ztEmail.pseudoClassStateChanged(errorClass, true);
					break;
				default:
					break;
				}

		} catch (Exception e) {
		}
	}

	private void resetFields() {
		ztImmatr.clear();
		ztNom.clear();
		ztPrenom.clear();
		ztEmail.clear();
	}

	private void resetPseudoClass() {
		ztImmatr.pseudoClassStateChanged(errorClass, false);
		ztNom.pseudoClassStateChanged(errorClass, false);
		ztPrenom.pseudoClassStateChanged(errorClass, false);
		ztEmail.pseudoClassStateChanged(errorClass, false);
	}

	@FXML
	void annuler(ActionEvent event) {
		//Reset les pseudoclass
		resetPseudoClass();
		//reset les champs à vide
		resetFields();
		
		// Récupère la fenêtre conteneur pour la fermer
		((Button) event.getSource()).getParent().getScene().getWindow().hide();
	}
	
	/**
	 * Permet de fournir l'accès à la couche de service
	 */
	public void setUp(IServicePersonnel service) {
		this.service = service;
	}
	
	
//Permet d'initialiser des composants 
	@Override
	public void initialize(URL url, ResourceBundle bd) {

	}

}
