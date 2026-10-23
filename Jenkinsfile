// CI-pipeline voor de stamboom-app. Elke stage draait in een eigen wegwerp-container (Docker Pipeline-plugin),
// zodat Jenkins zelf geen Maven of Node nodig heeft. Jenkins haalt de code vanzelf op aan het begin van elke stage.
pipeline {
    agent none

    options {
        timeout(time: 15, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        stage('Backend: test') {
            agent { docker { image 'maven:3.9-eclipse-temurin-25' } }
            steps {
                // De build-container draait als gewone gebruiker zonder home-map, dus de Maven-cache komt in de workspace.
                sh 'mvn -B -Dmaven.repo.local=.m2/repository test'
            }
            post {
                always { junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml' }
            }
        }

        stage('Frontend: build') {
            agent { docker { image 'node:24' } }
            steps {
                dir('frontend') {
                    // Zelfde reden: npm schrijft zijn cache in de workspace, niet in een home-map die niet bestaat.
                    sh 'npm ci --cache .npm-cache'
                    sh 'npm run build'
                }
            }
            post {
                success { archiveArtifacts artifacts: 'frontend/dist/**', fingerprint: true }
            }
        }

        // Start de hele app (database, API, frontend) in de aparte dind-engine. De poorten staan via de
        // jenkins-docker-container alleen op localhost van je pc open.
        stage('Deploy (lokaal)') {
            agent any
            steps {
                // Het wachtwoord komt uit Jenkins Credentials (Secret text, id familytree-db-password), nooit uit de code.
                withCredentials([string(credentialsId: 'familytree-db-password', variable: 'DB_PASSWORD')]) {
                    sh 'docker compose up -d --build --remove-orphans'
                }
                echo 'Frontend: http://localhost:8081   API: http://localhost:8080/api/health'
            }
        }
    }
}
