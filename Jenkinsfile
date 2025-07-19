pipeline {
    agent any

    triggers {
        // Run every day at 2:30 AM
        cron('30 2 * * *')
    }

    stages {
        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Run Batch Job') {
            steps {
                retry(2) {
                    bat 'java -jar target\\springboot3-batch-csv-processor-0.0.1-SNAPSHOT.jar'
                }
            }
        }

        stage('Archive Output') {
            steps {
                archiveArtifacts artifacts: 'output/*.csv', onlyIfSuccessful: true
            }
        }
    }
}
