package view.statusBar;

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class StatusBar extends AnchorPane {
	private ResourceBundle bundle;

	public StatusBar() {
		super();
		load();
	}

	private void load() {
		try {
			// Crée un loader pour charger la vue FXML
			FXMLLoader loader = new FXMLLoader(getClass().getResource("StatusBar.fxml"));
			// charge le bundle par rapport à la local par défaut
			bundle = ResourceBundle.getBundle("view.statusBar.bundles.vueStatus");
			loader.setResources(bundle);
			// indique que sa racine est ce composant
			loader.setRoot(this);
			//indique que je suis son controller
			loader.setController(this);
			// Charge la vue à partir du Loader
			loader.load();
		} catch (IOException e) {
			// gestion de l’erreur du chargement
			Alert al1 = new Alert(AlertType.ERROR, e.getMessage());
			al1.showAndWait();
		}
	}

	public enum TypeMessage {
		NONE, INFO, ERROR
	};

// Pseudo classe pour avec un style css adapté en mode d'erreur :error
	private final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

	@FXML
	private Label lblStatus;

	@FXML
	private TextField ztStatus;

	
	TypeMessage status = TypeMessage.INFO;

	public TypeMessage getEtatStatus() {
		return status;
	}

	/**
	 * Efface les messages
	 */
	public void clearStatusBar() {
		// indique un mode sans erreur
		setInfoStatus("", TypeMessage.NONE);

	}

	// Initialise les zones de textes avec l'article sélectionné
	public void setInfoStatus(String message, TypeMessage type) {
		switch (type) {
		case NONE:
			ztStatus.clear();
			break;
		case INFO:
		case ERROR:
			ztStatus.setText(message);
		}
		status = type;
		lblStatus.pseudoClassStateChanged(errorClass, status == TypeMessage.ERROR);
	}
	
	public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
    }

    public StringProperty textProperty() {
        return ztStatus.textProperty();
    }
	
	@FXML
	private void initialize() {
		
	}

}
