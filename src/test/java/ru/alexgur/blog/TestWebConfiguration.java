package ru.alexgur.blog;

import javax.sql.DataSource;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import ru.alexgur.intershop.Main;

@SpringBootTest(classes = Main.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application.yaml")
public abstract class TestWebConfiguration {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("myblogTestDB")
                .addScript("test-schema.sql")
                .build();
    }
}