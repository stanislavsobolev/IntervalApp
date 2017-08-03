package com.sobolev.service;

import com.sobolev.model.Interval;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequestScope
public class IntervalDataService {

    Logger log = LogManager.getLogger(IntervalDataService.class);

    private OptimizeIntervalsService optimizeService;
    private PostgreSQLConnectorService connectorService;

    private final String SQL_SELECT = "SELECT * FROM test_interval";
    private final String SQL_DELETE = "DELETE FROM test_interval";
    private final String WHERE = " WHERE (start_i BETWEEN ? AND ?) OR (end_i BETWEEN ? AND ?)";
    private final String SQL_INSERT = "INSERT INTO test_interval VALUES(?, ?)";

    public IntervalDataService() {
    }

    public IntervalDataService(OptimizeIntervalsService optimizeService, PostgreSQLConnectorService connectorService) {
        this.optimizeService = optimizeService;
        this.connectorService = connectorService;
    }

    /**
     * <code></code>InsertNewIntegerIntervals</code> will get, optimize and update rows which
     * meet the condition of intervals overlapping
     * <p>
     * Two lists are being processed:
     * 1. <code>Interval</code> list sent over POST request
     * 2. <code>Interval</code> list stored in DB and meeting the overlap condition
     * <p>
     * They both optimized, united into single list and stored into DB instead of
     * old interval list
     * <p>
     * DB interaction represented with transaction.
     * Transaction isolation used: SERIALIZABLE
     * <p>
     * Transaction steps:<p>
     * 1. SELECT overlapping intervals from DB<p>
     * 2. DELETE overlapping intervals<p>
     * 3. INSERT optimized list into DB<p>
     *
     * @Note Interval list in DB will be always optimized if third-party applications
     * use /interval/append of IntervalApp method instead of updating data by themselves directly
     * In this case there will be no need to use periodic optimizeAllData() method
     */
    public List<Interval> insertNewIntegerIntervals(List<Interval> intervals) {
        PreparedStatement stmt;
        ResultSet rs;

        List<Interval> optimizedIntervals = optimizeService.optimize(intervals);
        Integer minimalInt = optimizedIntervals.get(0).getStartI();
        Integer maximalInt = optimizedIntervals.get(optimizedIntervals.size() - 1).getEndI();

        try (Connection con = connectorService.establishConnectionToDB()) {
            con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            con.setAutoCommit(false);
            stmt = createPreparedStatement(con, SQL_SELECT + WHERE, minimalInt, maximalInt, minimalInt, maximalInt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                optimizedIntervals.add(new Interval(rs.getInt(1), rs.getInt(2)));
            }

            optimizedIntervals = optimizeService.optimize(optimizedIntervals);
            createPreparedStatement(con, SQL_DELETE + WHERE, minimalInt, maximalInt, minimalInt, maximalInt).executeUpdate();

            stmt = createPreparedStatement(con, SQL_INSERT);
            for (Interval interval : optimizedIntervals) {
                stmt.setInt(1, interval.getStartI());
                stmt.setInt(2, interval.getEndI());
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();
            con.commit();
        } catch (Exception e) {
            log.error(e);
        }
        return optimizedIntervals;
    }

    /**
     * selectIntervals used to select list of intervals from database matching overlap condition with
     * interval param limits
     * @param interval shows limits of select query
     */
    public List<Interval> selectIntervals(Interval interval) {
        List<Interval> selectedIntervals = new ArrayList<>();
        if(interval != null && interval.getStartI() != null && interval.getStartI() != null) {
            PreparedStatement stmt;
            ResultSet rs;
            Integer minimalInt = interval.getStartI();
            Integer maximalInt = interval.getEndI();

            try(Connection con = connectorService.establishConnectionToDB()) {
                stmt = createPreparedStatement(con, SQL_SELECT + WHERE, minimalInt, maximalInt, minimalInt, maximalInt);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    selectedIntervals.add(new Interval(rs.getInt(1), rs.getInt(2)));
                }
            }
            catch (Exception e) {
                log.error(e);
            }
        }
        return selectedIntervals;
    }

    /**
     * removeIntervals method used to remove intervals
     */
    public void deleteIntervals(List<Interval> intervals) {
        PreparedStatement stmt;

        if(intervals != null && !intervals.isEmpty()) {
            optimizeService.verifyData(intervals);

            try (Connection con = connectorService.establishConnectionToDB()) {
                con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                con.setAutoCommit(false);
                stmt = createPreparedStatement(con, SQL_DELETE + " WHERE start_i = ? AND end_i = ?");
                for (Interval interval : intervals) {
                    stmt.setInt(1, interval.getStartI());
                    stmt.setInt(2, interval.getEndI());
                    stmt.addBatch();
                }
                stmt.executeBatch();
                con.commit();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    /**
     * We assume that third-party applications may update Intervals data directly, avoiding /append service
     * And to guarantee data optimization application will periodically call optimizeAllData() method
     *<p>
     * @Important Performance can be increased if update data using /append service of IntervalApp
     */
    public void optimizeAllData() {
        log.info("scheduled data optimization process started");
        PreparedStatement stmt;
        ResultSet rs;
        List<Interval> optimizedIntervals;

        try (Connection con = connectorService.establishConnectionToDB()) {
            con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            con.setAutoCommit(false);
            rs = createPreparedStatement(con, SQL_SELECT).executeQuery();
            optimizedIntervals = new ArrayList<>();
            while (rs.next()) {
                optimizedIntervals.add(new Interval(rs.getInt(1), rs.getInt(2)));
            }
            optimizedIntervals = optimizeService.optimize(optimizedIntervals);
            stmt = createPreparedStatement(con, SQL_INSERT);
            for (Interval interval : optimizedIntervals) {
                stmt.setInt(1, interval.getStartI());
                stmt.setInt(2, interval.getEndI());
                stmt.addBatch();
            }
            stmt.addBatch();
            con.commit();
        } catch (Exception e) {
            log.error(e);
        }
    }

    private PreparedStatement createPreparedStatement(Connection con, String query, int... integers) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(query);
        for (int i = 1; i <= integers.length; i++) {
            stmt.setInt(i, integers[i - 1]);
        }
        return stmt;
    }

    @Autowired
    public void setOptimizeService(OptimizeIntervalsService optimizeService) {
        this.optimizeService = optimizeService;
    }

    @Autowired
    public void setConnectorService(PostgreSQLConnectorService connectorService) {
        this.connectorService = connectorService;
    }
}
