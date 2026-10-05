# API REST Java - Calculatrice, BMI et conversion de devises

Ce projet est une application API REST développée avec Java et Spring Boot. Il propose plusieurs services utiles en ligne :

- authentification avec JWT,
- calculatrice de base avec historique,
- calcul de l'IMC (BMI) avec suivi utilisateur,
- conversion de devises avec API externe et stockage des transactions.

L'application est structurée en contrôleurs, services, repositories, entités, DTO et sécurité JWT.

## 1. Objectif du projet

Le but de ce projet est de mettre en pratique la création d'une API REST complète avec :

- des endpoints HTTP REST,
- une persistance avec PostgreSQL,
- la validation des entrées,
- la gestion des erreurs côté serveur,
- l'authentification par JWT,
- la mise en cache des taux de change,
- la sécurisation des routes sensibles.

## 2. Fonctionnalités disponibles

### Authentification

- inscription d'un utilisateur,
- connexion avec génération de jetons JWT,
- rafraîchissement du token d'accès,
- déconnexion via refresh token,
- protection de certaines routes avec Spring Security.

### Calculatrice

- opérations mathématiques simples : addition, soustraction, multiplication, division,
- enregistrement des opérations dans la base,
- historique paginé,
- mise à jour et suppression d'une opération.

### BMI / IMC

- calcul de l'IMC à partir du poids et de la taille,
- association du calcul à l'utilisateur authentifié,
- historique personnel,
- mise à jour et suppression des enregistrements,
- statistiques globales sur les catégories BMI.

### Conversion de devises

- conversion de montant selon des devises,
- récupération des taux via une API externe (Frankfurter),
- conversion manuelle avec taux stocké localement,
- mise à jour d'un taux,
- liste des transactions.

## 3. Stack technique

- Java 21
- Spring Boot 4.1.1
- Maven
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Spring Security
- JWT (jjwt)
- Caffeine (cache)
- WebFlux (client HTTP pour l'API externe)

## 4. Prérequis

Avant de lancer le projet, vérifiez que vous avez :

- Java 21 installé,
- Maven installé ou l'utilisation de `mvnw` fournie,
- PostgreSQL installé et démarré,
- une base de données nommée `apirest` accessible sur `localhost:5432`.

La configuration de la base est dans le fichier :

- `src/main/resources/application.properties`

## 5. Configuration

Le projet utilise PostgreSQL avec cette configuration de base :

- base : `apirest`
- host : `localhost`
- port : `5432`
- utilisateur : `postgres`

Le mot de passe est défini dans `application.properties` et doit correspondre à votre instance PostgreSQL locale.

Fichier principal de configuration :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/apirest
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

## 6. Installation et lancement

### Sous Windows

```bash
cd "d:\GENIE_INFORMATIQUE\M1-Efrei-26-27\API&Web_Services\TP\apirest"
./mvnw.cmd spring-boot:run
```

### Sous Linux / macOS

```bash
cd /chemin/vers/apirest
./mvnw spring-boot:run
```

L'application démarre par défaut sur :

- http://localhost:8080

## 7. Endpoints principaux

### Authentification

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Inscription d'un utilisateur |
| POST | `/api/auth/login` | Connexion et génération des tokens |
| POST | `/api/auth/refresh` | Renouvellement du token d'accès |
| POST | `/api/auth/logout` | Invalidation du refresh token |

### Calculatrice

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/calculator` | Effectuer un calcul |
| GET | `/api/calculator/history` | Historique des opérations |
| PUT | `/api/calculator/{id}` | Modifier une opération |
| DELETE | `/api/calculator/{id}` | Supprimer une opération |

### BMI

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/bmi/calculate` | Calculer un IMC |
| GET | `/api/bmi/history` | Historique BMI de l'utilisateur |
| PUT | `/api/bmi/update/{id}` | Modifier un calcul BMI |
| DELETE | `/api/bmi/delete/{id}` | Supprimer un calcul BMI |
| GET | `/api/bmi/statistics` | Statistiques globales |

### Conversion de devises

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/currency/convert` | Conversion avec taux en ligne |
| POST | `/api/currency/convert` | Conversion avec taux manuel |
| PUT | `/api/currency/update-rate` | Modifier un taux local |
| DELETE | `/api/currency/delete-transaction/{id}` | Supprimer une transaction |
| GET | `/api/currency/transactions` | Liste des transactions |

## 8. Exemple d'utilisation

### Inscription

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "MotDePasse123!"
  }'
```

### Connexion

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "MotDePasse123!"
  }'
```

### Utilisation d'un token JWT

```bash
curl -X GET http://localhost:8080/api/bmi/history \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Calculatrice

```bash
curl -X POST http://localhost:8080/api/calculator \
  -H "Content-Type: application/json" \
  -d '{
    "leftOperand": 10,
    "rightOperand": 5,
    "operator": "+"
  }'
```

## 9. Structure du projet

```text
apirest/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/apirestjava/example/apirest/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── exception/
│   │   │       ├── filter/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       └── service/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
└── README.md
```

## 10. Sécurité et bonnes pratiques

- Les routes `/api/bmi/**` sont protégées par JWT.
- Les routes d'authentification sont publiques pour l'inscription et la connexion.
- Les mots de passe ne sont pas stockés en clair : ils sont hachés avec BCrypt.
- Les tokens sont gérés côté serveur avec rotation du refresh token.
- Les taux de change sont mis en cache pour limiter les appels externes.

## 11. Commandes utiles

### Compiler le projet

```bash
./mvnw clean package
```

### Exécuter les tests

```bash
./mvnw test
```

### Démarrer l'application

```bash
./mvnw spring-boot:run
```

## 12. Notes

Ce projet est un exemple d'API REST Java avec logique métier intégrée. Il est conçu pour une utilisation pédagogique et de démonstration. Vous pouvez l'adapter selon vos besoins en ajoutant des validations supplémentaires, des rôles utilisateurs, des tests d'intégration ou une vraie base de données de production.

Si vous souhaitez aller plus loin, les prochaines améliorations possibles sont :

- ajout de rôles administrateur,
- tests unitaires et d'intégration,
- documentation OpenAPI/Swagger,
- pagination avancée des résultats,
- gestion plus robuste des erreurs et des logs.
