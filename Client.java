package clientPackage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private static final int PORT = 1234;

    private static int lireEntier(Scanner sc, String message, int min, int max) {
        while (true) {
            System.out.print(message);
            String ligne = sc.nextLine().trim();
            try {
                int n = Integer.parseInt(ligne);
                if (n >= min && n <= max) return n;
            } catch (NumberFormatException e) {

            }
            System.out.println("Saisie invalide, recommencez.");
        }
    }


    private static double lireDouble(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String ligne = sc.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(ligne);
            } catch (NumberFormatException e) {
                System.out.println("Saisie invalide, entrez un nombre.");
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String ipServeur;
        if (args.length > 0) {
            ipServeur = args[0];
        } else {
            System.out.print("Adresse IP du serveur (ex. 192.168.1.15) : ");
            ipServeur = sc.nextLine().trim();
        }

        System.out.println("Je suis un client pas encore connecté…");
        try (Socket socket = new Socket()) {
            InetAddress adresse = InetAddress.getByName(ipServeur);
            socket.connect(new InetSocketAddress(adresse, PORT), 5000); 
            System.out.println("je suis un client connecté à " + adresse.getHostAddress() + ":" + PORT);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                System.out.println("\n===== CALCULATRICE =====");
                System.out.println("1. Addition");
                System.out.println("2. Soustraction");
                System.out.println("3. Multiplication");
                System.out.println("4. Division");
                System.out.println("0. Quitter");
                int choix = lireEntier(sc, "Votre choix : ", 0, 4);

                if (choix == 0) {
                    out.writeInt(0);               
                    out.flush();
                    break;
                }

                double x = lireDouble(sc, "x = ");
                double y = lireDouble(sc, "y = ");

                out.writeInt(choix);
                out.writeDouble(x);
                out.writeDouble(y);
                out.flush();

                int statut = in.readInt();
                double resultat = in.readDouble();
                if (statut == 0) {
                    String[] symboles = {"", "+", "-", "*", "/"};
                    System.out.println(x + " " + symboles[choix] + " " + y + " = " + resultat);
                } else {
                    System.out.println("Erreur : opération impossible (division par zéro ?)");
                }
            }
            System.out.println("Client déconnecté");   
        } catch (UnknownHostException e) {
            System.out.println("Adresse IP invalide : " + ipServeur);
        } catch (SocketTimeoutException e) {
            System.out.println("Délai dépassé : serveur injoignable (mauvaise IP, autre réseau ou pare-feu ?)");
        } catch (ConnectException e) {
            System.out.println("Connexion refusée : le serveur n'est pas lancé ou le port est bloqué.");
        } catch (IOException e) {
            System.out.println("Erreur de communication : " + e.getMessage());
        }
    }
}


