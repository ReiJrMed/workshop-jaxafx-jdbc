package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DBException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import gui.listeners.DataChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	Department department;
	
	DepartmentService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener dataChangelistener) {
		dataChangeListeners.add(dataChangelistener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(department == null)
			throw new IllegalStateException("Department is null");
		
		if(service == null)
			throw new IllegalStateException("Service is null");
		try {
		  Alerts.showAlert("Sucess", null, service.saveOrUpdate(getDepartmentFormData()), AlertType.INFORMATION);
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

	private Department getDepartmentFormData() {
		Department dp = new Department();
		
		ValidationException exc = new ValidationException("Validation error");
		
		dp.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText() == null || txtName.getAccessibleText().trim().equals(""))
			exc.addError("name", "Field can't be empty");
			
		dp.setName(txtName.getText());
		
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
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		Constraints.setTextFieldLetter(txtName);
		Constraints.setTextFieldTrim(txtName);
	}
	
	public void updateFormData() {
		if(department == null) {
			throw new IllegalStateException("Department was null");
		}
		
		txtId.setText(String.valueOf(department.getId()));
		txtName.setText(department.getName());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
}
