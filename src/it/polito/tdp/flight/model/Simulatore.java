package it.polito.tdp.flight.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.flight.db.FlightDAO;

public class Simulatore {
	
//PARAMETRI (variabili per modificare la simulazione)
	private int numPas;
	private Airline linea;
	private List<Airport> airports;
	
	private FlightDAO dao;
	private Model model;
	Random random;
	
	//range in cui lavoro, dalle 8 alle 20
		private LocalTime T_inizio = LocalTime.of(06, 00);
		private int T_fine;
	
	//MODELLO DEL MONDO (fotografia del sistema)
	private List<Passeggero> passeggeri;
	
	//CODA DEGLI EVENTI
	Queue<Event> queue;
	
	//VALORI OUTPUT
	//è la mia stessa lista di passeggeri, dove vado a memorizzare i ritardi, voglio quelli
	
	
	public void init(int numPas, Model model) {
		this.numPas=numPas;
		this.linea=linea;
		this.dao = model.dao;
		this.model=model;
		
		List<Airport> destinazioni = new ArrayList<>();
		airports= model.getSourceAirports();
		
		 queue = new LinkedList();
		 random = new Random();
		 T_fine=10; //dovrebbe essere 48h, ma dura tropo, vedo risultati da 10
		
		 //creo numPas passeggeri e li assegno ogni volta ad un aereoporto casuale
		 passeggeri = new ArrayList<>(); //li salvo per poi stamparne il ritardo
		 for(int i=0; i<numPas; i++) {
				
			 //posizionare numPas passeggeri in modo casuale tra gli aeroporti disponibili
				int x1 = random.nextInt(airports.size()); // nextInit da un numero da 0 al numero inserito tra () escluso
				Airport casualAirport = airports.get(x1); //prendo aereoporto casuale
				
				Passeggero p = new Passeggero(i, T_fine);
				passeggeri.add(p);
				casualAirport.addPasseggero();
				
				destinazioni= model.getDestinationsFrom(casualAirport);
				int x2=random.nextInt(destinazioni.size()); // nextInit da un numero da 0 al numero inserito tra () escluso
			    Airport casualAirport2 = destinazioni.get(x2);
			   
			    double durata=  model.getDurataVolo(casualAirport, casualAirport2); //model.grafo.getEdgeWeight(model.grafo.getEdge(casualAirport, casualAirport2));
			    int ore = (int) durata; // faccio il cast a int per ricavare le ore
				int minuti = (int) ((durata*60) % 60); // faccio il modulo per ricavare i minuti
			    		
			    //creo evento che mi dice quando arriva questo passeggero
				// CONSIDERO L'EVENTO LA DESTINAZIONE --> parte alle 7 + durata del volo = orario arrivo
				Event e = new Event(p, casualAirport2,  LocalTime.of(07, 00),  LocalTime.of(07+ore, minuti) );
				queue.add(e) ; //creato evento lo aggiungo alla coda
			
				
			}
	}

	public void run() {
		
		Event e;
		while ((e = queue.poll()) != null) {
			if(e.getPasseggero().getOreSim() > 0) {
				System.out.println("PROCESSO\n");
				processEvent(e);
			}
	    }
		
	}
	
	private void processEvent(Event e) {

		//ARRIVO PASSEGGERO==PARTENZA STESSO PASSEGGERO
		Passeggero passeggero = e.getPasseggero();
		Airport source = e.getAirport();
		source.addPasseggero();
		
		List<Airport> destinazioni= model.getDestinationsFrom(source);
		
		int x2=random.nextInt(destinazioni.size()); // nextInit da un numero da 0 al numero inserito tra () escluso
	    Airport casualAirport = destinazioni.get(x2);
	   
	    //conto le ore di volo fatte dal passeggero
	    LocalTime partenza = e.getPartenza();
		LocalTime arrivo = e.getArrivo();
		while(arrivo.compareTo(partenza)>=0) {
			partenza = partenza.plusHours(2);
			passeggero.decrementaOreSim(2);
		}
	    
	    
	    
	    double durata= model.getDurataVolo(source, casualAirport); //model.grafo.getEdgeWeight(model.grafo.getEdge(e.getAirport(), casualAirport));
		
	    int ore = (int) durata;
		int minuti = (int) ((durata*60) % 60);
	    		
		//considero solo ore diurne 7/23
		if(partenza.compareTo(LocalTime.of(07, 00)) >= 0 &&
				partenza.compareTo(LocalTime.of(23, 00)) <= 0) {
			//se arriva ed è ancora giorno lo faccio partire
			Event e1 = new Event(passeggero, casualAirport, partenza, LocalTime.of(partenza.getHour()+ore, partenza.getMinute()+minuti));
			queue.add(e1);
		} else {
			while(partenza.compareTo(LocalTime.of(07, 00)) < 0) {
				partenza = partenza.plusHours(2); //vuole partire prima delle 7, ma la prox partenza la posso fare alle 9
				passeggero.decrementaOreSim(2);
			}
			
	    //creo evento che mi dice quando arriva questo passeggero
		Event e2 = new Event(passeggero, casualAirport,  partenza, LocalTime.of(partenza.getHour()+ore, partenza.getMinute()+minuti) );
		queue.add(e2); //creato evento lo aggiungo alla coda
		}		
		
	}


	}
