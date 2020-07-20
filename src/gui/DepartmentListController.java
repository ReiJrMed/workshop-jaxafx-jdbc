package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentListController implements Initializable{
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnDepartmentId;//TableColumn<Tipo da coluna, tipo que será exposto na coluna>
	
	@FXML
	private TableColumn<Department, String> tableColumnDepartmentName;//TableColumn<Tipo da coluna, tipo que será exposto na coluna>
	
	@FXML
	private Button btNew;
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction()");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnDepartmentId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnDepartmentName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		
		Stage st = (Stage)Main.getMainScene().getWindow(); //getWindow() captura a janela e depois se faz o cast para Stage para captutar o Stage
		tableViewDepartment.prefHeightProperty().bind(st.heightProperty());
		//método para o height da tabela acompanhar o height da tela, ou seja, vai até o fim da tela
	}
}
