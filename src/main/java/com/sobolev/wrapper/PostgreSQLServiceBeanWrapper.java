package com.sobolev.wrapper;

import com.sobolev.service.PostgreSQLConnectorService;

public class PostgreSQLServiceBeanWrapper {
    PostgreSQLConnectorService service;

    public PostgreSQLServiceBeanWrapper(PostgreSQLConnectorService service) {
        this.service = service;
    }
}
