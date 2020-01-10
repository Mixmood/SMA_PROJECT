# Projet : Agents intelligents interagissant
## Allocation de ressources dans un système distribué

**Jordan Aurey, Timothée Denoux, Romain Derré, Adrien Vizier**

## Configuration
#### Contenu
Le projet est composé de 10 fichiers Java représentant 10 classes regroupé dans le *package* `examples.JINProject`:
* `Task`
* `Number`
* `Parameters`


* `JINContractNetInitiatorAgent`
* `JINContractNetResponderAgent`
* `JINProjectInitiator`


* `RandomContractNetInitiatorAgent`
* `RandomContractNetResponderAgent`
* `RandomProjectInitiator`

* `ProjectStatisticsWritter`


## Procédure de lancement
#### Un seul type d'agent
Pour utiliser le projet avec un seul type d'agent (JIN ou Random), ajoutez un seul agent `<JIN/Random>ProjectInitiator`. Celui-ci crée les autres agents la simulation s'exécute.

Il prend 0, 3 ou 4 arguments (séparé par des virgules) :
> * nombre de contractant
> * nombre de compétences/contraintes
> * nombre de tâches
> * "seed" pour ne pas avoir toujours les mêmes agents et tâches
>
> (pas d'argument ou nombre d'argument incorrect => valeurs par défaut)

On obtient les résultats dans le terminal.

***Exemple :***
```
Résultats (JIN) :
10 agents
10 compétences/contraintes
20 tâches -> valeur : 860
10 tâches accomplies -> valeur : 492
Valeur de réalisation (satisfaction) : 366
Rapport de réussite globale : 0.4255813953488372
Rapport de réussite des tâches terminées : 0.7439024390243902
```

#### Comparaison JIN / Random
Pour utiliser le projet en comparant entre JIN et Random, ajoutez un seul agent `ProjectStatisticsWritter`. Celui-ci crée un `JINProjectInitiator` et un `RandomProjectInitiator` avec les mêmes paramètres, attend a fin de la simulation puis recommence avec des paramètres différents.

Il prend 0 ou 1 paramètre :
> * nombre de simulation
>
> (pas d'argument ou nombre d'argument incorrect => valeur par défaut)

On obtient les résultats dans des fichiers **JINOutput.csv** et **RandomOutput.csv** sous la forme de tableaux.
