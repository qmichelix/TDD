# Book Management Application
Ce projet basé sur Kotlin est conçu pour gérer une collection de livres, démontrant une architecture propre et une approche de développement piloté par les tests (TDD).

# Structure du Projet
Le projet suit une architecture propre, organisée en différentes couches :

domain/: Contient la logique métier principale.
model/: Modèles de données, par exemple Book.kt.
port/: Interfaces de port pour les interactions externes.
usecase/: Cas d'utilisation métier.
infrastructure/: Cadres et pilotes.
application/: Configuration des cas d'utilisation.
driven/adapter/: Implémentations des interfaces de port.
driver/web/: Contrôleurs et interfaces web.
test/: Contient tous les tests, y compris les tests unitaires, d'intégration et de composants.
Modifications
Test d'Intégration (BookDAOIT.kt)
Ajustement des requêtes SQL pour une insertion et récupération précises des données.
Correction des assertions pour correspondre aux données attendues.
Tests d'Architecture (ArchitectureTest.kt)
Vérification et confirmation de la correction des tests d'architecture.
Tests de Composants avec Cucumber
Modification de BookStepDefs.kt pour une validation précise des réponses.
Ajustement des assertions pour correspondre aux données JSON attendues.
Ajout d'impression de réponse pour le débogage.
Fichier de Fonctionnalités Cucumber (book.feature)
Mise à jour pour correspondre aux attentes des tests de composants.
Formatage des données pour la cohérence.
Validation des Réponses dans les Tests
Ajustement de la logique de validation pour gérer les réponses vides ou nulles.
Ajout de Fonctionnalités et Scénarios dans book.feature
Ajout de nouveaux scénarios pour créer, récupérer et réserver des livres.
Modification des scénarios existants pour une meilleure alignement avec les tests.
Ajustements dans CucumberRunnerTest.kt
Assurer la configuration correcte pour les tests Cucumber avec le contexte Spring Boot.
Ajustements Généraux et Améliorations
Correction de problèmes mineurs et amélioration de la clarté et de la maintenabilité du code.
Intégration et Déploiement Continus
L'intégration et le déploiement continus sont gérés via GitHub Actions, exécutant des tests à chaque push et pull request.

# Comment Utiliser
Pour configurer et exécuter le projet :

Cloner le dépôt.
Construire le projet avec Gradle : ./gradlew build.
Exécuter les tests : ./gradlew test.
Informations Complémentaires
Le projet utilise Kotlin avec Spring Boot.
Les frameworks de test incluent JUnit, AssertK et Cucumber.
Les interactions avec la base de données sont testées à l'aide de Testcontainers avec un conteneur PostgreSQL.
Statut du Projet


![image](https://github.com/qmichelix/TDD/assets/109591838/5221fb6d-73f9-41ad-a550-96bd6bf8d1eb)

Le build s'est fait en 5min et 7 secondes en passant tous les tests :D à la 67ème execution du workflow.
