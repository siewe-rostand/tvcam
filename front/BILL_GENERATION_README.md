# Système de Génération de Factures TV CAM

## Vue d'ensemble

Le système de génération de factures TV CAM a été amélioré pour inclure les fonctionnalités suivantes :

### 🧾 Format de Facture Standardisé

- **Référence** : Format basé sur l'image `bill.jpeg` dans le dossier `public/`
- **Layout** : Deux factures par page A4 pour optimiser l'impression
- **Contenu** :
  - En-tête avec informations de l'entreprise
  - Détails du client et période de facturation
  - Tableau des montants (abonnement, arriérés, pénalités)
  - Instructions de paiement et notes importantes

### 📅 Contrôle de Génération Mensuelle

- **Restriction** : Une seule génération par mois par client
- **Vérification automatique** : Le système vérifie si des factures existent déjà pour le mois sélectionné
- **Notification utilisateur** : Alerte avant régénération de factures existantes

### 🔔 Système de Notifications

Notifications contextuelles pour :

- Confirmation de génération réussie
- Avertissement pour factures déjà générées
- Erreurs de génération avec messages détaillés
- Progression du traitement

## Composants Principaux

### 1. Enhanced Bill Generation Component

**Fichier** : `enhanced-bill-generation.component.ts`

**Fonctionnalités** :

- Interface utilisateur pour la sélection de clients
- Configuration des paramètres de génération (mois, année, montant)
- Vérification des factures existantes
- Confirmation avant génération/régénération

### 2. Bill Print Component

**Fichier** : `bill-print/bill-print.component.ts`

**Fonctionnalités** :

- Affichage formaté des factures pour impression
- Layout deux-par-page automatique
- Formatage des montants en FCFA
- Gestion des dates localisées

### 3. Bill Template Preview Component

**Fichier** : `bill-template-preview/bill-template-preview.component.ts`

**Fonctionnalités** :

- Prévisualisation du format de référence
- Affichage de l'image `bill.jpeg`
- Instructions d'utilisation

### 4. Services Améliorés

#### Bill Service

- `checkExistingBillsForMonth()` : Vérification des factures existantes
- Validation mensuelle avant génération

#### Bill Management Service

- `generateBillsWithMonthlyCheck()` : Génération avec vérification
- Gestion de la logique de validation

#### Notification Service

- Messages spécialisés pour la génération de factures
- Notifications contextuelles selon les situations

## Utilisation

### Génération de Nouvelles Factures

1. **Accès** : Aller à la page de génération de factures
2. **Configuration** :
   - Sélectionner le mois et l'année
   - Définir le montant mensuel
   - Ajouter une observation (optionnel)
3. **Sélection** : Choisir les clients pour la génération
4. **Génération** : Cliquer sur "Générer les Factures"

### Régénération de Factures

1. **Détection automatique** : Si des factures existent déjà pour le mois
2. **Notification** : Message d'avertissement affiché
3. **Confirmation** : L'utilisateur doit confirmer la régénération
4. **Remplacement** : Les anciennes factures sont remplacées

### Impression

1. **Sélection** : Choisir les factures à imprimer
2. **Prévisualisation** : Vérifier le format dans l'interface
3. **Impression** : Format automatique deux-par-page

## Structure des Fichiers

```
src/app/customers/components/bill/
├── bill.component.ts                    # Composant principal des factures
├── bill-print/
│   ├── bill-print.component.ts         # Composant d'impression
│   ├── bill-print.component.html       # Template formaté
│   └── bill-print.component.css        # Styles d'impression
├── bill-template-preview/
│   └── bill-template-preview.component.ts # Prévisualisation du format
├── bill-table/                         # Tableau des factures
└── enhanced-bill-generation.component.ts # Génération avancée

src/app/customers/service/
├── bill.service.ts                      # Service des factures
├── bill-management.service.ts           # Gestion avancée
└── ...

src/app/_shared/services/
└── notification.service.ts              # Service de notifications

public/
└── bill.jpeg                           # Image de référence du format
```

## Configuration

### Environnement de Développement

1. **Dépendances** : Toutes les dépendances PrimeNG nécessaires sont déjà incluses
2. **Assets** : S'assurer que `bill.jpeg` est présent dans `public/`
3. **Routes** : Vérifier que les routes vers les composants sont configurées

### Variables de Configuration

```typescript
// Mois disponibles
months = [
  { label: "Janvier", value: "january" },
  // ... autres mois
];

// Années disponibles (basées sur l'année actuelle ±2)
years = generateYearsArray();

// Montant par défaut
defaultMonthlyPayment = 2000; // FCFA
```

## API Backend Attendue

### Endpoints Requis

```typescript
// Vérification des factures existantes
POST /bills/check-existing
Body: { customerIds: number[], month: string, year: string }
Response: { hasExistingBills: boolean, existingBills?: BillModel[] }

// Génération de factures
POST /bills/generate?shouldGenerate=boolean
Body: number[] // customerIds
Response: BillModel[]

// Récupération des factures
GET /bills
Response: { data: BillModel[] }
```

### Modèle de Données

```typescript
interface BillModel {
  id?: number;
  customerId?: number;
  customerName?: string;
  month?: string; // 'january', 'february', etc.
  year?: string; // '2024', '2025', etc.
  monthlyPayment?: number; // Montant mensuel
  debt?: number; // Arriérés
  penalties?: number; // Pénalités
  netToPay?: number; // Total à payer
  deadLine?: string; // Date limite (ISO string)
  depositDate?: string; // Date de dépôt (ISO string)
  observation?: string; // Notes optionnelles
  status?: "PAID" | "UNPAID" | "PARTIALLY_PAID";
  remainingBalance?: number;
  paidAmount?: number;
}
```

## Styles et Responsive

### Impression

- **Format** : A4 optimisé
- **Layout** : Deux factures par page
- **Polices** : Arial pour compatibilité
- **Bordures** : Noires pour visibilité maximale

### Écran

- **Responsive** : Adaptation mobile/tablette
- **Interface** : PrimeNG pour cohérence UI
- **Accessibilité** : Labels et navigation clavier

## Maintenance et Évolutions

### Points d'Attention

1. **Format de l'image** : Maintenir `bill.jpeg` à jour selon les besoins
2. **Validation mensuelle** : Ajuster la logique selon les règles métier
3. **Performance** : Optimiser pour de gros volumes de clients

### Évolutions Possibles

1. **Templates multiples** : Support de différents formats de factures
2. **Export PDF** : Génération directe en PDF
3. **Email automatique** : Envoi des factures par email
4. **Historique détaillé** : Suivi des générations et modifications

## Support et Débogage

### Logs Utiles

- Console browser pour erreurs frontend
- Vérification des appels API dans Network tab
- État des services via Angular DevTools

### Problèmes Fréquents

1. **Image manquante** : Vérifier présence de `bill.jpeg` dans `public/`
2. **Dates incorrectes** : Contrôler le format des dates du backend
3. **Impression décalée** : Ajuster les styles CSS @media print
