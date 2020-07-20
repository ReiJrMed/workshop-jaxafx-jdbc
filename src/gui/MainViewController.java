package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;


public class MainViewController implements Initializable{

	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction()");
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		System.out.println("onMenuItemDepartmentAction()");
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
	
	private synchronized void loadView(String absoluteName) {
		//o "synchronized" garante que este m�todo ser� executado sem interrup��es
	   //em aplica��es gr�ficas h� v�rios processos executando, essa � uma medida de seguran�a	 
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			
			VBox mainVBox = (VBox)(((ScrollPane) mainScene.getRoot()).getContent());
			//mainScene.getRoot() acessa o primeiro item da tela principal (o ScrollPane, para acessar � preciso cast para ScrollPane)
		   //ScrollPane.getContent() acessa os componentes do ScrollPane (o VBox, para acessar � preciso cast para VBox)
			
			Node mainMenu = mainVBox.getChildren().get(0);
			//Para contruir uma tela dentro de outra (principal), vai ser preciso apagar os recursos do Container da tela(main) e salv�-los em Nodes;
		   //o item que ser� apagado temporariamente ser� o MenuBar, que ser� salvo nesse Node
		  //acessa-se os filhos do VBox pelo m�todo VBox.getChildren()
		 //acessa o filho de uma determinada posi��o pelo m�todo VBox.getChildren() (o container VBox tem posi��es no caso verticais, inicia em zero)	
			
			mainVBox.getChildren().clear(); //apaga-se todos os filhos de VBox
			mainVBox.getChildren().add(mainMenu);//adiciona-se o Node mainMenu aos filhos do VBox (pois o inicial foi apagado pelo m�todo clear)
			mainVBox.getChildren().addAll(newVBox.getChildren()); //adiciona uma cole��o ao VBox, no caso os filhos do newVBox;			
		} catch(IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}
