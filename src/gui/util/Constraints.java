package gui.util;

import javafx.scene.control.TextField;

public class Constraints {
	public static void setTextFieldInteger(TextField txt) {
		//o Listener � um evento que ocorre assim que o recurso sofre alguma intera��o
	   //nesse caso � para limitar o tipo que o usu�rio digita
	  //addListener(fun��o ou fun��o lambda)	
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*")) {
				//newValue.matches("\\d*") � uma "express�o regular que verifica se o n�mero digitado � inteiro"
			   //\d representa d�gito e * representa qualquer quantidade
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
				//newValue.matches("\\d*([\\.]\\d*)?") � uma "express�o regular que verifica se o n�mero digitado � tipo Double"
			   //nesse caso o "\." representa ou a v�rgula ou ponto do n�mero real, tipo 1,9 ou 1.9
				txt.setText(oldValue);
			}
		});
	}
}