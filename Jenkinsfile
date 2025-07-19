pipeline{
    agent any
    
    tools{
        maven 'maven_3.8'
        jdk 'jdk_17'
    }
    stages{
        stage(build){
            steps{
                sh 'maven clean package'
            }
        }

        stage('Run Batch Job'){
            steps{
                sh 'java -jar target/*.jar'
            }
        }
    }
