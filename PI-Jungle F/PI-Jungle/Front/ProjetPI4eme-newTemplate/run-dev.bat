@echo off
cd /d "%~dp0"
echo [Jungle] Dossier: %CD%
echo [Jungle] Installation des dependances si besoin...
call npm install
if errorlevel 1 (
  echo ERREUR: npm install a echoue. Verifie que Node.js est installe.
  pause
  exit /b 1
)
echo [Jungle] Suppression du cache .angular (corrige erreur Vite "zone.js" si ancienne config)...
if exist .angular rmdir /s /q .angular
echo [Jungle] Demarrage du serveur sur http://localhost:4200 ...
echo [Jungle] Ferme cette fenetre pour arreter le serveur.
call npm run start
pause
