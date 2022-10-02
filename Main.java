package car_park;
/**
 * @author Centrone Mario
 * @author Lamparella Andrea
 * */
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws Exception{		
		Scanner input = new Scanner (System.in);
		System.out.print("Selezionare numero parcheggi: ");
		int numeroParcheggi = input.nextInt();
		Piattaforma piattaforma = new Piattaforma(numeroParcheggi);
		System.out.println("\nSelezionare numero automobilisti ");
		int numeroAutomobilisti = input.nextInt();
		
		Automobilista[] automobilisti = new Automobilista[numeroAutomobilisti];
		Automobile[] auto = new Automobile[numeroAutomobilisti];
		
		for(int i = 0; i < numeroAutomobilisti; ++i)
			auto[i] = new Automobile(i);
		
		for(int i = 0; i < numeroAutomobilisti; ++i){
			//System.out.println("selezionare parcheggio automobilista "+i);
			//int idParcheggio = input.nextInt();
	        automobilisti[i] = new Automobilista(auto[i]);
	        automobilisti[i].setParcheggio(piattaforma.getParcheggio(0));//
		}
        try {
        	for(int c=0; c<automobilisti.length; c++) {
        		automobilisti[c].start();		
    		}
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
        	e.printStackTrace();
        }
	}

}
