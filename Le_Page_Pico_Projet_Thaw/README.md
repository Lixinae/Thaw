#Explications de l'arborescence

        Lors du rendu, nous avons délibérément mis quelques 
    dossiers et fichiers nécessaire au bon fonctionnement de l'application.
        Ces derniers sont :
            - Le dossier db/ qui contiendra la base de données utilisées par 
              l'application.
            
            - Un dossier config avec un dossier webserver qui servira à stocker
              le certificat auto signée nécessaire au bon fonctionnement du 
              serveur.
              
            - Un dossier logs qui stockera les différents logs du serveur pout
              permettre une analyse des évènements qui se sont déroulés pendant
              l'exécution de ce dernier.
              
            - Finalement, nous avons aussi remis deux script supplémentaires à
              savoir launch_server.sh et compile.sh permettant, respectivement
              de lancé le serveur et de compilé les sources. 
              Leurs fonctionnement est expliquer dans la documentation utilisateur.
              
        Autre point : 
            Vous pourrez remarquez la présence d'un dossier vert.x-3.0.0 contenant
         la librairie Vertx. Contrairement à ce que dit son nom, il s'agit bien
         de la version allégée imposé dans le sujet.
            Nous avions juste gardé ce nom car, au moment de commencer le projet,
         la librairie mlv pour vert.x n'avait pas encore était mise sur la page 
         du sujet.
                