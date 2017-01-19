set CATALINA_HOME=C:\Users\575724\Desktop\apache-tomcat-8.0.33
call %CATALINA_HOME%\bin\shutdown.bat
call mvn package
call cp target\attune.war %CATALINA_HOME%\webapps
call %CATALINA_HOME%\bin\startup.bat