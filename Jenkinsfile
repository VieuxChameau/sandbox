pipeline {
    agent {
        docker {
            image 'adoptopenjdk/openjdk12'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('Build and Test') {
            steps {
                sh "./mvnw clean package -Pjar -Djenkins.buildTag=${env.BUILD_TAG}"
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }
    stage('Deploy') {
        when {
            expression {
                currentBuild.result == null || currentBuild.result == 'SUCCESS'
            }
        }
        steps {
            sh 'echo TODO'
        }
    }

}