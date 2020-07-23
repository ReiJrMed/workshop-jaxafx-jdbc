package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DBIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener{
	
	private SellerService sellerService;
	
	private DepartmentService departmentService;
	
	@FXML
	private TableView<Seller> tableViewSeller;
	
	@FXML
	private TableColumn<Seller, Integer> tableColumnSellerId;//TableColumn<Tipo da coluna, tipo que será exposto na coluna>
	
	@FXML
	private TableColumn<Seller, String> tableColumnSellerName;//TableColumn<Tipo da coluna, tipo que será exposto na coluna>
	
	@FXML
	private TableColumn<Seller, String> tableColumnSellerEmail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumnSellerBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnSellerBaseSalary;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnDELETE;
	
	@FXML
	private Button btNew;
	
	@FXML
	private TextField txtConsultByName;
	
	@FXML
	private ComboBox<Department> comboBoxDepartment;
	
	private ObservableList<Department> obsListDepartment;
	
	private ObservableList<Seller> obsListSeller;
	
	@FXML
	public void onBtNewAction(ActionEvent action) {
		Seller department = new Seller();
		createDialogForm(department, "/gui/SellerForm.fxml", Utils.currentStage(action));
	}
	
	public void setServices(SellerService sellerService, DepartmentService departmentService) {
		this.sellerService = sellerService;
		this.departmentService = departmentService;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		        txtConsultByName.requestFocus();//quando a janela for inctanciada, o focus será direcionado a esse recurso
		    }
		});
	}
	
	@FXML
	public void onTxtConsultByNameKeyTyped() {
		if(comboBoxDepartment.getValue() != null)
		  consultTable(txtConsultByName.getText(), comboBoxDepartment.getValue());
		else
		  consultTable(txtConsultByName.getText(), new Department());	
	}
	
	@FXML
	public void onComboBoxDepartmentAction() {
		consultTable(txtConsultByName.getText(), comboBoxDepartment.getValue());
	}

	protected void consultTable(String name, Department department) {
		if(sellerService == null) {
			throw new IllegalStateException("Service was null");
		}
		
		List<Seller> list = sellerService.findByOptions(name, department);
		obsListSeller = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsListSeller);
		
		initEditButtons();
		initDeleteButtons();
	}
	
	private void initializeNodes() {
		tableColumnSellerId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnSellerName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		tableColumnSellerEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
		tableColumnSellerBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnSellerBirthDate, "dd/MM/yyyy");
		tableColumnSellerBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnSellerBaseSalary, 2);
		initializeComboBoxDepartment();
		comboBoxDepartment.getSelectionModel().selectFirst();
		
		Stage st = (Stage)Main.getMainScene().getWindow(); //getWindow() captura a janela e depois se faz o cast para Stage para captutar o Stage
		tableViewSeller.prefHeightProperty().bind(st.heightProperty());
		//método para o height da tabela acompanhar o height da tela, ou seja, vai até o fim da tela
	}
	
	protected void updateTableView() {
		if(sellerService == null) {
			throw new IllegalStateException("Service was null!!");
		}
		
		List<Seller> list = sellerService.findAll();
		obsListSeller = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsListSeller);
		
		initEditButtons();
		initDeleteButtons();
	}
	
	private void createDialogForm(Seller department, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			SellerFormController controller = loader.getController();
			controller.setSeller(department);
			controller.setServices(new SellerService(), new DepartmentService());
			controller.loadAssociatedObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			
			Stage dialogStage = new Stage();//Para fazer uma janela modal é preciso um novo palco(stage) instanciado
			dialogStage.setTitle("Enter Seller Data");//altera o título do palco
			dialogStage.setScene(new Scene(pane));//instancia uma cena(Scene(elemento raíz, no caso o tela(pane) ou container))
			dialogStage.setResizable(false);//método que diz se a janela pode ou não ser redimensionada
			dialogStage.initOwner(parentStage);//seta o pai dessa janela, no caso o formulário principal
			dialogStage.initModality(Modality.WINDOW_MODAL);//Seta esse formulário como modal, ou seja, só volta ao pricipal se encerrá-lo
			dialogStage.showAndWait();//Exibe o formulário, o novo palco
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loadind view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		consultTable(txtConsultByName.getText(), comboBoxDepartment.getValue());		
	}
	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>(){
			private final Button btEdit = new Button("edit");
			
			@Override
			protected void updateItem(Seller dp, boolean empty) {
				if(dp == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(btEdit);
				btEdit.setOnAction(event -> createDialogForm(dp, "/gui/SellerForm.fxml", Utils.currentStage(event)));					
			}			
		});
	}
	
	private void initDeleteButtons() {
		tableColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDELETE.setCellFactory(param -> new TableCell<Seller, Seller>(){
			private final Button btDelete = new Button("delete");
			
			@Override
			protected void updateItem(Seller sl, boolean empty) {
				if(sl == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(btDelete);
				btDelete.setOnAction(event -> removeSeller(sl));					
			}					
		});
	}
	
	private void removeSeller(Seller dp) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		if(result.get() == ButtonType.OK) {
			if(sellerService == null)
				throw new IllegalStateException("Service was null");
			
			try {
				sellerService.remove(dp);
				updateTableView();
			} catch (DBIntegrityException e) {
				Alerts.showAlert("Error removing department", null, e.getMessage(), AlertType.ERROR);
			}
		}			
	}
	
	public void loadAssociatedObjects() {
		if(departmentService == null)
			throw new IllegalStateException("Department Service was null");
		
		List<Department> departments = new ArrayList<>();
		departments.add(new Department());
		
		for(Department department : departmentService.findAll()) {
			departments.add(department);
		}		
				
		obsListDepartment = FXCollections.observableArrayList(departments);
		comboBoxDepartment.setItems(obsListDepartment);
	}
	
	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department departmentItem, boolean empty) {
				super.updateItem(departmentItem, empty);
				setText(empty ? "" : departmentItem.getName());
			} 
		};
		
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
	
}