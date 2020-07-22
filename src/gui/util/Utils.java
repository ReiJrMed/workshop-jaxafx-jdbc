package gui.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

public class Utils {
	
	public static Stage currentStage(ActionEvent action) {
		return (Stage)(((Node)action.getSource()).getScene().getWindow());//Downcasting do getWindow para o Stage
	}
	
	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return null;
		}
	}
	
	public static <T> void formatTableColumnDate(TableColumn<T, Date> tableColumn, String format) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Date> cell = new TableCell<T, Date>(){
				private SimpleDateFormat sdf = new SimpleDateFormat(format);
				
				@Override
				protected void updateItem(Date date, boolean empty) {
					super.updateItem(date, empty);
					
					if(empty)
						setText(null);
					else
						setText(sdf.format(date));
				}				
			};
			
			return cell;
		});
	}
	
	public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, int decimalPlaces) {
		tableColumn.setCellFactory(column -> {
		
		TableCell<T, Double> cell = new TableCell<T, Double>(){
			@Override
			protected void updateItem(Double value, boolean empty) {
				super.updateItem(value, empty);
					
				if(empty) {
					setText(null);
				} else {
					setText(String.format("%." + decimalPlaces + "f", value));
				}
			}
		 };			
		
		  return cell;
	   });
	}
	
}
