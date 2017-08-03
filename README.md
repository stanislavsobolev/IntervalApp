# IntervalApp

This is an application for optimizing integer intervals stored in database.
Database intervals can be modified via IntervalApp REST service or using third-party applications.

Technologies used:
1. Java 7
2. SQL (avoided using specific HQL\JPQL)
3. PostgreSQL 9.5 as DBMS
4. Spring MVC v.4
5. Spring Boot v.1.5.3 - to start application in fast and simple way. No need to use Tomcat explicitly.
6. Log4j - logging api

User guide:
1. Clone application and open it with any IDE
2. Application default port 8080 and context path /interval. These values stored in rexources/application.properties 
file and can be changed if needed
3. Run main() method of com.sobolev.controller.IntervalsServiceDataPublisher class to start application
4. Send REST requests to /interval/subscribe service with intervals data to optimize/add it to database
5. Every 1 hour optimization process will be started automatically for all database 
assuming it was possibly modified by third party applications
6. Both ways to modify data are correct. However, it is recommended to use single /interval/subscribe entry point
to add new data by any application. This will guarantee that intervals will be always optimized.
