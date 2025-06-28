# 🎮 Pixel Thief

Un jeu de puzzle furtif inspiré de Metal Gear et Minesweeper, développé pour Android avec un style pixel art et une ambiance jazz/noir.

![Pixel Thief](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

## 📖 Description

Pixel Thief est un jeu de puzzle tactique où vous incarnez un voleur professionnel infiltrant des installations sécurisées. Utilisez votre intelligence et vos gadgets pour éviter les gardes, désactiver les caméras de sécurité et récupérer des objets de valeur, le tout dans un univers pixel art avec une bande sonore jazz/noir immersive.

## ✨ Fonctionnalités

### 🎯 Gameplay
- **Mécanique de jeu par tours** : Planifiez vos mouvements stratégiquement
- **Système de vision** : Les gardes et caméras ont des cônes de vision
- **Brouillard de guerre** : Explorez progressivement la carte
- **Gadgets tactiques** :
  - 💨 Bombe fumigène : Masque temporairement votre position
  - ⚡ Puce EMP : Désactive les caméras de sécurité
  - 👣 Fausses empreintes : Distrait les gardes

### 🎨 Interface
- **Design moderne** avec thème néon
- **Boutons tactiles** optimisés pour mobile
- **Animations fluides** et feedback visuel
- **Interface intuitive** avec contrôles directionnels

### 🔊 Audio
- **Musique de fond** jazz/noir
- **Effets sonores** immersifs
- **Gestion audio** optimisée pour Android

## 🛠️ Installation

### Prérequis
- Android Studio Arctic Fox ou plus récent
- Android SDK API 21+ (Android 5.0)
- Java 8 ou plus récent

### Étapes d'installation

1. **Cloner le repository**
   ```bash
   git clone https://github.com/votre-username/pixel-thief.git
   cd pixel-thief
   ```

2. **Ouvrir dans Android Studio**
   - Lancez Android Studio
   - Sélectionnez "Open an existing project"
   - Naviguez vers le dossier `pixelthief/`

3. **Synchroniser le projet**
   - Android Studio va automatiquement synchroniser les dépendances Gradle
   - Attendez que la synchronisation soit terminée

4. **Ajouter les fichiers audio** (optionnel)
   - Placez vos fichiers audio dans `app/src/main/res/raw/`
   - Formats supportés : `.mp3`, `.ogg`, `.wav`

5. **Compiler et exécuter**
   - Connectez votre appareil Android ou lancez un émulateur
   - Cliquez sur "Run" (▶️) dans Android Studio

## 🎮 Comment jouer

### Contrôles
- **Boutons directionnels** : Déplacer le joueur
- **Bouton "Use"** : Utiliser l'objet sélectionné
- **Sélection d'objets** : Choisir entre bombe fumigène, puce EMP, ou fausses empreintes

### Objectifs
1. **Récupérer les gemmes** dispersées sur la carte
2. **Éviter les gardes** et leurs patrouilles
3. **Désactiver les caméras** de sécurité
4. **Atteindre la sortie** avec tous les objets

### Mécaniques
- **Tours** : Chaque action consomme un tour
- **Vision des gardes** : Restez hors de leur champ de vision
- **Tiles bruyantes** : Certaines cases alertent les gardes
- **Plates-formes de pression** : Déclenchent des alarmes

## 🏗️ Architecture du projet

```
pixelthief/
├── app/
│   ├── src/main/
│   │   ├── java/com/todolist/pixelthief/
│   │   │   ├── GameGrid.java          # Logique du jeu
│   │   │   ├── GameGridView.java      # Rendu graphique
│   │   │   ├── MainActivity.java      # Activité principale
│   │   │   ├── MenuActivity.java      # Menu principal
│   │   │   ├── GameOverActivity.java  # Écran de fin
│   │   │   ├── VictoryActivity.java   # Écran de victoire
│   │   │   └── SoundManager.java      # Gestion audio
│   │   ├── res/
│   │   │   ├── drawable/              # Ressources graphiques
│   │   │   ├── layout/                # Layouts XML
│   │   │   ├── values/                # Couleurs, thèmes, strings
│   │   │   └── raw/                   # Fichiers audio
│   │   └── AndroidManifest.xml
│   └── build.gradle
└── build.gradle
```

## 🎨 Ressources graphiques

Le jeu utilise des drawables XML personnalisés pour :
- **Tiles** : Sol, murs, cachettes
- **Entités** : Joueur, gardes, caméras, gemmes, sortie
- **UI** : Boutons, arrière-plans, icônes

## 🔧 Configuration

### Fichier `build.gradle`
```gradle
android {
    compileSdk 34
    defaultConfig {
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }
}
```

### Permissions requises
- `INTERNET` : Pour les futures fonctionnalités en ligne
- `VIBRATE` : Pour le feedback haptique

## 🚀 Fonctionnalités futures

- [ ] **Niveaux multiples** avec difficulté croissante
- [ ] **Mode multijoueur** local
- [ ] **Système de score** et classements
- [ ] **Nouvelles mécaniques** de jeu
- [ ] **Personnalisation** des personnages
- [ ] **Mode histoire** avec narration

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. Fork le projet
2. Créez une branche pour votre fonctionnalité (`git checkout -b feature/AmazingFeature`)
3. Committez vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

## 📝 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 👨‍💻 Développeur

**Pixel Thief** - Un projet de jeu mobile développé avec passion pour Android.

## 🙏 Remerciements

- Inspiré par **Metal Gear** et **Minesweeper**
- Style pixel art inspiré des jeux rétro
- Ambiance musicale jazz/noir pour l'immersion

---

⭐ Si ce projet vous plaît, n'oubliez pas de le star sur GitHub !
