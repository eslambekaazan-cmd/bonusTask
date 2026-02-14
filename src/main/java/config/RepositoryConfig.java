package config;

import db.DatabaseConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import patterns.ActivityFactory;
import repository.interfaces.ActivityRepository;
import repository.interfaces.RoutineRepository;
import repository.jdbc.ActivityRepositoryJdbc;

@Configuration
public class RepositoryConfig {

    @Bean
    public ActivityRepository activityRepository(
            DatabaseConnection db,
            RoutineRepository routineRepo,
            ActivityFactory factory
    ) {
        return new ActivityRepositoryJdbc(db, routineRepo, factory);
    }
}
