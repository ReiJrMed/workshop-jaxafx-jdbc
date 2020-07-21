package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	
	public static Stage currentStage(ActionEvent action) {
		return (Stage)(((Node)action.getSource()).getScene().getWindow());//Downcasting do getWindow para o Stage
	}
	
}
