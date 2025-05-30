package ru.alexgur.intershop;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(classes = ru.alexgur.intershop.Main.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application.yaml")
@Sql(scripts = "classpath:import.sql")
public abstract class TestWebConfiguration {

}