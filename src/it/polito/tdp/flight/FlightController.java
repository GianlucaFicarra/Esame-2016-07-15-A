package it.polito.tdp.flight;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.flight.model.Airport;
import it.polito.tdp.flight.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FlightController {

	private Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField txtDistanzaInput;

	@FXML
	private TextField txtPasseggeriInput;

	@FXML
	private TextArea txtResult;

	
	@FXML
	void doCreaGrafo(ActionEvent event) { //seleziona rotte
		
		txtResult.clear();
		String rotte= txtDistanzaInput.getText();
		
		try {
			int kmRotte= Integer.parseInt(rotte);
			
			model.creaGrafo(kmRotte);
			
			txtResult.appendText("\nNel grafo ottenuto è possibile da ogni aeroporto raggiungere ogni altro aeroporto?");	
			if(model.isStronglyConnected()==true) {
				txtResult.appendText("\nEsiste una componente connessa, tutti gli aereoporti sono raggiungibili");	
			} else {
				txtResult.appendText("\nNon esiste una componente connessa, non tutti gli aereoporti sono raggiungibili");	
			}
			
			txtResult.appendText("\nAereoporto più distante da Fiumicino: ");	
			txtResult.appendText(String.format("Ultimo aereoporto raggiunto da Fiumicino= "+ model.getPiùDistante() ));

			
		} catch(NumberFormatException e){
			txtResult.setText("Inserire numeri validi!!");		}
	}

	
	@FXML
	void doSimula(ActionEvent event) {
		txtResult.clear();

		
		try {
			String passeggeri= txtPasseggeriInput.getText();
			int numPas= Integer.parseInt(passeggeri);
			
			model.simula(numPas);
    		List <Airport> aereoporti = model.getAllAirports();
			
    		if(aereoporti.isEmpty()) {
				txtResult.appendText("Nessun aeroporto trovato\n");
				return;
			}
			
			for(Airport a : aereoporti) {
				if(a.getNumPasseggeri() > 0) {
					txtResult.appendText("L'aeroporto "+a.getName()+" ha "+a.getNumPasseggeri()+" numero di passeggeri.\n");
				}
			}
			
			
		} catch(NumberFormatException e){
			txtResult.setText("Inserire numeri validi!!");		}
	}

	@FXML
	void initialize() {
		assert txtDistanzaInput != null : "fx:id=\"txtDistanzaInput\" was not injected: check your FXML file 'Untitled'.";
		assert txtPasseggeriInput != null : "fx:id=\"txtPasseggeriInput\" was not injected: check your FXML file 'Untitled'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Untitled'.";

	}

	public void setModel(Model model) {
		this.model = model;
	}
}
