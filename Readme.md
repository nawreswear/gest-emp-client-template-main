# Template pour Gestion Emploi Client de gestion de calendrier avec Javalin

Ce projet a été converti pour utiliser Maven.

Commandes utiles:
- mvn clean test
- mvn -q clean package javafx:run

Prérequis:
- Java 21 (modifiable dans le pom.xml via maven.compiler.source/target)


## How to run

- Compiler et tester:
  - ./mvnw clean test (Windows: mvnw.cmd clean test)
- Packager:
  - ./mvnw package
- Exécuter l’application:
  - mvn javafx:run

Ce profil nécessite le plugin exec configuré dans le pom.xml.
