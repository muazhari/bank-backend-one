package com.bank.backend.one.outers.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationRepository {

    @Autowired
    @Qualifier(
            value = "postgresOneDatabaseClient"
    )
    private DatabaseClient postgresOneDatabaseClient;


}
