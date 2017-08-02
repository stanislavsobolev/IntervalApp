package com.sobolev.service;

import com.sobolev.model.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.sql.*;
import java.util.List;

@Service
@RequestScope
public class IntervalDataService {

    private OptimizeIntervalsService optimizeService;
    private PostgreSQLConnectorService connectorService;

    private final String SQL_SELECT = "SELECT * FROM test_interval WHERE (start_i BETWEEN ? AND ?) OR (end_i BETWEEN ? AND ?)";
    private final String SQL_DELETE = "DELETE FROM test_interval WHERE (start_i BETWEEN ? AND ?) OR (end_i BETWEEN ? AND ?)";
    private final String SQL_INSERT = "INSERT INTO test_interval VALUES(?, ?)";

    public IntervalDataService() {
    }

    public void insertNewIntegerIntervals(List<Interval> intervals) {
        PreparedStatement stmt;
        ResultSet rs;

        List<Interval> optimizedIntervals = optimizeService.optimize(intervals);
        Integer minimalInt = optimizedIntervals.get(0).getStartI();
        Integer maximalInt = optimizedIntervals.get(optimizedIntervals.size() - 1).getEndI();

        try (Connection con = connectorService.establishConnectionToDB()) {
            con.setAutoCommit(false);
            stmt = createPreparedStatement(con, SQL_SELECT, minimalInt, maximalInt, minimalInt, maximalInt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                optimizedIntervals.add(new Interval(rs.getInt(1), rs.getInt(2)));
            }

            optimizedIntervals = optimizeService.optimize(optimizedIntervals);
            createPreparedStatement(con, SQL_DELETE, minimalInt, maximalInt, minimalInt, maximalInt).executeUpdate();

            stmt = createPreparedStatement(con, SQL_INSERT);
            for(Interval interval : optimizedIntervals) {
                stmt.setInt(1, interval.getStartI());
                stmt.setInt(2, interval.getEndI());
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PreparedStatement createPreparedStatement(Connection con, String query, int... integers) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(query);
        for(int i = 1; i<= integers.length; i++) {
            stmt.setInt(i, integers[i-1]);
        }
        return stmt;
    }

    @Autowired
    public void setOptimizeService(OptimizeIntervalsService optimizeService) {
        this.optimizeService = optimizeService;
    }

    @Autowired
    public void setCommectorService(PostgreSQLConnectorService connectorService) {
        this.connectorService = connectorService;
    }
}
