package car_park;


import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;

public class Automobilista extends Thread {
	private final Automobile auto;
	private Parcheggio parcheggio;
	private int ticket;
	private Scanner tastiera;
	private Socket socket;

	public Automobilista(Automobile auto) {
		this.tastiera = new Scanner(System.in);
		this.auto = auto;
	}

	private void setSocket(InetAddress serverAddress, int serverPort) throws Exception {
		this.socket = new Socket(serverAddress, serverPort);
	}

	public void setParcheggio(Parcheggio parcheggio) {
		this.parcheggio = parcheggio;
	}

	public Parcheggio getParcheggio() {
		return this.parcheggio;
	}

	public Automobile getAutomobile() {
		return auto;
	}

	public void setTicket(int ticket) {
		this.ticket = ticket;
	}

	public int getTicket() {
		return ticket;
	}

	public void run() {
		try {
			// arriva al parcheggio
			sleep(100 * Math.round(Math.random()));

			// entra nel parcheggio
			System.out.println("Vuole entrare auto: " + this.getAutomobile().getTarga());
			parcheggio.parcheggia();

			// permanenza random
			sleep(4000 + Math.round(Math.random()) * 2000);

			// esce dal parcheggio
			System.out.println("Vuole uscire auto: " + this.getAutomobile().getTarga());
			parcheggio.ritiraAuto(this);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void startClient() throws IOException, InterruptedException, ClassNotFoundException {

		String input;

		PrintWriter outFirst = new PrintWriter(this.socket.getOutputStream(), true);

		System.out.println("richiesta lista parcheggi disponibili? (yes/no)");

		input = tastiera.nextLine();

		outFirst.println(input);
		outFirst.flush();

		if (input.equalsIgnoreCase("yes")) {
			InputStream inputStream = socket.getInputStream();

			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

			ArrayList<Parcheggio> parcheggiDisponibili = (ArrayList<Parcheggio>) objectInputStream.readObject();
			System.out.println("Ricevuti [" + parcheggiDisponibili.size() + "] messagi da: " + socket);

			System.out.println("Parcheggi disponibili:");

			parcheggiDisponibili.forEach(
					(parcheggio) -> System.out.println("parcheggio disponibile " + parcheggio.getIdParcheggio()));

			System.out.println("scegli il parcheggio digitando il numero corrispondente");

			int parcheggioScelto;
			parcheggioScelto = tastiera.nextInt();

			parcheggiDisponibili.forEach((parcheggio) -> {
				if (parcheggioScelto == parcheggio.getIdParcheggio())
					this.setParcheggio(parcheggio);
			});

			PrintWriter outSecond = new PrintWriter(this.socket.getOutputStream(), true);

			if (this.getParcheggio() == null) {
				System.out.println("parcheggio selezionato non valido");
				input = "";
			} else {
				System.out.println("mando la targa dell' auto e la scelta del parcheggio al server");
				input = auto.getTarga() + "-" + parcheggioScelto;
			}

			outSecond.println(input);
			outSecond.flush();
		} else {
			System.out.println("termino");
		}

		socket.close();
	}

	public static void main(String[] args) throws Exception {
		try {
			InetAddress serverAddress = InetAddress.getByName("0.0.0.0"); // 0.0.0.0

			Automobile[] auto = new Automobile[5];
			for (int i = 0; i < auto.length; ++i) {
				auto[i] = new Automobile(i);
			}
			Automobilista[] client = new Automobilista[5];

			for (int i = 0; i < client.length; ++i) {
				client[i] = new Automobilista(auto[i]);
				client[i].setSocket(serverAddress, 12345);// 12345
				System.out.println("Connected to: " + client[i].socket.getInetAddress());
				client[i].startClient();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
