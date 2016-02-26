## Commands to remote execute for dev


    mvn clean package ; scp target/BDSPlugin-2.0.X-SNAPSHOT.jar cc-integration:/opt/sonarqube-5.1.1/extensions/plugins/BDSPlugin-2.0.X-SNAPSHOT.jar ; ssh cc-integration 'chown builder /opt/sonarqube-5.1.1/extensions/plugins/BDSPlugin-2.0.X-SNAPSHOT.jar' ; ssh cc-integration ' /etc/init.d/sonar-tomcat restart' ; ssh cc-integration 'tail -f /opt/sonarqube-5.1.1/logs/sonar.log'
