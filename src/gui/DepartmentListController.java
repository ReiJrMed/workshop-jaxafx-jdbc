package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DBIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener{
	
	private DepartmentService service;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnDepartmentId;//TableColumn<Tipo da coluna, tipo que ser� exposto na coluna>
	
	@FXML
	private TableColumn<Department, String> tableColumnDepartmentName;//TableColumn<Tipo da coluna, tipo que ser� exposto na coluna>
	
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;
	
	@FXML
	private TableColumn<Department, Department> tableColumnDELETE;
	
	@FXML
	private Button btNew;
	
	@FXML
	private TextField txtConsultByName;
	
	private ObservableList<Department> obsListDepartment;
	
	@FXML
	public void onTxtConsultByNameKeyTyped() {
		consultTable(txtConsultByName.getText());
	}
	
	private void consultTable(String name) {
		if(service == null) {
			throw new IllegalStateException("Service was null!!");
		}
		
		List<Department> list = service.findByName(name);
		obsListDepartment = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsListDepartment);
		
		initEditButtons();
		initDeleteButtons();
	}
	
	@FXML
	public void onBtNewAction(ActionEvent action) {
		Department department = new Department();
		createDialogForm(department, "/gui/DepartmentForm.fxml", Utils.currentStage(action));
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		        txtConsultByName.requestFocus();
		    }
		});
	}

	private void initializeNodes() {
		tableColumnDepartmentId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnDepartmentName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		Constraints.setTextFieldLetter(txtConsultByName);
				
		Stage st = (Stage)Main.getMainScene().getWindow(); //getWindow() captura a janela e depois se faz o cast para Stage para captutar o Stage
		tableViewDepartment.prefHeightProperty().bind(st.heightProperty());
		//m�todo para o height da tabela acompanhar o height da tela, ou seja, vai at� o fim da tela
	}
	
	protected void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("Service was null!!");
		}
		
		List<Department> list = service.findAll();
		obsListDepartment = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsListDepartment);
		
		initEditButtons();
		initDeleteButtons();
	}
	
	private void createDialogForm(Department department, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(department);
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			
			Stage dialogStage = new Stage();//Para fazer uma janela modal � preciso um novo palco(stage) instanciado
			dialogStage.setTitle("Enter Department Data");//altera o t�tulo do palco
			dialogStage.setScene(new Scene(pane));//instancia uma cena(Scene(elemento ra�z, no caso o tela(pane) ou container))
			dialogStage.setResizable(false);//m�todo que diz se a janela pode ou n�o ser redimensionada
			dialogStage.initOwner(parentStage);//seta o pai dessa janela, no caso o formul�rio principal
			dialogStage.initModality(Modality.WINDOW_MODAL);//Seta esse formul�rio como modal, ou seja, s� volta ao pricipal se encerr�-lo
			dialogStage.showAndWait();//Exibe o formul�rio, o novo palco
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loadind view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		consultTable(txtConsultByName.getText());
	}
	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>(){
			private final Button btEdit = new Button("edit");
			
			@Override
			protected void updateItem(Department dp, boolean empty) {
				if(dp == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(btEdit);
				btEdit.setOnAction(event -> createDialogForm(dp, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));					
			}			
		});
	}
	
	private void initDeleteButtons() {
		tableColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDELETE.setCellFactory(param -> new TableCell<Department, Department>(){
			private final Button btDelete = new Button("delete");
			
			@Override
			protected void updateItem(Department dp, boolean empty) {
				if(dp == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(btDelete);
				btDelete.setOnAction(event -> removeDepartment(dp));					
			}					
		});
	}
	
	private void removeDepartment(Department dp) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		if(result.get() == ButtonType.OK) {
			if(service == null)
				throw new IllegalStateException("Service was null");
			
			try {
				service.remove(dp);
				updateTableView();
			} catch (DBIntegrityException e) {
				Alerts.showAlert("Error removing department", null, e.getMessage(), AlertType.ERROR);
			}
		}			
	}		
}
