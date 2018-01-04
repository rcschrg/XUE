pipeline {
  agent {
    docker {
      image 'openjdk:8'
    }
    
  }
  stages {
    stage('Build') {
      steps {
        sh '''chmod -R 777 .
bash ./gradlew assemble'''
      }
    }
    stage('Test') {
      steps {
        sh 'bash ./gradlew test'
      }
    }
  }
}