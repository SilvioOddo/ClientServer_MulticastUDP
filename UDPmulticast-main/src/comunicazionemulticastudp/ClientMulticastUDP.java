package comunicazionemulticastudp;

import static comunicazionemulticastudp.ServerMulticastUDP.ANSI_BLUE;
import java.io.*;
import java.net.*;

/**
 *
 * @author Luca Brunori-Gabriele Silla-Silvio Oddo
 */

public class ClientMulticastUDP {
    // Definizione di costanti per il colore del testo in console
    public static final String RED_BOLD = "\033[1;31m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String GREEN_UNDERLINED = "\033[4;32m";
    public static final String RESET = "\033[0m";

    public static void main(String[] args) {
        // Porta per la comunicazione unicast
        int port = 2000;
        // Porta per la comunicazione multicast
        int portGroup = 1900;
        InetAddress serverAddress;
        DatagramSocket dSocket = null;
        MulticastSocket mSocket = null;
        InetAddress group;
        DatagramPacket outPacket;
        DatagramPacket inPacket;
        // Buffer per la ricezione dei dati in modalità unicast e multicast
        byte[] inBuffer = new byte[256];
        byte[] inBufferG = new byte[1024];
        // Messaggio da inviare al server
        String messageOut = "Richiesta comunicazione";
        String messageIn;

        try {
            // Inizializzazione e configurazione del client
            System.out.println(RED_BOLD + "CLIENT UDP" + RESET);
            serverAddress = InetAddress.getLocalHost(); // Ottieni l'indirizzo IP del server
            System.out.println(RED_BOLD + "Connessione al server in corso..." + RESET);
            dSocket = new DatagramSocket(); // Crea un socket per la comunicazione unicast
            // Invia il messaggio di richiesta al server
            outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), serverAddress, port);
            dSocket.send(outPacket);
            System.out.println(RED_BOLD + "Richiesta inviata al server." + RESET);
            // Ricevi la risposta dal server
            inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            dSocket.receive(inPacket);
            messageOut = new String(inPacket.getData(), 0, inPacket.getLength());
            System.out.println(ANSI_BLUE + "Risposta ricevuta dal server:" + RESET);
            messageIn = new String(inPacket.getData(), 0, inPacket.getLength());
            System.out.println(ANSI_BLUE + "Messaggio ricevuto dal server: " + messageIn + RESET);

            // Configurazione e ricezione dei messaggi dal gruppo multicast
            mSocket = new MulticastSocket(portGroup); // Crea un socket per la comunicazione multicast
            group = InetAddress.getByName("239.255.255.250"); // Indirizzo IP del gruppo multicast
            mSocket.joinGroup(group); // Aggiungi il client al gruppo multicast
            // Ricevi il messaggio dal gruppo multicast
            inPacket = new DatagramPacket(inBufferG, inBufferG.length);
            mSocket.receive(inPacket);
            messageIn = new String(inPacket.getData(), 0, inPacket.getLength());
            System.out.println(GREEN_UNDERLINED + "Messaggio ricevuto dal gruppo: " + messageIn + RESET);
            mSocket.leaveGroup(group); // Lascia il gruppo multicast
        } catch (UnknownHostException ex) {
            System.err.println("Errore di risoluzione host");
        } catch (SocketException ex) {
            System.err.println("Errore di creazione socket");
        } catch (IOException ex) {
            System.err.println("Errore di I/O");
        } finally {
            // Chiusura dei socket
            if (dSocket != null)
                dSocket.close();
            if (mSocket != null)
                mSocket.close();
        }
    }
}
