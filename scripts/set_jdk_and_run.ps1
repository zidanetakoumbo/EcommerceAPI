$jdk = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot'
# Persist JAVA_HOME for current user
setx JAVA_HOME $jdk
# Update current session
$env:JAVA_HOME = $jdk
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
Write-Output "JAVA_HOME set to: $env:JAVA_HOME"
Write-Output "java version:"; java -version
Write-Output "javac version:"; javac -version
# Run the app (skip tests)
Push-Location 'C:\Users\zidane\Desktop\EcommerceAPI'
.\mvnw.cmd -DskipTests spring-boot:run
Pop-Location
