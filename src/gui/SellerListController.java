package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DBIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener{
	
	private SellerService service;
	
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
	
	private ObservableList<Seller> obsListSeller;
	
	@FXML
	public void onBtNewAction(ActionEvent action) {
		Seller department = new Seller();
		createDialogForm(department, "/gui/SellerForm.fxml", Utils.currentStage(action));
	}
	
	public void setSellerService(SellerService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnSellerId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnSellerName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		tableColumnSellerEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
		tableColumnSellerBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnSellerBirthDate, "dd/MM/yyyy");
		tableColumnSellerBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnSellerBaseSalary, 2);
		
		Stage st = (Stage)Main.getMainScene().getWindow(); //getWindow() captura a janela e depois se faz o cast para Stage para captutar o Stage
		tableViewSeller.prefHeightProperty().bind(st.heightProperty());
		//método para o height da tabela acompanhar o height da tela, ou seja, vai até o fim da tela
	}
	
	protected void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("Service was null!!");
		}
		
		List<Seller> list = service.findAll();
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
			controller.setSellerService(new SellerService());
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
			Alerts.showAlert("IO Exception", "Error loadind view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();		
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
			protected void updateItem(Seller dp, boolean empty) {
				if(dp == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(btDelete);
				btDelete.setOnAction(event -> removeSeller(dp));					
			}					
		});
	}
	
	private void removeSeller(Seller dp) {
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
