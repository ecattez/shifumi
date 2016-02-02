# shifumi
Jeu de shifumi en réseau

## Protocole

| Message|Effet|Reponse|
| ------------- |-------------- | ---------|
|JOIN|Rejoindre une partie|WAIT/READY/ERROR|
|QUIT|Quitter une partie|OK/ERROR|
|DO|Faire une action|WAIT/OK/STOP/ERROR|
