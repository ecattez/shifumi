# shifumi
## Jeu de shifumi en réseau
### Edouard CATTEZ - Julien LELEU

----------------

## Protocole

### Client

| Message| Paramètre(s) | Effet|Reponse|
|:------------|:------------|:-------------|:--------|
|JOIN|USERNAME|Rejoindre une partie|WAIT / READY / ERROR|
|QUIT|USERNAME|Quitter une partie|L_GAME / ERROR|
|DO|USERNAME:ACTION|Faire une action|WAIT / END_ROUND / END_GAME / STOP / ERROR|

### Serveur

| Message| Paramètre(s) | Effet|
|:------------|:------------|:-------------|
|WAIT|OPTION|En attente d'infos externes supplèmentaires|
|READY|OPTION|Démarrer une partie|
|END_ROUND|USERNAME|Fin de la manche|
|END_GAME|USERNAME|Fin de la partie|
|STOP|-|Arrêter une partie|
|ERROR|MESSAGE|Une erreur s'est produite|

### Normes

|Acteur|Message|
|:------------|:-------------|
| Client|ACTION:USERNAME:OPTION|
| Serveur|STATUT:OPTION|

#### Exemple

- Client1:		`JOIN:Etienne:`
 
*En attente d'un second client*

- Serveur:	`WAIT:`
- Client2:		`JOIN:Jordy:`

*Début de la partie*

- Serveur:	`READY:`

*Début de la première manche*

- Client1: `DO:Etienne:ROCK`
- Serveur: `WAIT:`
- Client2: `DO:Jordy:PAPER`

*Gagnant de la 1ère manche : Jordy*

- Serveur: `END_ROUND:Jordy`

*Seconde manche*

- Client2: `DO:Jordy:SCISSORS`
- Serveur: `WAIT:`
- Client1: `DO:Etienne:ROCK`

*Gagnant de la 2ème manche : Etienne*

- Serveur: `END_ROUND:Etienne`

*Troisième manche*

- Client1: `DO:Etienne:PAPER`
- Serveur: `WAIT:`
- Client2: `DO:Jordy:SCISSORS`

*Fin de la partie : Jordy est le vainqueur*

- Serveur: `END_GAME:Jordy`
