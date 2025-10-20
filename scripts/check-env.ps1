# Check Java and Maven environment for this project
Write-Host "Checking Java and Maven environment..."

try {
    $javaVersion = java -version 2>&1
    Write-Host "Java version:"
    Write-Host $javaVersion
} catch {
    Write-Host "Java not found in PATH."
}

try {
    $mvnVersion = mvn -v 2>&1
    Write-Host "Maven version:"
    Write-Host $mvnVersion
} catch {
    Write-Host "Maven not found in PATH."
}

Write-Host "If Java or Maven are missing, install them via winget or choco (see README.md)."
