@echo off
setlocal EnableExtensions
REM Maven bootstrap portavel para Windows/Jenkins.
REM Usa a mesma versao definida em .mvn/wrapper/maven-wrapper.properties.
REM Nao depende de mvn no PATH nem de MAVEN_HOME.

set "MAVEN_VERSION=3.9.11"
set "MAVEN_DIST=apache-maven-%MAVEN_VERSION%"
set "MAVEN_HOME_LOCAL=%USERPROFILE%\.m2\wrapper\dists\%MAVEN_DIST%"
set "MAVEN_CMD=%MAVEN_HOME_LOCAL%\bin\mvn.cmd"
set "MAVEN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/%MAVEN_DIST%-bin.zip"
set "MAVEN_ZIP=%TEMP%\%MAVEN_DIST%-bin.zip"
set "MAVEN_TMP=%TEMP%\maven-wrapper-%MAVEN_VERSION%"

where java >nul 2>nul
if errorlevel 1 (
  echo [ERRO] Java nao encontrado no PATH do usuario que executa o Jenkins.
  echo Configure o Jenkins para executar com JDK 17 ou superior.
  exit /b 1
)

if not exist "%MAVEN_CMD%" (
  echo [INFO] Maven %MAVEN_VERSION% nao encontrado no cache do Wrapper.
  echo [INFO] Baixando Maven %MAVEN_VERSION% de Maven Central...

  if exist "%MAVEN_ZIP%" del /q "%MAVEN_ZIP%" >nul 2>nul
  if exist "%MAVEN_TMP%" rmdir /s /q "%MAVEN_TMP%" >nul 2>nul
  if exist "%MAVEN_HOME_LOCAL%" rmdir /s /q "%MAVEN_HOME_LOCAL%" >nul 2>nul
  mkdir "%MAVEN_TMP%" >nul 2>nul

  powershell.exe -NoProfile -NonInteractive -ExecutionPolicy Bypass -Command ^
    "$ErrorActionPreference='Stop'; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; $wc=New-Object System.Net.WebClient; $wc.DownloadFile('%MAVEN_URL%','%MAVEN_ZIP%'); Expand-Archive -LiteralPath '%MAVEN_ZIP%' -DestinationPath '%MAVEN_TMP%' -Force"

  if errorlevel 1 (
    echo [ERRO] Falha ao baixar ou extrair Maven %MAVEN_VERSION%.
    echo [ERRO] Verifique acesso HTTPS a repo.maven.apache.org.
    exit /b 1
  )

  if not exist "%MAVEN_TMP%\%MAVEN_DIST%\bin\mvn.cmd" (
    echo [ERRO] Distribuicao Maven extraida, mas bin\mvn.cmd nao foi encontrado.
    exit /b 1
  )

  mkdir "%USERPROFILE%\.m2\wrapper\dists" >nul 2>nul
  move "%MAVEN_TMP%\%MAVEN_DIST%" "%MAVEN_HOME_LOCAL%" >nul
  if errorlevel 1 (
    echo [ERRO] Nao foi possivel instalar Maven no cache local do Wrapper.
    exit /b 1
  )

  del /q "%MAVEN_ZIP%" >nul 2>nul
  rmdir /s /q "%MAVEN_TMP%" >nul 2>nul
  echo [OK] Maven %MAVEN_VERSION% preparado pelo Wrapper.
) else (
  echo [OK] Maven %MAVEN_VERSION% encontrado no cache do Wrapper.
)

call "%MAVEN_CMD%" %*
exit /b %ERRORLEVEL%
