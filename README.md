# IntervalApp

**Currently adding QueriesPool to guarantee query execution is database if offline!**
**2 more hours to implement required**
--------------

This is an application for optimizing integer intervals stored in database.
Database intervals can be modified via IntervalApp REST service or using third-party applications.

**Technologies used:**
1. Java 7
2. SQL (avoided using specific HQL\JPQL)
3. PostgreSQL 9.5 as DBMS
4. Spring MVC v.4
5. Spring Boot v.1.5.3 - to start application in fast and simple way. No need to use Tomcat explicitly.
6. Log4j - logging api

**User guide:**
1. Clone application and open it with any IDE
2. Application default port 8080 and context path */interval*. These values stored in resources/application.properties
file and can be changed if needed
3. PostgreSQLConnectorService contain hardcoded data to connect to db - DBMS values can be changed if needed - 
it was made to simplify application showcase and implementation. 
It is recommended to use Authentication layer in real case.
4. Run main() method of com.sobolev.controller.IntervalsServiceDataPublisher class to start application
5. Use payload.json in */resources/request* folder as an example to create REST request body
6. Send POST request to */interval/append* (default value - *localhost:8080/interval/append*) 
service with intervals list data to optimize/add it to database
7. Send POST request to */interval/select* service with single interval to select all overlapping intervals from table
8. Send DELETE request to */interval/delete* service with intervals list data to remove fully matching intervals from table
9. Every 1 hour optimization process will be started automatically for all database
assuming it was possibly modified by third party applications
10. Both ways to modify data are correct. However, it is recommended to use single */interval/append* entry point
to add new data by any application. This will guarantee that intervals will be always optimized.

--------------

**Application basics:**

a. Application offer 4 methods to operate with test_interval table
3 of them recommended to use:
1. append to insert new intervals and optimize them
2. select to get existing intervals
3. delete to delete fully matching intervals

The best way is to use */interval* service as the ONE entry point for 
any application which is going to modify test_interval table
This service support concurrent interactions, every operation is transactional and 
locks set of table rows until transaction ends

b. *optimizeAllData* should be used if we assuming that third party application will access and modify 
test_interval table bypassing */interval* service
By default it is called by *IntervalsOptimizationManager* once per hour automatically

c. Database connection manager will automatically check if connection to database exist, if
no, after some time it will close application with error

d. Database fault messages are being processed with Spring implicitly

--------------

**Suggested improvements:**

1. Create connection pool instead of creating new connection each time for new transaction request
Depends on number of transactions and amount of data
2. To improve automatic data optimization process use LastModificationTime parameter to compare
with scheduled data. If LastModificationTime differs that test_interval table was modified by
side application and optimization process should be started
3. Add authentication

--------------

**Components table with logic explanations:**

| Class                        | Description                                                                      |
| :--------------------------- | :------------------------------------------------------------------------------- |
| IntervalDatServicePublisher  | Class starting IntervalApp. Has only main() method                               |
| IntervalDataController       | @RestController annotated class. Started by IntervaldataServicePublisher         |
|                              | and provides /interval/append REST service mapping. When new intervals           |
|                              | should be added to database it is strongly recommended to use this               |
|                              | service by third party applications or within the scope of IntervalApp           |
|                              | Such an approach will allow to keep data always optimized.                       |
|                              | If there is no such possibility and test_interval being modified                 |
|                              | bypassing /interval/append service, IntervalsOptimizationManager used            |
| DatabaseConnectionManager    | Scheduled to check db connection every minute. If timeout >= 10 minutes,         |
|                              | will throw an Error and close application                                        |
| IntervalsOptimizationManager | Scheduled to optimize all data in database once per hour. Not recommended        |
|                              | to use as it may decrease application performance. Use IntervalDataController    |
|                              | instead.                                                                         |
| Interval                     | Class describing interval data. Comparable.                                      |
| IntervalDataService          | Service providing 4 intervals processing methods:                                |
|                              | 1. insertNewIntegerIntervals(List<Interval> intervals) - will insert intervals   |
|                              | into test_interval table affecting limited number of rows. Uses transaction with |
|                              | SERIALIZABLE isolation level                                                     |
|                              | 2. selectIntervals(Interval interval) - select all intervals overlapping         |
|                              | with interval                                                                    |
|                              | 3. deleteIntervals(List<Interval> intervals) - remove fully matching intervals   |
|                              | 4. optimizeAllData() - called periodically by scheduled class assuming database  |
|                              | was potentially modified by third party applications                             |
| OptimizeIntervalsService     | Support class providing optimization logic                                       |
| PostgreSQLConnectorService   | Support class providing db connection                                            |



**In case of any questions please feel free to contact me via Skype:**
sobolev_stanislav