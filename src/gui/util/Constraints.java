package gui.util;

import javafx.scene.control.TextField;

public class Constraints {
	
	public static void setTextFieldInteger(TextField txt) {
		//o Listener é um evento que ocorre assim que o recurso sofre alguma interação
	   //nesse caso é para limitar o tipo que o usuário digita
	  //addListener(função ou função lambda)	
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*")) {
				//newValue.matches("\\d*") é uma "expressão regular" que verifica se o número digitado é inteiro
			   //\d representa dígito e * representa qualquer quantidade
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldMaxLength(TextField txt, int max) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.length() > max) {
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldDouble(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
				//newValue.matches("\\d*([\\.]\\d*)?") é uma "expressão regular que verifica se o número digitado é tipo Double"
			   //nesse caso o "\." representa ou a vírgula ou ponto do número real, tipo 1,9 ou 1.9
				txt.setText(oldValue);
			}
		});
	}
	
	public static void setTextFieldLetter(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("([a-zA-Z\u00C0-\u017F'\s-]*)?")) {
				txt.setText(oldValue);
			}
		});
	}
	
	public static void setTextFieldTrim(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.length() == 1) {
				if((newValue.charAt(0) == '-') || (newValue.charAt(0) == 0b00100111) || (newValue.charAt(0) == ' ')) {
					txt.setText(oldValue);
				}
			}			
			
		});
	}
}