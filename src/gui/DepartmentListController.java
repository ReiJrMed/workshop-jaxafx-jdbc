package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable{
	
	private DepartmentService service;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnDepartmentId;//TableColumn<Tipo da coluna, tipo que será exposto na coluna>
	
	@FXML
	private TableColumn<Department, String> tableColumnDepartmentName;//TableColumn<Tipo da coluna, tipo que será exposto na coluna>
	
	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsListDepartment;
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction()");
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
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
	
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("Service was null!!");
		}
		
		List<Department> list = service.findAll();
		obsListDepartment = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsListDepartment);
	}
}
