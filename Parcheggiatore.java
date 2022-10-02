package car_park;

import java.io.Serializable;

public class Parcheggiatore implements Serializable{
	private final Parcheggio parcheggio;
	private final int id;
	private boolean busy;

	public Parcheggiatore(int id, Parcheggio parcheggio) {
		this.id = id;
		this.parcheggio = parcheggio;
		busy = false;
	}

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public int getIdentifier() {
		return id;
	}

	public void parcheggia(Automobile auto, int ticket) {
		try {
			Thread.sleep(3000);
			auto.setParked(true);
			parcheggio.addAuto(ticket, auto, id);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void rimuovi(Automobile auto, int ticket) {
		try {
			Thread.sleep(3000);
			auto.setParked(false);
			parcheggio.removeAuto(ticket, auto, id);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
