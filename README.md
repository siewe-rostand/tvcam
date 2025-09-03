# TVCam Mobile - Application Flutter

Une application mobile moderne et professionnelle pour la gestion de projets vidéo, construite avec Flutter et Riverpod.

## 🚀 Fonctionnalités

- **Design moderne et ergonomique** : Interface utilisateur intuitive avec des animations fluides
- **Support offline** : Fonctionnement complet sans connexion internet
- **Architecture propre** : Code bien organisé et optimisé suivant les principes de Clean Architecture
- **Gestion d'état avancée** : Utilisation de Riverpod pour une gestion d'état robuste
- **Thème adaptatif** : Support des thèmes clair et sombre
- **Navigation moderne** : Utilisation de GoRouter pour une navigation fluide

## 🏗️ Architecture

L'application suit une architecture en couches bien définie :

```
lib/
├── core/                    # Couche de base
│   ├── constants/          # Constantes et thèmes
│   ├── di/                 # Injection de dépendances
│   ├── errors/             # Gestion des erreurs
│   ├── network/            # Services réseau
│   ├── router/             # Configuration des routes
│   └── services/           # Services de base
├── features/               # Fonctionnalités métier
│   ├── auth/              # Authentification
│   ├── home/              # Page d'accueil
│   ├── profile/           # Profil utilisateur
│   └── settings/          # Paramètres
└── shared/                 # Composants partagés
    ├── models/             # Modèles de données
    ├── services/           # Services partagés
    └── widgets/            # Widgets réutilisables
```

### Principes de Clean Architecture

1. **Séparation des responsabilités** : Chaque couche a une responsabilité spécifique
2. **Dépendances unidirectionnelles** : Les dépendances vont toujours de l'extérieur vers l'intérieur
3. **Inversion de dépendances** : Les couches internes ne dépendent pas des couches externes
4. **Testabilité** : Architecture conçue pour faciliter les tests unitaires et d'intégration

## 🛠️ Technologies utilisées

- **Flutter** : Framework de développement mobile
- **Riverpod** : Gestion d'état moderne et type-safe
- **GoRouter** : Navigation déclarative et performante
- **Hive** : Base de données locale pour le support offline
- **Dio** : Client HTTP avec intercepteurs pour la gestion offline
- **GetIt + Injectable** : Injection de dépendances
- **Google Fonts** : Typographie moderne avec Poppins

## 📱 Support Offline

L'application est conçue pour fonctionner entièrement en mode hors ligne :

- **Stockage local** : Utilisation de Hive pour le cache et les données persistantes
- **File d'attente des requêtes** : Les actions sont mises en file d'attente et synchronisées automatiquement
- **Détection de connectivité** : Surveillance en temps réel de l'état de la connexion
- **Bannière informative** : Notification visuelle de l'état offline

## 🎨 Design System

### Couleurs
- **Primaire** : Bleu moderne (#2563EB)
- **Secondaire** : Vert (#10B981)
- **Accent** : Orange (#F59E0B)
- **Support** : Succès, avertissement, erreur, info

### Typographie
- **Famille** : Poppins (Google Fonts)
- **Hiérarchie** : Display, Headline, Title, Body, Label
- **Poids** : Regular (400), Medium (500), SemiBold (600), Bold (700)

### Composants
- **Cartes** : Bordures arrondies, ombres subtiles
- **Boutons** : États interactifs, animations de feedback
- **Formulaires** : Validation en temps réel, design cohérent
- **Navigation** : Barre de navigation moderne avec Material 3

## 🚀 Installation et configuration

### Prérequis
- Flutter SDK 3.10.0 ou supérieur
- Dart SDK 3.0.0 ou supérieur
- Android Studio / VS Code avec extensions Flutter

### Installation

1. **Cloner le projet**
   ```bash
   git clone <repository-url>
   cd mobile_app
   ```

2. **Installer les dépendances**
   ```bash
   flutter pub get
   ```

3. **Générer le code**
   ```bash
   flutter packages pub run build_runner build
   ```

4. **Lancer l'application**
   ```bash
   flutter run
   ```

### Configuration

1. **Variables d'environnement** : Créer un fichier `.env` avec les configurations nécessaires
2. **Clés API** : Configurer les clés pour les services externes
3. **Certificats** : Configurer les certificats de développement/production

## 📁 Structure des fichiers

```
mobile_app/
├── lib/
│   ├── main.dart                 # Point d'entrée de l'application
│   ├── core/                     # Couche de base
│   │   ├── constants/
│   │   │   ├── app_colors.dart   # Palette de couleurs
│   │   │   └── app_theme.dart    # Configuration des thèmes
│   │   ├── di/
│   │   │   └── injection.dart    # Injection de dépendances
│   │   ├── errors/
│   │   │   └── app_exception.dart # Gestion des erreurs
│   │   ├── router/
│   │   │   └── app_router.dart   # Configuration des routes
│   │   └── services/
│   │       ├── connectivity_service.dart # Service de connectivité
│   │       └── storage_service.dart      # Service de stockage
│   ├── features/                 # Fonctionnalités métier
│   │   ├── auth/                 # Authentification
│   │   │   └── presentation/
│   │   │       ├── pages/
│   │   │       │   ├── login_page.dart
│   │   │       │   └── register_page.dart
│   │   │       └── widgets/
│   │   │           ├── auth_text_field.dart
│   │   │           └── social_login_button.dart
│   │   ├── home/                 # Page d'accueil
│   │   │   └── presentation/
│   │   │       ├── pages/
│   │   │       │   └── home_page.dart
│   │   │       └── widgets/
│   │   │           ├── feature_card.dart
│   │   │           ├── quick_action_button.dart
│   │   │           └── recent_activity_card.dart
│   │   ├── profile/              # Profil utilisateur
│   │   │   └── presentation/
│   │   │       └── pages/
│   │   │           └── profile_page.dart
│   │   └── settings/             # Paramètres
│   │       └── presentation/
│   │           └── pages/
│   │               └── settings_page.dart
│   └── shared/                   # Composants partagés
│       └── widgets/
│           └── offline_banner.dart # Bannière de mode offline
├── assets/                       # Ressources
│   ├── images/                   # Images
│   ├── icons/                    # Icônes
│   └── fonts/                    # Polices
├── pubspec.yaml                  # Dépendances Flutter
└── README.md                     # Documentation
```

## 🔧 Développement

### Commandes utiles

```bash
# Générer le code
flutter packages pub run build_runner build

# Générer le code en continu
flutter packages pub run build_runner watch

# Nettoyer le cache
flutter clean

# Analyser le code
flutter analyze

# Tests unitaires
flutter test

# Tests d'intégration
flutter test integration_test/
```

### Bonnes pratiques

1. **Nommage** : Utiliser des noms descriptifs et cohérents
2. **Documentation** : Commenter le code complexe
3. **Tests** : Écrire des tests pour les fonctionnalités critiques
4. **Performance** : Optimiser les widgets et les animations
5. **Accessibilité** : Respecter les guidelines d'accessibilité

## 🧪 Tests

L'application inclut une suite de tests complète :

- **Tests unitaires** : Logique métier et services
- **Tests de widgets** : Interface utilisateur
- **Tests d'intégration** : Flux complets de l'application

### Exécution des tests

```bash
# Tous les tests
flutter test

# Tests spécifiques
flutter test test/unit/
flutter test test/widget/
flutter test test/integration/
```

## 📦 Déploiement

### Build de production

```bash
# Android
flutter build apk --release

# iOS
flutter build ios --release
```

### Configuration de production

1. **Optimisations** : Activer les optimisations de compilation
2. **Clés de signature** : Configurer les clés de signature Android/iOS
3. **Variables d'environnement** : Configurer les variables de production
4. **Monitoring** : Intégrer les outils de monitoring et de crash reporting

## 🤝 Contribution

1. **Fork** le projet
2. **Créer** une branche pour votre fonctionnalité
3. **Commit** vos changements
4. **Push** vers la branche
5. **Créer** une Pull Request

### Standards de code

- Suivre les conventions Flutter/Dart
- Utiliser des noms descriptifs
- Documenter les fonctions publiques
- Écrire des tests pour les nouvelles fonctionnalités

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 🆘 Support

Pour toute question ou problème :

- **Issues** : Créer une issue sur GitHub
- **Documentation** : Consulter la documentation Flutter
- **Communauté** : Participer aux forums Flutter

## 🔮 Roadmap

### Version 1.1
- [ ] Support des notifications push
- [ ] Synchronisation cloud avancée
- [ ] Mode collaboratif

### Version 1.2
- [ ] Éditeur vidéo intégré
- [ ] Support des formats 4K
- [ ] Intégration avec les réseaux sociaux

### Version 2.0
- [ ] Support multiplateforme (web, desktop)
- [ ] Intelligence artificielle pour l'édition
- [ ] Écosystème de plugins

---

**TVCam Mobile** - Créer, éditer et partager vos projets vidéo avec style et simplicité.
