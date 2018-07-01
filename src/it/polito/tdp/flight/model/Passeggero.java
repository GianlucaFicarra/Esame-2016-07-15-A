package it.polito.tdp.flight.model;

public class Passeggero {

	private int id;
	private int oreSim;
	
	public Passeggero(int id, int oreSim) {
		this.id=id;
		this.oreSim=oreSim;
	}
	
	public void decrementaOreSim(int valore) {
		if(oreSim<=0) {oreSim=0;}
		oreSim -= valore;
	}
	
	public int getOreSim() {
		return this.oreSim;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	
}
