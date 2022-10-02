package car_park;

public class Automobile {	
	private final int targa;
	private boolean parked;
	
	public Automobile (int targa){
		this.targa = targa;
		this.parked = false;
	}
	
	public boolean isParked() {
		return parked;
	}

	public void setParked(boolean parked) {
		this.parked = parked;
	}

	public int getTarga(){
		return this.targa;
	}
}
