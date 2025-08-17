pipeline {
    agent any

    tools {
        jdk 'OpenJDK 17'
        maven 'Maven-3.9'
    }

    environment {
        APP_NAME = 'java-backend'
        DOCKER_IMAGE = "${APP_NAME}"
        DOCKER_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                cleanWs()
                git branch: 'main',
                    credentialsId: 'github-fine-token',
                    url: 'https://github.com/your-org/backend-app.git'

                echo "Building branch: ${env.BRANCH_NAME}"
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    sh """
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                        docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                    """
                }
            }
        }

        stage('Docker Push to Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh """
                            echo \${DOCKER_PASS} | docker login -u \${DOCKER_USER} --password-stdin
                            docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} \${DOCKER_USER}/${DOCKER_IMAGE}:${DOCKER_TAG}
                            docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} \${DOCKER_USER}/${DOCKER_IMAGE}:latest
                            docker push \${DOCKER_USER}/${DOCKER_IMAGE}:${DOCKER_TAG}
                            docker push \${DOCKER_USER}/${DOCKER_IMAGE}:latest
                            docker logout
                        """
                    }
                }
            }
        }


    }

    post {
        success {
            echo "✅ Build successful!"
            echo "📦 Docker image pushed to Docker Hub:"
            echo "   - ${DOCKER_IMAGE}:${DOCKER_TAG}"
            echo "   - ${DOCKER_IMAGE}:latest"
        }
        failure {
            echo "❌ Build failed!"
        }
        always {
            cleanWs()

            sh 'docker image prune -f || true'
        }
    }
}