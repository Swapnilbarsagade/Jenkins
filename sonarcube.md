### SonarQube Installation and Configuration Guide

Here's a detailed guide for installing and configuring SonarQube on your system.

---

### **Prerequisites**
- Ensure your system has **3GB+ RAM** for optimal performance.
- **Root or sudo privileges** are required.

---

### **Step 1: Install MySQL Database**
1. Add MySQL Community Repo:
   ```bash
   rpm -ivh http://repo.mysql.com/mysql57-community-release-el7.rpm
   rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2022
   ```

2. Install MySQL Server:
   ```bash
   yum install mysql-server -y
   ```

3. Start and Enable MySQL Service:
   ```bash
   systemctl start mysqld
   systemctl enable mysqld
   ```

4. Retrieve Temporary Password:
   ```bash
   grep 'temporary password' /var/log/mysqld.log
   ```

5. Secure MySQL Installation:
   ```bash
   mysql_secure_installation
   ```

---

### **Step 2: Install Java**
2. Install BellSoft JDK 11:
   ```bash
   wget https://download.bell-sw.com/java/11.0.4/bellsoft-jdk11.0.4-linux-amd64.rpm
   rpm -ivh bellsoft-jdk11.0.4-linux-amd64.rpm
   ```

3. Check Java Installation:
   ```bash
   java -version
   ```

---

### **Step 3: Configure Linux System for SonarQube**
1. Set `vm.max_map_count`:
   ```bash
   echo 'vm.max_map_count=262144' >> /etc/sysctl.conf
   sysctl -p
   ```

2. Increase File Descriptors:
   ```bash
   echo '* - nofile 80000' >> /etc/security/limits.conf
   ```

3. Update MySQL Configuration:
   ```bash
   sed -i -e '/query_cache_size/ d' -e '$ a query_cache_size = 15M' /etc/my.cnf
   systemctl restart mysqld
   ```

---

### **Step 4: Configure Database for SonarQube**
1. Log in to MySQL:
   ```bash
   mysql -p -u root
   ```

2. Run the following commands:
   ```sql
   CREATE DATABASE sonarqube;
   CREATE USER 'sonarqube'@'localhost' IDENTIFIED BY 'Redhat@123';
   GRANT ALL PRIVILEGES ON sonarqube.* TO 'sonarqube'@'localhost';
   FLUSH PRIVILEGES;
   ```

---

### **Step 5: Install SonarQube**
1. Install Required Tools:
   ```bash
   yum install unzip -y
   ```

2. Download and Extract SonarQube:
   ```bash
   wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-7.9.1.zip
   cd /opt
   unzip ~/sonarqube-7.9.1.zip
   mv sonarqube-7.9.1 sonar
   ```

---

### **Step 6: Configure SonarQube**
1. Update SonarQube Properties:
   ```bash
   sed -i -e '/^sonar.jdbc.username/ d' \
          -e '/^sonar.jdbc.password/ d' \
          -e '/^sonar.jdbc.url/ d' \
          -e '/^sonar.web.host/ d' \
          -e '/^sonar.web.port/ d' /opt/sonar/conf/sonar.properties

   sed -i -e '/#sonar.jdbc.username/ a sonar.jdbc.username=sonarqube' \
          -e '/#sonar.jdbc.password/ a sonar.jdbc.password=Redhat@123' \
          -e '/InnoDB/ a sonar.jdbc.url=jdbc:mysql://localhost:3306/sonarqube?useUnicode=true&characterEncoding=utf&rewriteBatchedStatements=true&useConfigs=maxPerformance' \
          -e '/#sonar.web.host/ a sonar.web.host=0.0.0.0' /opt/sonar/conf/sonar.properties
   ```

2. Create a Dedicated User for SonarQube:
   ```bash
   useradd sonar
   chown sonar:sonar /opt/sonar/ -R
   ```

3. Set SonarQube User:
   ```bash
   sed -i -e '/^#RUN_AS_USER/ c RUN_AS_USER=sonar' /opt/sonar/bin/linux-x86-64/sonar.sh
   ```

---

### **Step 7: Start SonarQube**
1. Start SonarQube Service:
   ```bash
   /opt/sonar/bin/linux-x86-64/sonar.sh start
   ```

2. Check Status:
   ```bash
   /opt/sonar/bin/linux-x86-64/sonar.sh status
   ```

3. View Logs:
   ```bash
   cat /opt/sonar/logs/*
   ```

---

### **Step 8: Access SonarQube**
- Open a browser and visit: `http://<your-server-ip>:9000`

---

### **Notes**
- For SonarQube versions higher than 7.9, you may need **OpenJDK 17** instead of BellSoft JDK 11.
- Update the JDBC credentials if needed:
   ```bash
   sonar.jdbc.username=sonarqube
   sonar.jdbc.password=Cloudblitz@123
   ```



The default username and password for a fresh SonarQube installation are:

Username: admin
Password: admin