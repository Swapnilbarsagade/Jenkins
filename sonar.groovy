pipeline {
    agent any
    stages {
        stage('Pull') {
            steps {
                echo "Pulling from GitHub"
                git url: 'https://github.com/AnupDudhe/studentapp-ui'
            }
        }
        stage('Build') {
            steps {
                sh '''
                # Update system and install dependencies
                sudo apt update
                sudo apt install -y maven unzip

                # Remove existing Tomcat if it exists
                if [ -d "apache-tomcat-9.0.98" ]; then
                    sudo rm -rf apache-tomcat-9.0.98
                fi
        
                # Download and unzip Tomcat
                sudo wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.97/bin/apache-tomcat-9.0.98.zip
                sudo unzip apache-tomcat-9.0.98.zip

                # Build the project
                mvn clean package
                '''
                echo "Build completed"
            }
        }
        stage('Test') {
            steps {
                withSonarQubeEnv('SonarQube') { // Replace 'SonarQube' with your SonarQube configuration name
                    sh '''
                    mvn sonar:sonar \
                    -Dsonar.projectKey=studentapp \
                    -Dsonar.host.url=http://3.38.190.6:9000 \
                    -Dsonar.login=d00aedb0f9d6fc51aaaae16c59d5fb595395a6ae
                                        '''
                }
                echo "Testing completed"
            }
        }
        stage('Deploy') {
            steps {
                sh '''
                # Deploy the application to Tomcat
                mv target/*.war apache-tomcat-9.0.98/webapps/student.war

                # Start Tomcat server
                bash apache-tomcat-9.0.98/bin/catalina.sh start
                '''
                echo "Deployment completed"
            }
        }
    }
}
