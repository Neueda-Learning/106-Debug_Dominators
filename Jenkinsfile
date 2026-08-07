pipeline {

    agent any

    environment {
        GIT_URL = 'https://github.com/Neueda-Learning/106-Debug_Dominators.git'
        BRANCH = 'main'
    }

    stages {

        stage('Checkout Source') {
            steps {
                git branch: "${BRANCH}",
                    url: "${GIT_URL}"
            }
        }

        stage('Build Spring Boot') {
            steps {
                dir('Backend/payment-processing-system') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Stop Existing Containers') {
            steps {
                sh 'docker-compose down || true'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker-compose build --no-cache'
            }
        }

        stage('Deploy') {
            steps {
                // Remove old containers by name
                sh 'docker rm -f mysql_container || true'
                sh 'docker rm -f springboot_container || true'
                sh 'docker rm -f frontend_container || true'

                // Remove ANY container using ports 8085 or 8082
                sh 'docker rm -f $(docker ps -aq --filter "publish=8085") || true'
                sh 'docker rm -f $(docker ps -aq --filter "publish=8082") || true'

                // Deploy fresh containers
                sh 'docker-compose up -d'
            }
        }

        stage('Verify') {
            steps {
                sh 'docker ps'
            }
        }
    }
}
