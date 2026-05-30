DOS
@echo off
cd /d "c:\Users\zidane\Desktop\EcommerceAPI"
echo Nettoyage du projet...
call mvn clean
echo.
echo Demarrage de l'application Spring Boot...
call mvnw spring-boot:run
pause