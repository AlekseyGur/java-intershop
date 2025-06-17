package ru.alexgur.intershop.system.config;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;
import org.testcontainers.containers.PostgreSQLContainer;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import jakarta.annotation.PostConstruct;

@Configuration
@EnableR2dbcRepositories(basePackages = { "ru.alexgur.intershop" })
public class R2dbcConfig {

    private String username = "testuser";
    private String password = "testpass";
    private String host = "localhost";
    private String dbName = "testdb";
    private String protocol = "postgresql";

    // @Value("${testcontainers.postgresql.port}")
    private Integer dynamicPort = 5432;

    @Autowired
    private PostgreSQLContainer<?> postgresqlContainer;

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        setTestcontainerPort();

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql"));
        populator.addScript(new ClassPathResource("import.sql"));

        initializer.setDatabasePopulator(populator);

        return initializer;
    }

    @PostConstruct
    public void setTestcontainerPort() {
        dynamicPort = postgresqlContainer.getFirstMappedPort();
        System.out.println("Testcontainer Port: " + postgresqlContainer.getFirstMappedPort());
        System.out.println("Testcontainer ContainerId: " + postgresqlContainer.getContainerId());
        System.out.println("Testcontainer ContainerName: " + postgresqlContainer.getContainerName());
        System.out.println("Testcontainer Host: " + postgresqlContainer.getHost());
        System.out.println("Testcontainer DriverClassName: " + postgresqlContainer.getDriverClassName());
        System.out.println("Testcontainer Password: " + postgresqlContainer.getPassword());
        System.out.println("Testcontainer Username: " + postgresqlContainer.getUsername());
        System.out.println("Testcontainer DatabaseName: " + postgresqlContainer.getDatabaseName());
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions.Builder options = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "pool")
                .option(ConnectionFactoryOptions.PROTOCOL, protocol)
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
