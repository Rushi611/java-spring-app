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
                bat 'java -jar target\\springboot3-batch-csv-processor-0.0.1-SNAPSHOT.jar'
            }
        }
    }
}
