# Améliorations pour l'Application TV-CAM

## Fonctionnalités ajoutées

### 1. 🗺️ **Gestion des Zones Géographiques**

#### Nouvelles entités :

- **Zone** : Représente les zones géographiques
- **user_zones** : Table de liaison pour assigner les utilisateurs aux zones

#### Fonctionnalités :

- Création, modification, suppression de zones
- Attribution d'utilisateurs à une ou plusieurs zones
- Attribution de clients à des zones
- Restriction d'accès par zone pour chaque utilisateur

#### API Endpoints :

```
POST   /api/v1/zones                          - Créer une zone
GET    /api/v1/zones                          - Lister les zones
GET    /api/v1/zones/active                   - Lister les zones actives
GET    /api/v1/zones/{id}                     - Détails d'une zone
PUT    /api/v1/zones/{id}                     - Modifier une zone
DELETE /api/v1/zones/{id}                     - Supprimer une zone
POST   /api/v1/zones/{zoneId}/users/{userId}  - Assigner utilisateur à zone
DELETE /api/v1/zones/{zoneId}/users/{userId}  - Retirer utilisateur de zone
```

### 2. 🎫 **Système de Réclamations/Incidents (Issues)**

#### Nouvelle entité Issue :

- Types d'incidents : Problème technique, Panne d'équipement, Poteau tombé, Pas d'image, etc.
- Priorités : Faible, Moyenne, Élevée, Urgente
- Statuts : Ouvert, En cours, En attente, Résolu, Fermé, Annulé
- Assignation aux techniciens
- Système de feedback client

#### Fonctionnalités :

- Création de réclamations par les clients
- Assignation automatique ou manuelle aux techniciens
- Suivi du cycle de vie des incidents
- Évaluation du service après résolution
- Statistiques des réclamations

#### API Endpoints :

```
POST   /api/v1/issues                         - Créer une réclamation
GET    /api/v1/issues                         - Lister les réclamations
GET    /api/v1/issues/{id}                    - Détails d'une réclamation
PUT    /api/v1/issues/{id}                    - Modifier une réclamation
PUT    /api/v1/issues/{id}/assign/{userId}    - Assigner à un technicien
PUT    /api/v1/issues/{id}/resolve            - Résoudre une réclamation
PUT    /api/v1/issues/{id}/close              - Fermer une réclamation
PUT    /api/v1/issues/{id}/feedback           - Évaluer le service
```

### 3. 💰 **Système de Rabais pour Paiements Avancés**

#### Nouvelle entité Discount :

- Rabais configurables par fréquence de paiement
- Pourcentages de rabais : Trimestriel (5%), Semestriel (8%), Annuel (12%)
- Montants minimum et maximum de rabais

#### Exemples de calculs :

- **Mensuel** : 2000F/mois (pas de rabais)
- **Trimestriel** : 6000F → 5700F (économie de 300F)
- **Semestriel** : 12000F → 11040F (économie de 960F)
- **Annuel** : 24000F → 21120F (économie de 2880F)

#### Service BillCalculationService :

- Calcul automatique des montants avec rabais
- Génération de résumés de facturation détaillés
- Vérification d'éligibilité aux rabais

### 4. 👥 **Nouveaux Rôles Métier**

#### Rôles ajoutés :

- **CHEF_CABLEUR** : Supervision d'équipe, gestion des zones
- **TECHNICIEN** : Maintenance et réparations
- **RECOUVREUR** : Gestion des paiements et recouvrement
- **MANAGER** : Gestion générale

#### Permissions par rôle :

- **Admin** : Accès complet
- **Chef Câbleur** : Gestion équipe, attribution zones, supervision incidents
- **Technicien** : Résolution incidents, mise à jour statuts
- **Recouvreur** : Gestion paiements, facturation, recouvrement

### 5. 🔒 **Sécurité Basée sur les Zones**

#### Service ZoneSecurityService :

- Vérification automatique des accès par zone
- Filtrage des données selon les zones de l\'utilisateur
- Méthodes utilitaires pour les contrôles d\'accès

#### Fonctionnalités :

- Un câbleur ne voit que les clients de ses zones
- Isolation des données par zone géographique
- Contrôle d\'accès granulaire

### 6. 📊 **Tableau de Bord et Statistiques**

#### Nouveau contrôleur Dashboard :

- Statistiques générales du système
- Métriques par zone
- Performance des équipes
- Alertes et notifications

#### Métriques disponibles :

- Nombre de clients par zone
- Taux de résolution des incidents
- Performance des paiements
- Activités récentes

## Améliorations de la Base de Données

### Nouvelles tables :

```sql
-- Table des zones
CREATE TABLE zones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    address VARCHAR(500),
    created_at VARCHAR(50),
    updated_at VARCHAR(50)
);

-- Table des réclamations
CREATE TABLE issues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    issue_type VARCHAR(50) NOT NULL,
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    status VARCHAR(20) DEFAULT 'OPEN',
    reference_number VARCHAR(100) UNIQUE,
    customer_id BIGINT NOT NULL,
    assigned_to BIGINT,
    resolved_at TIMESTAMP,
    due_date TIMESTAMP,
    resolution TEXT,
    customer_rating INT,
    customer_feedback TEXT
);

-- Table des rabais
CREATE TABLE discounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_frequency VARCHAR(50) NOT NULL,
    discount_percentage DECIMAL(5,2) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    minimum_amount DECIMAL(10,2),
    maximum_discount_amount DECIMAL(10,2)
);

-- Table de liaison utilisateur-zones
CREATE TABLE user_zones (
    user_id BIGINT NOT NULL,
    zone_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, zone_id)
);
```

### Modifications des tables existantes :

```sql
-- Ajout colonne zone aux clients
ALTER TABLE customers ADD COLUMN zone_id BIGINT;
ADD FOREIGN KEY (zone_id) REFERENCES zones(id);
```

## Configuration et Initialisation

### Données par défaut :

- Rôles métier créés automatiquement
- Rabais par défaut configurés
- Zones d'exemple (optionnel)

### Script Liquibase :

Le fichier `004-add-zones-issues-discounts.xml` contient toutes les migrations nécessaires.

## Sécurité et Contrôle d'Accès

### Annotations de sécurité :

```java
@PreAuthorize("hasRole('ADMIN') or hasRole('CHEF_CABLEUR')")
@PreAuthorize("hasRole('TECHNICIEN') or hasRole('CHEF_CABLEUR')")
```

### Filtrage automatique par zone :

Les requêtes sont automatiquement filtrées selon les zones de l'utilisateur connecté.

## Guide d'Utilisation

### Pour un Chef Câbleur :

1. Gérer les zones sous sa responsabilité
2. Assigner les techniciens aux zones
3. Superviser les réclamations
4. Consulter les statistiques de performance

### Pour un Technicien :

1. Consulter les réclamations assignées
2. Mettre à jour le statut des interventions
3. Résoudre les incidents
4. Accéder uniquement aux clients de ses zones

### Pour un Recouvreur :

1. Gérer les paiements et facturations
2. Appliquer les rabais selon les fréquences
3. Suivre les arriérés de paiement
4. Accéder aux clients de ses zones

### Pour un Client :

1. Signaler des incidents/pannes
2. Suivre le statut de ses réclamations
3. Évaluer le service après résolution
4. Bénéficier de rabais pour paiements avancés

## Prochaines Étapes

1. **Tests** : Implémenter les tests unitaires et d'intégration
2. **Interface Angular** : Adapter le frontend pour les nouvelles fonctionnalités
3. **Notifications** : Système de notifications en temps réel
4. **Rapports** : Génération de rapports PDF détaillés
5. **Mobile** : Application mobile pour les techniciens

## Technologies Utilisées

- **Backend** : Spring Boot 3.4.3, JPA/Hibernate
- **Base de données** : MySQL avec Liquibase
- **Sécurité** : Spring Security avec JWT
- **Documentation** : Swagger/OpenAPI
- **Validation** : Bean Validation
- **Logging** : SLF4J + Logback

Cette implémentation répond parfaitement aux besoins spécifiés :
✅ Gestion des zones géographiques
✅ Système de réclamations/incidents
✅ Rabais pour paiements avancés  
✅ Rôles métier spécialisés
✅ Restriction d'accès par zone
✅ Historique des paiements détaillé
✅ Paiements par tranches
✅ Système de rabais configurables
