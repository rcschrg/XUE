properties([authorizationMatrix(['com.cloudbees.plugins.credentials.CredentialsProvider.Create:authenticated', 'com.cloudbees.plugins.credentials.CredentialsProvider.Delete:authenticated', 'com.cloudbees.plugins.credentials.CredentialsProvider.ManageDomains:authenticated', 'com.cloudbees.plugins.credentials.CredentialsProvider.Update:authenticated', 'com.cloudbees.plugins.credentials.CredentialsProvider.View:anonymous', 'com.cloudbees.plugins.credentials.CredentialsProvider.View:authenticated', 'hudson.model.Item.Build:authenticated', 'hudson.model.Item.Cancel:authenticated', 'hudson.model.Item.Configure:authenticated', 'hudson.model.Item.Delete:authenticated', 'hudson.model.Item.Discover:authenticated', 'hudson.model.Item.Move:authenticated', 'hudson.model.Item.Read:anonymous', 'hudson.model.Item.Read:authenticated', 'hudson.model.Item.Workspace:authenticated', 'hudson.model.Run.Delete:authenticated', 'hudson.model.Run.Replay:authenticated', 'hudson.model.Run.Update:authenticated', 'hudson.scm.SCM.Tag:authenticated']), pipelineTriggers([])])

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
    stage('Sonar') {
      steps {
        withCredentials([string(credentialsId: 'SonarExecutor', variable: 'SONAR_TOKEN')]) {
          sh 'bash ./gradlew xue:sonarqube -Dsonar.host.url=https://sonar.rschrage.org -Dsonar.login=$SONAR_TOKEN'
        }
      }
    }
  }
}
