pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    environment {
        COMPOSE_PROJECT_NAME = 'fasterpay'
        COMPOSE_CMD_FILE = '.compose_cmd'
        WORK_DIR_FILE = '.workdir'
        ENV_FILE = '.env'
        REPO_URL = 'https://github.com/Neueda-Learning/106-Debug_Dominators.git'
        REPO_BRANCH = 'production'
        REPO_CREDENTIALS_ID = ''

        // Collision-safe defaults for shared agents.
        MYSQL_PORT = '13306'
        BACKEND_PORT = '18082'
        FRONTEND_PORT = '18081'
    }

    stages {
        stage('Checkout Source') {
            steps {
                script {
                    def workDir = "repo-${env.BUILD_NUMBER}-${UUID.randomUUID().toString().substring(0, 8)}"

                    def repoUrl = env.REPO_URL?.trim()
                    def branch = env.REPO_BRANCH?.trim()
                    def credentialsId = env.REPO_CREDENTIALS_ID?.trim()

                    if (!repoUrl) {
                        error('REPO_URL is required.')
                    }

                    if (!branch) {
                        error('REPO_BRANCH is required.')
                    }

                    sh "rm -rf '${workDir}' 2>/dev/null || true"

                    if (credentialsId) {
                        dir(workDir) {
                            git branch: branch, credentialsId: credentialsId, url: repoUrl
                        }
                    } else {
                        sh "git clone --branch '${branch}' --single-branch '${repoUrl}' '${workDir}'"
                    }

                    writeFile file: env.WORK_DIR_FILE, text: workDir
                    echo "Checked out into workspace subfolder: ${workDir}"
                }
            }
        }

        stage('Validate Agent Tooling') {
            steps {
                script {
                    if (!isUnix()) {
                        error('This pipeline requires a Linux Jenkins agent with git, curl, docker, and either docker compose or docker-compose installed.')
                    }

                    sh 'git --version'
                    sh 'docker --version'
                    sh 'curl --version'

                    def composeCmd = sh(
                        script: '''
if docker compose version >/dev/null 2>&1; then
    echo "docker compose"
elif docker-compose version >/dev/null 2>&1; then
    echo "docker-compose"
fi
''',
                        returnStdout: true
                    ).trim()

                    if (!composeCmd) {
                        error('Neither docker compose nor docker-compose is available on this Jenkins agent.')
                    }

                    writeFile file: env.COMPOSE_CMD_FILE, text: composeCmd + "\n"
                    sh "${composeCmd} version"
                }
            }
        }

        stage('Test Backend') {
            steps {
                script {
                    def workDir = readFile(env.WORK_DIR_FILE).trim()
                    dir("${workDir}/Backend/payment-processing-system") {
                        sh 'chmod +x mvnw && DB_USER=${MYSQL_USER:-fasterpay} DB_PASSWORD=${MYSQL_PASSWORD:-n3u3da!} ./mvnw -B clean test'
                    }
                }
            }
            post {
                always {
                    script {
                        def workDir = readFile(env.WORK_DIR_FILE).trim()
                        junit testResults: "${workDir}/Backend/payment-processing-system/target/surefire-reports/*.xml", allowEmptyResults: true
                    }
                }
            }
        }

        stage('Build Backend') {
            steps {
                script {
                    def workDir = readFile(env.WORK_DIR_FILE).trim()
                    dir("${workDir}/Backend/payment-processing-system") {
                        sh 'chmod +x mvnw && ./mvnw -B package -DskipTests'
                    }
                }
            }
        }

        stage('Validate Frontend') {
            steps {
                script {
                    def workDir = readFile(env.WORK_DIR_FILE).trim()
                    dir("${workDir}/Frontend") {
                        sh '''
if command -v npm >/dev/null 2>&1; then
    npm ci
    npm run build
else
    echo "npm is not installed on this Jenkins agent. Skipping local frontend validation; frontend will be built by Docker during deployment."
fi
'''
                    }
                }
            }
        }

        stage('Prepare Deployment Env') {
            steps {
                script {
                    def workDir = readFile(env.WORK_DIR_FILE).trim()
                    def envContent = """
MYSQL_ROOT_PASSWORD=${env.MYSQL_ROOT_PASSWORD ?: 'n3u3da!'}
MYSQL_DATABASE=${env.MYSQL_DATABASE ?: 'payment_db'}
MYSQL_USER=${env.MYSQL_USER ?: 'fasterpay'}
MYSQL_PASSWORD=${env.MYSQL_PASSWORD ?: 'n3u3da!'}
MYSQL_PORT=${env.MYSQL_PORT ?: '13306'}
BACKEND_PORT=${env.BACKEND_PORT ?: '18082'}
FRONTEND_PORT=${env.FRONTEND_PORT ?: '18081'}
VITE_API_BASE_URL=${env.VITE_API_BASE_URL ?: "http://localhost:${env.BACKEND_PORT ?: '18082'}"}
APP_CORS_ALLOWED_ORIGINS=${env.APP_CORS_ALLOWED_ORIGINS ?: "http://localhost:${env.FRONTEND_PORT ?: '18081'}"}
""".trim() + "\n"

                    writeFile file: "${workDir}/${env.ENV_FILE}", text: envContent
                }
            }
        }

        stage('Deploy Stack') {
            steps {
                script {
                    def workDir = readFile(env.WORK_DIR_FILE).trim()
                    def composeCmd = readFile(env.COMPOSE_CMD_FILE).trim()
                    dir(workDir) {
                        sh "${composeCmd} --env-file .env down --volumes --remove-orphans || true"
                        sh "${composeCmd} --env-file .env pull || true"
                        sh """
if ! ${composeCmd} --env-file .env up -d --build mysql; then
    ${composeCmd} --env-file .env ps || true
    ${composeCmd} --env-file .env logs --tail=200 mysql || true
    exit 1
fi

MYSQL_CONTAINER="\${COMPOSE_PROJECT_NAME}-mysql-1"
for i in \$(seq 1 40); do
    MYSQL_STATUS=\$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' "\${MYSQL_CONTAINER}" 2>/dev/null || true)
    if [ "\${MYSQL_STATUS}" = "healthy" ]; then
        echo "MySQL is healthy."
        break
    fi
    if [ "\${MYSQL_STATUS}" = "exited" ] || [ "\${MYSQL_STATUS}" = "dead" ]; then
        echo "MySQL container is not running (status=\${MYSQL_STATUS})."
        ${composeCmd} --env-file .env logs --tail=200 mysql || true
        exit 1
    fi
    echo "Waiting for MySQL health... (\${i}/40, status=\${MYSQL_STATUS:-starting})"
    sleep 5
    if [ "\${i}" -eq 40 ]; then
        echo "MySQL did not become healthy in time."
        ${composeCmd} --env-file .env logs --tail=200 mysql || true
        exit 1
    fi
done

if ! ${composeCmd} --env-file .env up -d --build --remove-orphans backend frontend; then
    ${composeCmd} --env-file .env ps || true
    ${composeCmd} --env-file .env logs --tail=200 backend mysql frontend || true
    exit 1
fi
"""
                        sh "${composeCmd} --env-file .env ps"
                    }
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    def workDir = readFile(env.WORK_DIR_FILE).trim()
                    def composeCmd = readFile(env.COMPOSE_CMD_FILE).trim()
                    dir(workDir) {
                        sh "${composeCmd} --env-file .env ps"
                    }

                    sh '''
for i in $(seq 1 30); do
  if curl -fsS "http://localhost:${BACKEND_PORT}/v3/api-docs" >/dev/null 2>&1; then
    echo "Backend is healthy."
    break
  fi
  echo "Waiting for backend health... ($i/30)"
  sleep 5
  if [ "$i" -eq 30 ]; then
    echo "Backend health check failed"
    exit 1
  fi
done
'''

                    sh '''
for i in $(seq 1 30); do
  if curl -fsS "http://localhost:${FRONTEND_PORT}/" >/dev/null 2>&1; then
    echo "Frontend is healthy."
    break
  fi
  echo "Waiting for frontend health... ($i/30)"
  sleep 5
  if [ "$i" -eq 30 ]; then
    echo "Frontend health check failed"
    exit 1
  fi
done
'''
                }
            }
        }
    }

    post {
        success {
            echo 'Deployment pipeline completed successfully.'
        }
        failure {
            echo 'Deployment pipeline failed. Check stage logs above.'
        }
        cleanup {
            script {
                if (fileExists(env.WORK_DIR_FILE)) {
                    def workDir = readFile(env.WORK_DIR_FILE).trim()
                    sh "rm -f '${workDir}/${env.ENV_FILE}'"
                }
                sh 'rm -f .compose_cmd .workdir'
            }
        }
    }
}
