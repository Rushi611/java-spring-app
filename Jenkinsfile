pipeline {
    agent any

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
