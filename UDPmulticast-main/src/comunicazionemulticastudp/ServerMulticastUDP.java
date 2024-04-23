package comunicazionemulticastudp;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luca Brunori-Gabriele Silla-Silvio Oddo
 */
public class ServerMulticastUDP {
    // Definizione di costanti per il colore del testo in console
    public static final String ANSI_BLUE = "\u001B[34m"; // Colore del prompt del Server
    public static final String RED_BOLD = "\033[1;31m"; // Colore del prompt del Client
    public static final String RESET = "\033[0m"; // Colore reset

    public static void main(String[] args) {
        // Porta del server
        int port = 2000;
        // Socket UDP per la comunicazione
        DatagramSocket dSocket = null;
        // Datagramma UDP ricevuto dal client
        DatagramPacket inPacket;
        // Datagramma UDP di risposta da inviare
        DatagramPacket outPacket;
        // Buffer per il contenuto del segmento da ricevere
        byte[] inBuffer;
        // Indirizzo del gruppo Multicast UDP
        InetAddress groupAddress;
        // Messaggio ricevuto
        String messageIn;
        // Messaggio da inviare
        String messageOut;

        try {
            // Inizializzazione e avvio del server
            System.out.println(ANSI_BLUE + "SERVER UDP" + RESET);
            // 1) Apertura della porta di ascolto del server
            dSocket = new DatagramSocket(port);
            System.out.println(ANSI_BLUE + "Apertura porta in corso!" + RESET);

            while (true) {
                // 2) Ricezione messaggio dal client
                inBuffer = new byte[256]; // Preparazione del buffer per il messaggio da ricevere
                inPacket = new DatagramPacket(inBuffer, inBuffer.length); // Creazione del datagramma per ricevere il messaggio
                dSocket.receive(inPacket); // Attesa e ricezione del pacchetto dal client

                // Recupero dell'indirizzo IP e della porta UDP del client
                InetAddress clientAddress = inPacket.getAddress();
                int clientPort = inPacket.getPort();

                // Stampare a video il messaggio ricevuto dal client
                messageIn = new String(inPacket.getData(), 0, inPacket.getLength());
                System.out.println(RED_BOLD + "Messaggio ricevuto dal client " + clientAddress +
                        ":" + clientPort + "\n\t" + messageIn + RESET);

                // 3) Risposta al client
                messageOut = "Ricevuta richiesta!";
                outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), clientAddress, clientPort);
                dSocket.send(outPacket); // Invio dei dati al client
                System.out.println(ANSI_BLUE + "Spedito messaggio al client: " + messageOut + RESET);

                // 4) Invio messaggio al gruppo multicast
                groupAddress = InetAddress.getByName("239.255.255.250"); // Recupero l'indirizzo IP del gruppo
                int groupPort = 1900; // Inizializzazione della porta del gruppo

                // Preparazione del datagramma con i dati da inviare al gruppo multicast
                messageOut = "Benvenuti a tutti!";
                outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), groupAddress, groupPort);
                dSocket.send(outPacket); // Invio dei dati al gruppo multicast
                System.out.println(ANSI_BLUE + "Spedito messaggio al gruppo: " + messageOut + RESET);
            }
        } catch (BindException ex) {
            System.err.println("Porta già in uso");
        } catch (SocketException ex) {
            System.err.println("Errore di creazione del socket e apertura del server");
        } catch (UnknownHostException ex) {
            System.err.println("Errore di risoluzione");
        } catch (IOException ex) {
            System.err.println("Errore di I/O");
        } finally {
            // Chiusura del socket
            if (dSocket != null)
                dSocket.close();
        }
    }
}
