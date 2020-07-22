package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DBException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import gui.listeners.DataChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	Seller seller;
	
	SellerService service;
	
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
	
	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	
	public void setSellerService(SellerService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener dataChangelistener) {
		dataChangeListeners.add(dataChangelistener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(seller == null)
			throw new IllegalStateException("Seller is null");
		
		if(service == null)
			throw new IllegalStateException("Service is null");
		try {
		  Alerts.showAlert("Sucess", null, service.saveOrUpdate(getSellerFormData()), AlertType.INFORMATION);
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
		Seller dp = new Seller();
		
		ValidationException exc = new ValidationException("Validation error");
		
		dp.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText() == null || txtName.getText().trim().equals(""))
			exc.addError("name", "Field can't be empty");
				
		if((!txtName.getText().trim().equals("")) && ((txtName.getText().trim().charAt(txtName.getText().trim().length() -1) == '-') || (txtName.getText().trim().charAt(txtName.getText().trim().length() -1) == 0b00100111)))
			 exc.addError("name", "- or ' present in the end of name");
			
		dp.setName(txtName.getText().trim());
		
		if(exc.getErrors().size() > 0) {
			throw exc;
		}
		
		return dp;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
		
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		comboBoxDepartment.setItems(FXCollections.observableArrayList((new DepartmentService()).findAll()));
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 60);
		Constraints.setTextFieldLetter(txtName);
		Constraints.setTextFieldTrim(txtName);
		Constraints.setTextFieldMaxLength(txtEmail, 100);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
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
		if(seller.getBirthDate() != null) {
		 dpBirthDate.setValue(LocalDate.ofInstant(seller.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		comboBoxDepartment.setValue(seller.getDepartment());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
}
