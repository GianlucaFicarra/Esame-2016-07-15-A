package it.polito.tdp.flight.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;


import it.polito.tdp.flight.db.FlightDAO;

public class Model {

	private List<Airport> airports;
	private List<Route> routes;
	
	AirportIdMap airportIdMap;
	
	FlightDAO dao;
		
		
	SimpleDirectedWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	
	
	
	public Model() {
		dao= new FlightDAO();
	
		routes = dao.getAllRoutes();
				
		airportIdMap = new AirportIdMap();
		airports= dao.getAllAirports(airportIdMap);
		}



	public void creaGrafo(int kmRotte) {
		
		//creo grafo.... dichiarazione standard
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
				
		//creato grafo aggiungo i vertici dalla lista di aereoporti
		Graphs.addAllVertices(grafo, this.airports);
		
		//Un arco collega due aeroporti solo se la loro distanza è
		//inferiore ai chilometri selezionati ed almeno una compagnia aerea compie tale rotta.
				for (Route r : routes) {
					//aereoporto sorgente e destinazione
					Airport sourceAirport = airportIdMap.get(r.getSourceAirportId());
					Airport destinationAirport = airportIdMap.get(r.getDestinationAirportId());
					
					//prendo solo aereoporti validi registrati nella cartella AIRPORTS
					if (sourceAirport!=null && destinationAirport!=null) {
					
						//se sono diversi
						if (!sourceAirport.equals(destinationAirport)) {
							
							//calcolo il peso tramite le distanze in longitudine e latitudine:
							double distanza = LatLngTool.distance(new LatLng(sourceAirport.getLatitude(), sourceAirport.getLongitude()),
									                            new LatLng(destinationAirport.getLatitude(), destinationAirport.getLongitude()),
									                            LengthUnit.KILOMETER);
							
							if(distanza <kmRotte) {
								//Il peso dell’arco è pari alla durata del volo, ipotizzando una velocità costante di crociera a 800 km/h.
							    //v=s/t --> t=s/V
								double weight= (double)distanza/(double)800;
								
								Graphs.addEdge(grafo, sourceAirport, destinationAirport, weight);
							}
							
						
						}
					}
		
		
	          }
				
	}




		//un grafo diretto è fortemente connesso se e solo se ha una sola componente connessa
		//cioè se tra tutti i nodi esiste una strada da AB e da BA--> uso Kosaraju
	   //ovvero è possibile da ogni aeroporto raggiungere ogni altro aeroporto.
		public boolean isStronglyConnected() {
			KosarajuStrongConnectivityInspector<Airport, DefaultWeightedEdge> ksci = new KosarajuStrongConnectivityInspector<Airport, DefaultWeightedEdge>(grafo);
			return ksci.isStronglyConnected();
		}



		public Airport getPiùDistante() {
			
		Airport fiumicino=null;
			for(Airport a: airports) {
				if(a.getName().equals("Fiumicino")) {
					 fiumicino=a;
					break;
				}
			}
			
			// visita il grafo, creo iteratore che restituisce i vertiti che visita
			//e li raccolgo in una collection (scelgo list perchè devo prenderne l'ultima componente)
			List<Airport> visitati = new LinkedList<>();
			
			DepthFirstIterator<Airport, DefaultWeightedEdge> dfv = new DepthFirstIterator<>(this.grafo, fiumicino);
			//iteratore che uso per iterare grafo partendo da start chiamando next
			
			while (dfv.hasNext()) //finche cè un elemento successivo per l'iteratore
				visitati.add(dfv.next()); //aggiungo il prox elemento, se diventa falso esco

			// torno l'ultima componete visitata
			return visitati.get(visitati.size()-1);
			
		}



		public void simula(int numPas) {
			for(Airport a : grafo.vertexSet()) {
				a.setNumPasseggeri(); //inizzializzo a 0 i passeggeri per ogni aereoporto
			}
			Simulatore sim = new Simulatore();
			sim.init(numPas, this); 
			sim.run();
		}

		public List<Airport> getDestinationsFrom(Airport source) {
			
			List<Airport> destinations = new ArrayList<>();
			
			// graph.outgoingEdgesOf ritorna un set di archi dato un vertice V
			for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(source)) {
				destinations.add(grafo.getEdgeTarget(e));
			}
			
			return destinations;
		}
		
		public List<Airport> getSourceAirports() {
			List<Airport> sources = new ArrayList<>();
			for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
				sources.add(grafo.getEdgeSource(e));
			}
			
			return sources;
		}
		
		public double getDurataVolo(Airport source, Airport destination) {
			return grafo.getEdgeWeight(grafo.getEdge(source, destination));
		
		}
		
		//i risultati della simulazione sono settati negli aereoporti
		public List<Airport> getAllAirports() {
			return this.airports;
		}
		
	
	
	
	
	

}
