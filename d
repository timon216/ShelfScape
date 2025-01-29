warning: in the working copy of 'shelfscape/pom.xml', LF will be replaced by CRLF the next time Git touches it
warning: in the working copy of 'shelfscape/src/main/resources/application.properties', LF will be replaced by CRLF the next time Git touches it
[1mdiff --git a/shelfscape/pom.xml b/shelfscape/pom.xml[m
[1mindex eb990b8..23f3943 100644[m
[1m--- a/shelfscape/pom.xml[m
[1m+++ b/shelfscape/pom.xml[m
[36m@@ -54,6 +54,7 @@[m
 		<dependency>[m
 			<groupId>com.mysql</groupId>[m
 			<artifactId>mysql-connector-j</artifactId>[m
[32m+[m			[32m<version>8.0.33</version>[m
 			<scope>runtime</scope>[m
 		</dependency>[m
 		<dependency>[m
[1mdiff --git a/shelfscape/src/main/resources/application.properties b/shelfscape/src/main/resources/application.properties[m
[1mindex ecf7433..85d890b 100644[m
[1m--- a/shelfscape/src/main/resources/application.properties[m
[1m+++ b/shelfscape/src/main/resources/application.properties[m
[36m@@ -1 +1,13 @@[m
 spring.application.name=ShelfScape[m
[32m+[m
[32m+[m[32m# MySQL Database Configuration[m
[32m+[m[32mspring.datasource.url=jdbc:mysql://localhost:3306/shelfscape[m
[32m+[m[32mspring.datasource.username=root[m
[32m+[m[32mspring.datasource.password=onomatopeja[m
[32m+[m[32mspring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver[m
[32m+[m
[32m+[m[32m# Hibernate Configuration[m
[32m+[m[32mspring.jpa.hibernate.ddl-auto=update[m
[32m+[m[32mspring.jpa.database-platform=org.hibernate.dialect.MySQLDialect[m
[32m+[m[32mspring.jpa.show-sql=true[m
[41m+[m
