# Local Setup

## Backend Service
0. Prerequisites: Install Maven, Java JDK 21, Docker Desktop, IntelliJ
1. Load service project into IntelliJ
2. Run mvn clean install
3. Set Up MySQL 8 Database: https://hub.docker.com/_/mysql (on Windows: Use Docker Desktop to create a container)
5. Set Up WildFly 30 WebServer: https://www.wildfly.org/downloads/ (either in IntelliJ or as standalone)
6. Set Up DataSource in Administration Console of WildFly 30 WebServer
7. Configure Properties File reflecting System Environment
8. Add Built Artefact (Results from 2.) as a Deployment to Wildfly. Flyway will initialize the database tables automatically.
9. (Optional) Set Up MS Azure OpenAI Service and configure it in properties file

## ReactJS Frontend
0. Prerequisites: Install NodeJS, npm & IntelliJ
1. Run npm install
2. Run npm serve
