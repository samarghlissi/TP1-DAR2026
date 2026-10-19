TP1-DAR2026 : Les sockets en mode connecté TCP

Étudiante : Ghlissi Samar — LSI 3
Activité 1-1 : Première connexion Client/Serveur

Étape 1 et 2 : vérification de Git et de Java (`git --version` et `java --version`)

(captures/versions.png)

Git (version 2.56.0) et Java (OpenJDK 25) sont installés. La commande `java --version` fonctionne, donc la variable PATH est correcte.

Étapes 3 et 4 : serveur et client

Le serveur crée un `ServerSocket` sur le port 1234 et attend avec `accept()`. Il affiche « Je suis un serveur en attente de la connexion d'un client ». Le client crée un `Socket` vers l'adresse du serveur. Quand la connexion est établie, le client affiche « je suis un client connecté » et le serveur affiche « un client est connecté ». (Captures dans l'activité 1-3.)

Étape 4.f : adresse IP au lieu de localhost
`localhost` est le nom de l'adresse `127.0.0.1` (adresse de la machine elle-même). Remplacer `localhost` par une adresse IP donne le même résultat.

---

Activité 1-2 : Service de calculatrice

Le client envoie un entier au serveur avec `DataOutputStream.writeInt`. Le serveur le lit avec `DataInputStream.readInt`, calcule, puis renvoie le résultat. Un `int` est transmis sur 4 octets, donc **les nombres négatifs sont bien gérés**. Le client répète l'opération jusqu'à la saisie de **0** : la communication s'arrête alors et les sockets sont fermés des deux côtés. (Test avec un nombre négatif dans la capture de l'activité 1-3.)

---

Activité 1-3 : Deux machines et calculatrice améliorée


- serveur :`InetAddress` donne l'adresse IP réelle de la machine, et `ServerSocket.bind(new InetSocketAddress(adresse, 1234))` écoute sur cette adresse et ce port.
- Client : `localhost` est remplacé par l'adresse IP du serveur (saisie au lancement), avec `socket.connect(new InetSocketAddress(adresse, 1234), 5000)`.

Console du serveur (machine 1) :

(captures/Console du serveur)

Console du client (machine 2): 

(captures/Console du client )
Console du client avec le menu et les calculs :

(captures/Console du client calculatrice)
Calculatrice
Le client affiche un menu : 1 Addition, 2 Soustraction, 3 Multiplication, 4 Division, 0 Quitter. Il envoie le choix et les nombres x et y. Le serveur calcule et renvoie le résultat (`double`). La division par zéro est détectée côté serveur et le client affiche une erreur.

| Opération | x | y | Résultat |
|---|---|---|---|
| Addition | 8 | 2 | 10.0 |
| Soustraction | 8 | 2 | 6.0 |
| Multiplication | -3 | 5 | -15.0 |
| Division | 8 | 2 | 4.0 |
| Division | 8 | 0 | Erreur (division par zéro) |

---

Lien du repository
https://github.com/samarghlissi/TP1-DAR2026