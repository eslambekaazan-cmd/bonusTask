package repository.jdbc;

import db.DatabaseConnection;
import exception.DatabaseOperationException;
import model.ProductivityActivity;
import model.Routine;
import model.WellnessActivity;
import model.base.SelfCareActivityBase;
import patterns.ActivityFactory;
import repository.interfaces.ActivityRepository;
import repository.interfaces.RoutineRepository;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ActivityRepositoryJdbc implements ActivityRepository {

    private final DatabaseConnection db;
    private final RoutineRepository routineRepo;
    private final ActivityFactory factory;

    public ActivityRepositoryJdbc(DatabaseConnection db, RoutineRepository routineRepo, ActivityFactory factory) {
        this.db = db;
        this.routineRepo = routineRepo;
        this.factory = factory;
    }

    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM activities WHERE name = ? LIMIT 1";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed existsByName", e);
        }
    }

    @Override
    public SelfCareActivityBase create(SelfCareActivityBase entity) {
        String sql = """
            INSERT INTO activities
            (name, routine_id, activity_type, kind, minutes, intensity, difficulty, focus_area)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, entity.getName());
            ps.setInt(2, entity.getRoutine().getId());

            if (entity instanceof WellnessActivity w) {
                ps.setString(3, "WELLNESS");
                ps.setString(4, "WELLNESS");

                ps.setInt(5, w.getMinutes());
                ps.setString(6, w.getIntensity());
                ps.setNull(7, Types.INTEGER);
                ps.setNull(8, Types.VARCHAR);

            } else if (entity instanceof ProductivityActivity p) {
                ps.setString(3, "PRODUCTIVITY");
                ps.setString(4, "PRODUCTIVITY");

                ps.setNull(5, Types.INTEGER);
                ps.setNull(6, Types.VARCHAR);
                ps.setInt(7, p.getDifficulty());
                ps.setString(8, p.getFocusArea());

            } else {
                throw new DatabaseOperationException("Unknown activity subclass", null);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) entity.setId(rs.getInt(1));
            return entity;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to create Activity", e);
        }
    }

    @Override
    public Optional<SelfCareActivityBase> findById(int id) {
        String sql = "SELECT * FROM activities WHERE id=?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return Optional.empty();
            return Optional.of(mapRow(rs));

        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to find Activity", e);
        }
    }

    @Override
    public List<SelfCareActivityBase> findAll() {
        String sql = "SELECT * FROM activities ORDER BY id";
        List<SelfCareActivityBase> list = new ArrayList<>();

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to list Activities", e);
        }
    }

    @Override
    public SelfCareActivityBase update(SelfCareActivityBase entity) {
        String sql = """
            UPDATE activities
            SET name=?, routine_id=?, activity_type=?, kind=?, minutes=?, intensity=?, difficulty=?, focus_area=?
            WHERE id=?
            """;

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, entity.getName());
            ps.setInt(2, entity.getRoutine().getId());

            if (entity instanceof WellnessActivity w) {
                ps.setString(3, "WELLNESS");
                ps.setString(4, "WELLNESS");

                ps.setInt(5, w.getMinutes());
                ps.setString(6, w.getIntensity());
                ps.setNull(7, Types.INTEGER);
                ps.setNull(8, Types.VARCHAR);

            } else if (entity instanceof ProductivityActivity p) {
                ps.setString(3, "PRODUCTIVITY");
                ps.setString(4, "PRODUCTIVITY");

                ps.setNull(5, Types.INTEGER);
                ps.setNull(6, Types.VARCHAR);
                ps.setInt(7, p.getDifficulty());
                ps.setString(8, p.getFocusArea());

            } else {
                throw new DatabaseOperationException("Unknown activity subclass", null);
            }

            ps.setInt(9, entity.getId());
            ps.executeUpdate();
            return entity;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to update Activity", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM activities WHERE id=?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to delete Activity", e);
        }
    }

    private SelfCareActivityBase mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int routineId = rs.getInt("routine_id");
        String activityType = rs.getString("activity_type");

        Routine routine = routineRepo.findById(routineId).orElse(null);

        Integer minutes = (Integer) rs.getObject("minutes");
        String intensity = rs.getString("intensity");
        Integer difficulty = (Integer) rs.getObject("difficulty");
        String focusArea = rs.getString("focus_area");

        return factory.create(
                activityType,
                id,
                name,
                routine,
                minutes,
                intensity,
                difficulty,
                focusArea
        );
    }
}
