package utils;

import db.DatabaseConnection;
import model.Routine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import patterns.ActivityFactory;
import repository.interfaces.ActivityRepository;
import repository.interfaces.RoutineRepository;
import repository.interfaces.RoutineTypeRepository;
import repository.jdbc.ActivityRepositoryJdbc;
import repository.jdbc.RoutineRepositoryJdbc;
import repository.jdbc.RoutineTypeRepositoryJdbc;
import service.impl.ActivityServiceImpl;
import service.impl.RoutineServiceImpl;
import service.interfaces.ActivityService;
import service.interfaces.CrudService;

@Configuration
public class AppBeansConfig {

    @Bean
    public DatabaseConnection databaseConnection() {
      
        return DatabaseConnection.getInstance(
                "jdbc:postgresql://localhost:5432/postgres",
                "postgres",
                "1234"
        );
    }

    @Bean
    public ActivityFactory activityFactory() {
        return new ActivityFactory();
    }

    @Bean
    public RoutineTypeRepository routineTypeRepository(DatabaseConnection db) {
        return new RoutineTypeRepositoryJdbc(db);
    }

    @Bean
    public RoutineRepository routineRepository(
            DatabaseConnection db,
            RoutineTypeRepository routineTypeRepository
    ) {
        return new RoutineRepositoryJdbc(db, routineTypeRepository);
    }


    public ActivityRepository activityRepository(
            DatabaseConnection db,
            RoutineRepository routineRepository,
            ActivityFactory factory
    ) {
        return new ActivityRepositoryJdbc(db, routineRepository, factory);
    }

    @Bean
    public ActivityService activityService(ActivityRepository activityRepository) {
        return new ActivityServiceImpl(activityRepository);
    }

    @Bean
    public CrudService<Routine> routineService(RoutineRepository routineRepository) {
        return new RoutineServiceImpl(routineRepository);
    }
}
