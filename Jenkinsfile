pipeline {
    agent any

    tools {
        maven 'Maven_3.8'
        jdk 'Java_17'
    }

    stages {
        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Run Batch Job') {
            steps {
                bat 'java -jar target\\*.jar'
            }
        }
    }
}
