package com.sobolev.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.naming.NamingException;
import java.sql.*;

@Service
@RequestScope
public class PostgreSQLConnectorService {

    public PostgreSQLConnectorService()  {
    }

    public Connection establishConnectionToDB() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/postgres",
                "postgres",
                "admin");
    }
}
