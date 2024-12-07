pipeline {
    agent {
        label 'dummy'  // Mention the label of your Jenkins agent
    }
    environment {
        // Define environment variables
        S3_BUCKET = 'swapbuck4'
        VERSION = "${env.BUILD_NUMBER}"  // Use Jenkins build number for versioning
        ARTIFACT_NAME = "student-${VERSION}.war"  // Artifact name with version
    }
    stages {
        stage('Pull') {
            steps {
                echo "Pulling from GitHub..."
                git "https://github.com/AnupDudhe/studentapp-ui"
            }
        }
        stage('Build') {
            steps {
                echo "Building the application..."
                sh '''
                sudo mvn clean package  # Build the WAR file
                sudo apt update 
                sudo apt install unzip -y
                sudo curl -O https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.97/bin/apache-tomcat-9.0.97.zip
                sudo unzip -o apache-tomcat-9.0.97.zip 
                '''
            }
        }
        stage('Upload to S3') {
            steps {
                echo "Uploading WAR file to S3..."
                sh '''
                # Upload the WAR file to S3 with versioning
                aws s3 cp target/*.war s3://$S3_BUCKET/$ARTIFACT_NAME
                '''
            }
        }
        stage('Test') {
            steps {
                echo "Running tests..."
                sh '''
                # Add your testing commands here
                echo "Tests passed!"
                '''
            }
        }
        stage('Deploy') {
            steps {
                echo "Deploying the application from S3..."
                sh '''
                # Ensure proper ownership and permissions for Tomcat directory
                sudo chown -R ubuntu:ubuntu apache-tomcat-9.0.96
                sudo chmod -R 755 apache-tomcat-9.0.96

                # Download the WAR file from S3
                aws s3 cp s3://$S3_BUCKET/$ARTIFACT_NAME apache-tomcat-9.0.96/webapps/student.war

                # Start Tomcat
                sudo bash apache-tomcat-9.0.96/bin/catalina.sh start
                echo "Deployment complete."
                '''
            }
        }
    }
    post {
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}
