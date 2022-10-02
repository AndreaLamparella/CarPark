package car_park;

import java.io.Serializable;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Parcheggio implements Serializable{
	private Map<Integer, Automobile> autoMap = new HashMap<Integer, Automobile>();
	private Parcheggiatore[] parcheggiatori;
	private ArrayList<Integer> targhe = new ArrayList<>();
	private final int posti, idParcheggio;
	private int ticket, numPostiOccupati;
	private boolean libero;		

	public Parcheggio(int posti, int numeroParcheggiatori, int idParcheggio) {
		this.posti = posti;
		this.parcheggiatori = new Parcheggiatore[numeroParcheggiatori];
		for (int i = 0; i < numeroParcheggiatori; ++i) {
			parcheggiatori[i] = new Parcheggiatore(i, this);
		}
		this.idParcheggio=idParcheggio;
		this.ticket = 0;
		this.numPostiOccupati = 0;
		this.libero = true;
	}
	
	public int getIdParcheggio(){
		return this.idParcheggio;
	}

	public Map<Integer, Automobile> getAuto() {
		return autoMap;
	}

	public void setTicket(int ticket) {
		this.ticket = ticket;
	}

	public int getTicket() {
		return ticket;
	}
	
	public void addTarga(int targa){
		numPostiOccupati++;
		targhe.add(targa);
	}
	public void removeTarga(int targa){
		numPostiOccupati--;
		targhe.remove(targa);
	}

	public synchronized boolean parcheggioLibero() {
		return (numPostiOccupati >= posti) ? false : true;
	}

	/**
	 * Handles the ticket value in order to be consistent.
	 */
	public synchronized void ticketAggiunta(Automobilista a) {
		a.setTicket(ticket);
		ticket++;
		numPostiOccupati++;
		this.libero = parcheggioLibero();
		System.out
				.println("CHECK-POINT ENTRATA " + a.getAutomobile().getTarga() + " posti occupati " + numPostiOccupati);
	}

	public synchronized void ticketRimozione(Automobilista a) {
		numPostiOccupati--;
		this.libero = parcheggioLibero();
		System.out
				.println("CHECK-POINT USCITA " + a.getAutomobile().getTarga() + " posti occupati " + numPostiOccupati);
	}

	/**
	 * Check if any parking attendant is available. If there aren't the method
	 * returns an error code.
	 */
	public synchronized int parcheggiatoreDisponibile() {
		for (int i = 0; i < parcheggiatori.length; ++i) {
			if (!parcheggiatori[i].isBusy()) {
				parcheggiatori[i].setBusy(true);
				return i;
			}
		}

		return -1;
	}

	/**
	 * Check if any parking spot is available. If so, a parking attendant is
	 * required in order to server the car driver, otherwise the current thread
	 * waits. When none is available the current thread goes in a waiting state.
	 */
	public synchronized int controlloParcheggia(Automobilista a) throws InterruptedException {
		while (!libero) {
			wait();
		}
		
		ticketAggiunta(a);
		int i = parcheggiatoreDisponibile();
		while (i == -1) {
			wait();
			i = parcheggiatoreDisponibile();
		}

		return i;
	}

	public synchronized int controlloRitira(Automobilista a) throws InterruptedException {
		int i = parcheggiatoreDisponibile();
		while (i == -1) {
			System.out.println("ATTENDI un parcheggiatore per USCIRE auto " + a.getAutomobile().getTarga());
			wait();
			i = parcheggiatoreDisponibile();
		}
		ticketRimozione(a);

		return i;
	}

	public void parcheggia() throws InterruptedException {
		Automobilista a = (Automobilista) Thread.currentThread();

		int i = controlloParcheggia(a);
		System.out.println("[PARCHEGGIA] Parcheggiatore " + i + " auto " + a.getAutomobile().getTarga() + " ticket "
				+ a.getTicket());
		parcheggiatori[i].parcheggia(a.getAutomobile(), a.getTicket());

	}

	public void ritiraAuto(Automobilista a) throws InterruptedException {

		int i = controlloRitira(a);
		System.out.println(
				"[RITIRA] Parcheggiatore " + i + " auto " + a.getAutomobile().getTarga() + " ticket " + a.getTicket());
		parcheggiatori[i].rimuovi(a.getAutomobile(), a.getTicket());
	}

	public synchronized void addAuto(int ticket, Automobile auto, int i) throws InterruptedException {
		autoMap.put(ticket, auto);
		parcheggiatori[i].setBusy(false);
		System.out.println(
				"liberato parcheggiatore " + i + " parchaggiata auto " + auto.getTarga() + " ticket " + ticket);
		notifyAll();
	}

	public synchronized void removeAuto(int ticket, Automobile auto, int i) throws InterruptedException {
		autoMap.remove(ticket, auto);
		parcheggiatori[i].setBusy(false);
		System.out.println("liberato parcheggiatore " + i + " rimossa auto " + auto.getTarga() + " ticket " + ticket);
		notifyAll();
	}
	
	
}
