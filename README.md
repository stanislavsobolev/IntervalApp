# IntervalApp

**In case of any questions please feel free to contact me via skype: sobolev_stanislav**

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
4. Application require only test_interval table
5. Run main() method of com.sobolev.controller.IntervalsServiceDataPublisher class to start application
6. Use payload.json in */resources/request* folder as an example to create REST request body
7. Send POST request to */interval/append* (default value - *localhost:8080/interval/append*) 
service with intervals list data to optimize/add it to database
8. Send POST request to */interval/select* service with single interval to select all overlapping intervals from table
9. Send DELETE request to */interval/delete* service with intervals list data to remove fully matching intervals from table
10. In case connection to database is missing, requests will be gathered into QueriesPool.
DatabaseConnectionManager will check connection every 15 seconds, and if connection restored,
Intervals stored in QueriesPool will be sent to database
11. Every 1 hour optimization process will be started automatically for all database
assuming it was possibly modified by third party applications. Automatic optimization process can be 
turned off/on by changing *run.scheduled.optimization* parameter to false/true respectively in *scheduled.properties* file
Default value is false
12. Both ways to modify data are correct. However, it is recommended to use single */interval/append* entry point
to add new data by any application. This will guarantee that intervals will be always optimized.

--------------

**Application basics:**

- Application offer 4 methods to operate with test_interval table
3 of them recommended to use:
1. append to insert new intervals and optimize them
2. select to get existing intervals
3. delete to delete fully matching intervals

The best way is to use */interval* service as the ONE entry point for 
any application which is going to modify test_interval table
This service support concurrent interactions, every operation is transactional and 
locks set of table rows until transaction ends

- In case of losing connection to database, all Intervals will be gathered in QueriesPool
They will be of 2 types: 
1. Append intervals
2. Delete intervals
They are stored in concurrent Queue by FIFO principle: first incoming element
will be transferred to database first
When connection restored, DatabaseConnectionManager will send handle requests to finish append and delete operations
for stored intervals. If connection not restored for quite long (10 minutes by default), application will be closed with error

- test_interval table structure:
```sql
CREATE TABLE test_interval (
start_i INT NOT NULL,
end_i INT NOT NULL,
CONSTRAINT end_gte_start CHECK (end_i>=start_i)
);
```

- *optimizeAllData* should be used if we assuming that third party application will access and modify 
test_interval table bypassing */interval* service
By default it is called by *IntervalsOptimizationManager* once per hour automatically

- Database fault messages are being processed with Spring implicitly


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
|                              | 1. appendIntervals(List<Interval> intervals) - will insert intervals             |
|                              | into test_interval table affecting limited number of rows. Uses transaction with |
|                              | SERIALIZABLE isolation level                                                     |
|                              | 2. selectIntervals(Interval interval) - select all intervals overlapping         |
|                              | with interval                                                                    |
|                              | 3. deleteIntervals(List<Interval> intervals) - remove fully matching intervals   |
|                              | 4. optimizeAllData() - called periodically by scheduled class assuming database  |
|                              | was potentially modified by third party applications                             |
| OptimizeIntervalsService     | Support class providing optimization logic                                       |
| PostgreSQLConnectorService   | Support class providing db connection                                            |
| QueriesPool                  | Store Intervals for Append and Delete operations if connection with db lost      |
|                              | Will automatically perform both operations when connection restored              |
| Query                        | Basic model representing pending query. Stores List<Interval> and QueryType      |
| QueriesPoolManager           | Class scheduled to run handleQueriesPool() method once a minute to try to persist|
|                              | Queries if connection to db is up                                                |
| QueryType                    | Enum which represents 2 types of operations. Based on it, corresponding          |
|                              | DataService method is called                                                     | 


**In case of any questions please feel free to contact me via Skype:**
sobolev_stanislav