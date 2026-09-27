pipeline {
    agent any

    parameters {
        booleanParam(name: 'PUSH_DOCKER_HUB', defaultValue: false,
            description: 'Opcional: publicar a imagem no Docker Hub apos o build local.')
    }

    environment {
        DOCKER_IMAGE = 'andprof/jogo-enigma-api'
        DOCKER_CREDENTIALS_ID = 'dockerhub-credentials'
        IMAGE_TAG = "${BUILD_NUMBER}"
        COMPOSE_FILE = 'docker-compose.homol.yml'
        // A API usa 8081 no host para nao disputar a porta 8080, comum no Jenkins.
        // As portas internas dos containers permanecem inalteradas.
        API_PORT = '8081'
        BFF_PORT = '3000'
        PGADMIN_PORT = '5050'
        PROMETHEUS_PORT = '9091'
        GRAFANA_PORT = '3001'
    }

    stages {
        stage('0 - Preflight do Ambiente') {
            steps {
                bat '''
                    @echo off
                    echo === Java usado pelo Jenkins ===
                    java -version
                    if errorlevel 1 exit /b 1
                    echo.
                    echo === Docker ===
                    docker version
                    if errorlevel 1 exit /b 1
                    echo.
                    echo === Docker Compose ===
                    docker compose version
                    if errorlevel 1 exit /b 1
                    echo.
                    echo [OK] Nao e necessario instalar Maven, Node, npm ou Cypress globalmente.
                '''
            }
        }

        stage('1 - Checkout') {
            steps { checkout scm }
        }

        stage('2 - CI - Testes JUnit + JaCoCo') {
            steps { bat 'call mvnw.cmd -B clean test' }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                    archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }

        stage('3 - CI - Qualidade PMD') {
            steps { bat 'call mvnw.cmd -B pmd:pmd -DskipTests' }
            post {
                always { archiveArtifacts artifacts: 'target/site/pmd.html', allowEmptyArchive: true }
            }
        }

        stage('4 - CI - Package') {
            steps { bat 'call mvnw.cmd -B package -DskipTests' }
        }

        stage('5 - CD - Build dos Containers') {
            steps {
                bat '''
                    docker compose -f %COMPOSE_FILE% build api bff
                '''
            }
        }

        stage('6 - Registry - Docker Hub (opcional)') {
            when { expression { return params.PUSH_DOCKER_HUB } }
            steps {
                withCredentials([usernamePassword(credentialsId: env.DOCKER_CREDENTIALS_ID,
                    usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASSWORD')]) {
                    bat '''
                        echo %DOCKER_PASSWORD% | docker login -u %DOCKER_USER% --password-stdin
                        docker push %DOCKER_IMAGE%:%IMAGE_TAG%
                        docker logout
                    '''
                }
            }
        }

        stage('7 - CD - Deploy HOMOL') {
            steps {
                bat 'docker compose -f %COMPOSE_FILE% down --remove-orphans'
                powershell '''
                    $ports = @($env:API_PORT, $env:BFF_PORT, $env:PGADMIN_PORT, $env:PROMETHEUS_PORT, $env:GRAFANA_PORT)
                    $ocupadas = @()
                    foreach ($p in $ports) {
                        $conn = Get-NetTCPConnection -LocalPort ([int]$p) -State Listen -ErrorAction SilentlyContinue
                        if ($conn) { $ocupadas += $p }
                    }
                    if ($ocupadas.Count -gt 0) {
                        throw "Porta(s) ocupada(s): $($ocupadas -join ', '). Altere apenas as variaveis de porta no bloco environment do Jenkinsfile."
                    }
                    Write-Host "[OK] Portas de HOMOL disponiveis."
                '''
                bat 'docker compose -f %COMPOSE_FILE% up -d postgres pgadmin api bff prometheus grafana'
            }
        }

        stage('8 - CD - Health Check HOMOL') {
            steps {
                powershell '''
                    $url = "http://localhost:$env:API_PORT/actuator/health"
                    $ok = $false
                    for ($i = 1; $i -le 18; $i++) {
                        try {
                            $r = Invoke-RestMethod -Uri $url -TimeoutSec 5
                            if ($r.status -eq "UP") { $ok = $true; break }
                        } catch {
                            Write-Host "API ainda nao pronta. Tentativa $i/18..."
                        }
                        Start-Sleep -Seconds 5
                    }
                    if (-not $ok) {
                        docker compose -f $env:COMPOSE_FILE ps
                        docker compose -f $env:COMPOSE_FILE logs --tail=100 api
                        throw "Health check da API falhou em $url"
                    }
                    Write-Host "[OK] API HOMOL UP: $url"
                '''
            }
        }

        stage('9 - CD - Cypress E2E em Container') {
            steps {
                bat 'docker compose -f %COMPOSE_FILE% --profile e2e run --rm cypress'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'frontend/cypress/screenshots/**,frontend/cypress/videos/**', allowEmptyArchive: true
                }
            }
        }

        stage('10 - Observabilidade') {
            steps {
                echo "API: http://localhost:${API_PORT}"
                echo "BFF/Front: http://localhost:${BFF_PORT}"
                echo "pgAdmin: http://localhost:${PGADMIN_PORT}"
                echo "Prometheus: http://localhost:${PROMETHEUS_PORT}"
                echo "Grafana: http://localhost:${GRAFANA_PORT}"
            }
        }
    }

    post {
        success { echo 'Pipeline portavel concluido: CI, containers, HOMOL, E2E e observabilidade aprovados.' }
        failure { echo 'Pipeline interrompido. O stage de falha agora informa dependencia, porta ou servico responsavel.' }
        always {
            archiveArtifacts artifacts: 'target/site/jacoco/**,target/site/pmd.html,frontend/cypress/screenshots/**,frontend/cypress/videos/**', allowEmptyArchive: true
        }
    }
}
