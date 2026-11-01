// Jenkins declarative pipeline: build -> test -> docker images -> push -> deploy to AWS EC2.
// Required Jenkins credentials:
//   dockerhub-creds  (Username/Password)  - Docker Hub login
//   ec2-ssh-key      (SSH Username with private key) - key for the EC2 instance
// Required environment: set EC2_HOST (e.g. ec2-user@<public-ip>) and DOCKER_USER below / in Jenkins.
pipeline {
  agent any
  environment {
    DOCKER_USER = 'your-dockerhub-username'
    EC2_HOST    = 'ec2-user@your-ec2-public-ip'
    TAG         = "${env.BUILD_NUMBER}"
  }
  stages {
    stage('Checkout') {
      steps { checkout scm }
    }
    stage('Backend: test & package') {
      steps {
        dir('backend') { sh 'mvn -B clean verify' }
      }
      post { always { junit 'backend/target/surefire-reports/*.xml' } }
    }
    stage('Frontend: build') {
      steps {
        dir('frontend') {
          sh 'npm ci'
          sh 'npx ng build --configuration production'
        }
      }
    }
    stage('Docker build') {
      steps {
        sh 'docker build -t $DOCKER_USER/foodie-backend:$TAG -t $DOCKER_USER/foodie-backend:latest backend'
        sh 'docker build -t $DOCKER_USER/foodie-frontend:$TAG -t $DOCKER_USER/foodie-frontend:latest frontend'
      }
    }
    stage('Docker push') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'U', passwordVariable: 'P')]) {
          sh 'echo $P | docker login -u $U --password-stdin'
          sh 'docker push $DOCKER_USER/foodie-backend:$TAG && docker push $DOCKER_USER/foodie-backend:latest'
          sh 'docker push $DOCKER_USER/foodie-frontend:$TAG && docker push $DOCKER_USER/foodie-frontend:latest'
        }
      }
    }
    stage('Deploy to EC2') {
      steps {
        sshagent(['ec2-ssh-key']) {
          sh 'scp -o StrictHostKeyChecking=no docker-compose.prod.yml $EC2_HOST:~/docker-compose.yml'
          sh '''ssh -o StrictHostKeyChecking=no $EC2_HOST "export DOCKER_USER=$DOCKER_USER && docker compose pull && docker compose up -d"'''
        }
      }
    }
  }
}
