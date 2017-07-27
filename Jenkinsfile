#!/usr/bin/env groovy
node {
   def mvnHome
   stage('Preparation') { // for display purposes
      // Get some code from a GitHub repository
      git 'https://github.com/vgrys/activemq.git'
      // Get the Maven tool.
      // ** NOTE: This 'M3' Maven tool must be configured
      // **       in the global configuration.
      mvnHome = tool 'M2'
   }
   stage 'promotion'
      def userInput = input(
      id: 'userInput', message: 'Let\'s promote?', parameters: [
      [$class: 'TextParameterDefinition', defaultValue: 'uat', description: 'Environment', name: 'env']
      ])
      echo ("Env: "+userInput)
      // Starting build process
   stage('Build') {
      // Run the maven build
      if (isUnix()) {
         sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore -Dmaven.test.skip.exec=true clean package"
      } else {
         bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean package/)
      }
   }
   stage('Results') {
      junit '**/target/surefire-reports/TEST-*.xml'
      archive 'target/*.jar'
   }
}
