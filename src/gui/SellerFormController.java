package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DBException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller seller;
	
	private SellerService sellerService;
	
	private DepartmentService departmentService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private ComboBox<Department> comboBoxDepartment;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	private ObservableList<Department> obsListDepartment;
	
	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	
	public void setServices(SellerService sellerService, DepartmentService departmentService) {
		this.sellerService = sellerService;
		this.departmentService = departmentService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener dataChangelistener) {
		dataChangeListeners.add(dataChangelistener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(seller == null)
			throw new IllegalStateException("Seller is null");
		
		if(sellerService == null)
			throw new IllegalStateException("Service is null");
		try {
		  Alerts.showAlert("Sucess", null, sellerService.saveOrUpdate(getSellerFormData()), AlertType.INFORMATION);
		  notifyDataChangeListeners();
		  Utils.currentStage(event).close();
		} catch(ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DBException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListeners() {
		for(DataChangeListener dataChangeListener : dataChangeListeners) {
			dataChangeListener.onDataChanged();
		}		
	}

	private Seller getSellerFormData() {
		Seller sl = new Seller();
		
		ValidationException exc = new ValidationException("Validation error");
		
		sl.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exc.addError("name", "Field can't be empty");
		} else {
			if((txtName.getText().trim().charAt(txtName.getText().trim().length() -1) == '-') || (txtName.getText().trim().charAt(txtName.getText().trim().length() -1) == 0b00100111))
				 exc.addError("name", "- or ' present in the end of name");
			else
				sl.setName(txtName.getText().trim());	
		}	
				
		if(txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exc.addError("email", "Field can't be empty");
		} else {	
		    if(Utils.validateEmail(txtEmail.getText().trim()))
			    sl.setEmail(txtEmail.getText().trim());
		    else
		    	exc.addError("email", "Email format invalid (#####@####.###)");
		}
		if(dpBirthDate.getValue() != null) {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			//capturando data do datepicker em forma de instant
			
			if(Date.from(instant).after(new Date()))
			  exc.addError("birthDate", "Date after current date.");
			else		
		      sl.setBirthDate(Date.from(instant));
			
		} else {
			exc.addError("birthDate", "Field can't be empty");
		}
		
		if(txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals(""))
			exc.addError("BaseSalary", "Field can't be empty");
		
		sl.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		sl.setDepartment(comboBoxDepartment.getValue());
		
		if(exc.getErrors().size() > 0) {
			throw exc;
		}
		
		return sl;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
		
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();		
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 60);
		Constraints.setTextFieldLetter(txtName);
		Constraints.setTextFieldTrim(txtName);
		Constraints.setTextFieldMaxLength(txtEmail, 100);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
		initializeComboBoxDepartment();
	}
	
	public void updateFormData() {
		if(seller == null) {
			throw new IllegalStateException("Seller was null");
		}
		
		txtId.setText(String.valueOf(seller.getId()));
		txtName.setText(seller.getName());
		txtEmail.setText(seller.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", seller.getBaseSalary()));
		
		if(seller.getBirthDate() != null)
		 dpBirthDate.setValue(LocalDate.ofInstant(seller.getBirthDate().toInstant(), ZoneId.systemDefault()));
		
		
		if(seller.getDepartment() == null)
			comboBoxDepartment.getSelectionModel().selectFirst();//seleciona o primeiro item
		else
			comboBoxDepartment.setValue(seller.getDepartment());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		labelErrorName.setText(fields.contains("name") ? errors.get("name") : "");
		labelErrorEmail.setText(fields.contains("email") ? errors.get("email") : "");
		labelErrorBirthDate.setText(fields.contains("birthDate")? errors.get("birthDate") : "");
		labelErrorBaseSalary.setText(fields.contains("BaseSalary") ? errors.get("BaseSalary") : "");		
	}
	
	public void loadAssociatedObjects() {
		if(departmentService == null)
			throw new IllegalStateException("Department Service was null");
		
		List<Department> departments = departmentService.findAll();
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
