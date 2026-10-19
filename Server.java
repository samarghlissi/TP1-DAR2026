package serverPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;

public class Server {
    private static final int PORT = 1234;

    
    private static InetAddress trouverAdresseReelle() throws IOException {
       
        try (DatagramSocket ds = new DatagramSocket()) {
            ds.connect(InetAddress.getByName("8.8.8.8"), 10002);
            InetAddress a = ds.getLocalAddress();
            if (a instanceof Inet4Address && !a.isAnyLocalAddress() && !a.isLoopbackAddress()) {
                return a;
            }
        } catch (IOException e) {
            
        }
       
        for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            String nom = (ni.getDisplayName() + " " + ni.getName()).toLowerCase();
            if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()
                    || nom.contains("virtual") || nom.contains("vmware")
                    || nom.contains("vbox") || nom.contains("host-only")) {
                continue;
            }
            for (InetAddress a : Collections.list(ni.getInetAddresses())) {
                if (a instanceof Inet4Address && a.isSiteLocalAddress()) {
                    return a;
                }
            }
        }
        
        return InetAddress.getLocalHost();
    }

    public static void main(String[] args) {
        try {
            
            InetAddress adresse = (args.length > 0)
                    ? InetAddress.getByName(args[0])
                    : trouverAdresseReelle();

            try (ServerSocket serverSocket = new ServerSocket()) {
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(adresse, PORT));
                System.out.println("Je suis un serveur en attente de la connexion d'un client");
                System.out.println("Adresse : " + adresse.getHostAddress() + "  Port : " + PORT);

                try (Socket socket = serverSocket.accept()) {
                    System.out.println("un client est connecté : "
                            + socket.getInetAddress().getHostAddress());

                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    while (true) {
                        int choix = in.readInt();   
                        if (choix == 0) {
                            System.out.println("Le client a quitté : fin de la communication");
                            break;
                        }
                        double x = in.readDouble();
                        double y = in.readDouble();
                        System.out.println("Reçu : choix=" + choix + " x=" + x + " y=" + y);

                        double resultat = 0;
                        int statut = 0;            
                        switch (choix) {
                            case 1: resultat = x + y; break;
                            case 2: resultat = x - y; break;
                            case 3: resultat = x * y; break;
                            case 4:
                                if (y == 0) statut = 1;  
                                else resultat = x / y;
                                break;
                            default: statut = 1;          
                        }
                        out.writeInt(statut);
                        out.writeDouble(resultat);
                        out.flush();
                        System.out.println("Envoyé : statut=" + statut + " résultat=" + resultat);
                    }
                } catch (EOFException e) {
                    System.out.println("Le client s'est déconnecté brutalement");
                }
            }
            System.out.println("Serveur arrêté");
        } catch (IOException e) {
            System.out.println("Erreur serveur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}