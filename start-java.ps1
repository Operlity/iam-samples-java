# Start the IdentityHub Java Application
Write-Host '========================================' -ForegroundColor Cyan
Write-Host '  IdentityHub Java - SSL Startup' -ForegroundColor Cyan
Write-Host '========================================' -ForegroundColor Cyan

# 1. Setup Environment
$JAVA_HOME = Get-ChildItem 'C:\Program Files\Microsoft\jdk-17*' | Select-Object -First 1 -ExpandProperty FullName
$MAVEN_HOME = 'd:\Java SpringBoot Web App\maven'
$env:JAVA_HOME = $JAVA_HOME
$env:Path = "$JAVA_HOME\bin;$MAVEN_HOME\bin;$env:Path"

Write-Host "Using Java: $JAVA_HOME" -ForegroundColor Green

# 2. Generate Certificate
$KS_PATH = "d:\Java SpringBoot Web App\keystore.p12"
if (-not (Test-Path $KS_PATH)) {
    Write-Host 'Generating local SSL certificate...' -ForegroundColor Yellow
    & keytool -genkeypair -alias identityhub -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore $KS_PATH -validity 3650 -storepass password -dname 'CN=localhost' -keypass password -noprompt
}

# 3. Launch Browser
Start-Job -ScriptBlock { 
    Start-Sleep -Seconds 25 # Java clean build takes longer
    Start-Process 'https://localhost:4500' 
} | Out-Null

# 4. Clean Build and Run
Write-Host '1. CLEANING and Starting Spring Boot...' -ForegroundColor Green
mvn clean spring-boot:run
