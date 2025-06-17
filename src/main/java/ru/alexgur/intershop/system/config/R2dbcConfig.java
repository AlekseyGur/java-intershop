package ru.alexgur.intershop.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;

@Configuration
@EnableR2dbcRepositories(basePackages = { "ru.alexgur.intershop" })
public class R2dbcConfig {

    private String username = "testuser";
    private String password = "testpass";
    private String host = "localhost";
    private String dbName = "testdb";

    @Value("${testcontainers.postgresql.port}")
    private Integer dynamicPort;

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        // Настройте ResourceDatabasePopulator для ваших SQL скриптов
        // ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        // populator.addScript(new ClassPathResource("schema.sql"));
        // initializer.setDatabasePopulator(populator);

        // initializer.setDatabasePopulator(new ResourceDatabasePopulator(new
        // ClassPathResource("import.sql")));
        return initializer;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions.Builder options = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "pool")
                .option(ConnectionFactoryOptions.PROTOCOL, "postgresql")
                .option(ConnectionFactoryOptions.USER, this.username)
                .option(ConnectionFactoryOptions.PASSWORD, this.password)
                .option(ConnectionFactoryOptions.HOST, host)
                .option(ConnectionFactoryOptions.PORT, dynamicPort)
                .option(ConnectionFactoryOptions.DATABASE, dbName);

        return ConnectionFactoryBuilder.withOptions(options)
                .build();
    }

    @Bean
    public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
        return DatabaseClient.builder()
                .connectionFactory(connectionFactory)
                .build();
    }

    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }
}
