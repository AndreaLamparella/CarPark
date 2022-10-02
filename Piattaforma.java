package car_park;


import java.util.Scanner;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Piattaforma {
	private Parcheggio[] parcheggi;
	Scanner input = new Scanner(System.in);

	public Piattaforma(int numeroParcheggi) {
		this.parcheggi = new Parcheggio[numeroParcheggi];
		int i;
		for (i = 0; i < numeroParcheggi; ++i) {
			int posti = (int) (3 * Math.random() + 5);	
			int numeroParcheggiatori = (int) (3 * Math.random() + 2) ;	// At least two parking attendant are supposed to work at the parking garage

			parcheggi[i] = new Parcheggio(posti, numeroParcheggiatori, i);
			System.out.println("parcheggio " + i + " posti " + posti + " parcheggiatori " + numeroParcheggiatori);
		}
	}

	public Parcheggio getParcheggio(int i) {
		return parcheggi[i];
	}

	public static void main(String[] args) {

		byte[] byteReceived = new byte[1000];

		String messageString = "";

		Parcheggio[] parcheggi = new Parcheggio[5];

		for (int i = 0; i < 5; ++i)
			parcheggi[i] = new Parcheggio(1, i, i); // parcheggiatori=idParcheggio

		ArrayList<Parcheggio> parcheggiDisponibili = new ArrayList<>();

		boolean end = false;

		try {
			Socket clientSocket;
			ServerSocket listenSocket;
			listenSocket = new ServerSocket(12345);

			while (!end) {

				System.out.println("\nRunning Server: " + "Host=" + listenSocket.getLocalSocketAddress() + " Port="
						+ listenSocket.getLocalPort());

				clientSocket = listenSocket.accept();

				DataInputStream inResponse = new DataInputStream(clientSocket.getInputStream());
				int bytesRead = 0;
				bytesRead = inResponse.read(byteReceived);
				messageString = new String(byteReceived, 0, bytesRead);

				if (messageString.trim().equalsIgnoreCase("yes")) {

					OutputStream outputStream = clientSocket.getOutputStream();

					ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

					for (int i = 0; i < 5; ++i) {
						if (parcheggi[i].parcheggioLibero()) {
							System.out.println("aggiungo parcheggio " + parcheggi[i].getIdParcheggio());
							parcheggiDisponibili.add(parcheggi[i]);
						}
					}

					objectOutputStream.writeObject(parcheggiDisponibili);

					DataInputStream in = new DataInputStream(clientSocket.getInputStream());
					bytesRead = 0;

					bytesRead = in.read(byteReceived);
					messageString = new String(byteReceived, 0, bytesRead);

					if (messageString.trim().equals("")) {
						System.out.println("parcheggio selezionato non valido");
					} else {
						String[] message = messageString.split("-");

						int targa = Integer.parseInt(message[0].trim());

						int parcheggio = Integer.parseInt(message[1].trim());

						System.out.println("parcheggio " + parcheggio + " auto " + targa);

						parcheggi[parcheggio].addTarga(targa);
					}

					parcheggiDisponibili.clear();
				} else {
					System.out.println("disconnetto");
				}

				clientSocket.close();
			}
			listenSocket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
