pipeline {
    agent{
        label 'dummy'
    } 
    stages{
        stage (pull){
            steps{
                echo "we are pulling from github"
                git "https://github.com/AnupDudhe/studentapp-ui"
            }   
        }
        stage (build){
            steps{
                sh '''
                
                sudo apt update
                sudo apt install maven -y
                sudo apt install unzip -y
                sudo wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.97/bin/apache-tomcat-9.0.97.zip
                sudo unzip apache-tomcat-9.0.97.zip
                sudo mvn clean package
                
                '''
                echo "we are building"
            }   
        }

         stage (test){
            steps{
                sh '''mvn sonar:sonar \\
                      -Dsonar.projectKey=studentapp \\
                      -Dsonar.host.url=http://13.125.4.135:9000 \\
                      -Dsonar.login=ece555bcfcc2a48216418caac40b3abcc22b228d'''
                echo "we are testing"
            }   
        }

        stage (deploy){
            steps{
                sh '''
                sudo mv target/*.war  apache-tomcat-9.0.97/webapps/student.war
                sudo bash apache-tomcat-9.0.97/bin/catalina.sh start
                '''
                echo "we are configuring"
            }   
        }
    }










}