// Jenkinsfile (pre InsuranceApp)
pipeline {
    agent any // Pipeline pobeží na Jenkins serveri (VivoBook)
    
    // Premenné prostredia a Credentials
    environment {
        // Zmeňte "gloziksoft" na váš reálny Docker Hub/Registry username
        DOCKER_IMAGE_NAME = "gloziksoft/insuranceapp-jenkins" 
        
        // ID prihlasovacích údajov nastavených v Jenkins UI (pozri Krok 2.B)
        DOCKER_CREDENTIAL_ID = "docker-hub-credentials" 
        
        // Použije short Git commit hash ako verziu aplikácie
        APP_VERSION = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim() 
    }
    
    stages {
        stage('Build Image') {
            steps {
                echo "Building Docker image: ${DOCKER_IMAGE_NAME}:${APP_VERSION}"
                // Zostavenie obrazu
                sh "docker build -t ${DOCKER_IMAGE_NAME}:${APP_VERSION} ."
                sh "docker tag ${DOCKER_IMAGE_NAME}:${APP_VERSION} ${DOCKER_IMAGE_NAME}:latest"
            }
        }
        
        stage('Push Image') {
            // Použije prihlasovacie údaje z Jenkins Credentials
            withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIAL_ID}", 
                                              passwordVariable: 'DOCKER_PASSWORD', 
                                              usernameVariable: 'DOCKER_USERNAME')]) {
                steps {
                    echo "Pushing images to Docker Registry..."
                    // Prihlásenie
                    sh "echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin"
                    // Odoslanie obrazov
                    sh "docker push ${DOCKER_IMAGE_NAME}:${APP_VERSION}"
                    sh "docker push ${DOCKER_IMAGE_NAME}:latest"
                }
            }
        }
        
        stage('Deploy to K3s') {
            steps {
                echo 'Applying deployment to K3s cluster...'
                // Aplikácia K8s YAML súborov (Rolling Update)
                sh 'kubectl apply -f k8s/deployment.yaml' 
            }
        }
    }
}
