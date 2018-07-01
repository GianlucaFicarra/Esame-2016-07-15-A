package it.polito.tdp.flight.model;

import java.time.LocalDateTime;
import java.time.LocalTime;


public class Event {
	
	/*HO UN SOLO EVENTO - EVENTO PARTENZA:
	 * PASSEGGERO PARTE PER UN AEREOPORTO, QUANDO ARRIVA QUESTA SARà LA NUOVA PARTENZA */

	private Passeggero passeggero;
	private Airport airport;       // aeroporto di destinazione
	private LocalTime partenza;    // ora partenza volo per quell'aereoporto
	private LocalTime arrivo;    // ora partenza volo per quell'aereoporto
	
	

	public Event(Passeggero passeggero, Airport airport, LocalTime partenza, LocalTime arrivo) {
		super();
		this.passeggero = passeggero;
		this.airport = airport;
		this.partenza = partenza;
		this.arrivo = arrivo;
	}

	public Passeggero getPasseggero() {
		return passeggero;
	}

	public Airport getAirport() {
		return airport;
	}

	public LocalTime getPartenza() {
		return partenza;
	}

	public LocalTime getArrivo() {
		return arrivo;
	}

	public void setPartenza(LocalTime partenza) {
		this.partenza = partenza;
	}


}
