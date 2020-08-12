La persistance des données dans Kafka est un sujet qui est souvent lié au cas d'usage.  
A travers un découplage du compute et du storage, Confluent propose une fonctionalité de Tiered Storage qui permet de persister les données sur longue période, avec des solutions de stockage à faible coût.
Une porte s'ouvre ainsi vers de nouveaux cas d'usage : 
- Tableaux de bord comparatifs
- Machine Learning au fil de l'eau
- Jeu (et rejeu) de données
- Utilisation de Kafka comme System of Record

Cette brève présentation suivi d’une démo de bout en bout vous montre comment :
- Les données sont persistées localement pendant une courte période.
- Les données demeurent dans AWS S3 (plusieurs mois / années si nécessaire).
- L'offset étant préservé, ces mécanismes sont transparents pour les consommateurs. 
