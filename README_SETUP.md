Guide d'installation rapide pour VS Code et Spring Boot (Windows)

1) JDK 21
- Téléchargez et installez OpenJDK 21 (Eclipse Temurin / Adoptium recommandé): https://adoptium.net/
- Après installation, notez le chemin du JDK (ex: C:\Program Files\Eclipse Adoptium\jdk-21)

2) Définir `JAVA_HOME` (PowerShell temporaire pour la session courante) :
```powershell
$env:JAVA_HOME = 'C:\Path\To\jdk-21'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```
Pour le rendre permanent (ouvrir PowerShell en Administrateur) :
```powershell
setx JAVA_HOME "C:\Path\To\jdk-21" /M
# Puis ouvrir une nouvelle session PowerShell pour que la variable prenne effet
```

3) Maven
- Le projet contient un wrapper `mvnw`/`mvnw.cmd`. Vous pouvez l'utiliser sans installer Maven globalement :
```powershell
# depuis le dossier du projet
.\mvnw.cmd clean package
```

4) Extensions VS Code recommandées
- Installer depuis la vue Extensions (Ctrl+Shift+X) :
  - Language Support for Java™ by Red Hat (`redhat.java`)
  - Java Extension Pack (`vscjava.vscode-java-pack`)
  - Maven for Java (`vscjava.vscode-maven`)
  - Debugger for Java (`vscjava.vscode-java-debug`)
  - Java Test Runner (`vscjava.vscode-java-test`)
  - Lombok Annotations Support (`GabrielBB.vscode-lombok`)
  - Spring Boot Dashboard (`vscjava.vscode-spring-boot-dashboard`)

5) Lombok
- Installez l'extension Lombok et redémarrez VS Code. Aucun code supplémentaire n'est nécessaire si Lombok est déjà dans le `pom.xml`.

6) Importer le projet
- Ouvrez le dossier du projet dans VS Code. Acceptez l'import Maven si demandé par la Java Language Server.
- Ouvrez la vue Spring Boot Dashboard pour lancer et debuguer l'application.

7) Lancer l'application
```powershell
# depuis le dossier du projet
.\mvnw.cmd spring-boot:run
```

Remarque: Java actuellement détecté sur cette machine : `java -version` retourne Java 1.8.0.491. Installez JDK 21 et mettez à jour `JAVA_HOME` pour aligner avec `java.version` dans le `pom.xml`.
