pipeline {
    agent {
        label 'dummy'
    }
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
                if [ -d "apache-tomcat-9.0.97" ]; then
                    sudo rm -rf apache-tomcat-9.0.97
                fi
        
                # Download and unzip Tomcat
                sudo wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.97/bin/apache-tomcat-9.0.97.zip
                sudo unzip apache-tomcat-9.0.97.zip

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
                        -Dsonar.host.url=http://43.202.110.30:9000 \
                        -Dsonar.login=$SONAR_AUTH_TOKEN
                    '''
                }
                echo "Testing completed"
            }
        }
        stage('Deploy') {
            steps {
                sh '''
                # Deploy the application to Tomcat
                mv target/*.war apache-tomcat-9.0.97/webapps/student.war

                # Start Tomcat server
                bash apache-tomcat-9.0.97/bin/catalina.sh start
                '''
                echo "Deployment completed"
            }
        }
    }
}
