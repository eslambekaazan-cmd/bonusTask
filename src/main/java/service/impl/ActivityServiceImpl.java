package service.impl;

import cache.CacheKey;
import cache.InMemoryCache;
import model.base.SelfCareActivityBase;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import repository.interfaces.ActivityRepository;
import service.interfaces.ActivityService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
@Primary
@Service
public class ActivityServiceImpl implements ActivityService {

    private static final String NS = "activities";
    private static final CacheKey ALL_KEY = CacheKey.of(NS, "getAll");

    private final ActivityRepository activityRepository;
    private final InMemoryCache cache = InMemoryCache.getInstance(Duration.ofMinutes(5));

    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SelfCareActivityBase> getAll() {
        return (List<SelfCareActivityBase>) cache.getOrLoad(
                ALL_KEY,
                List.class,
                activityRepository::findAll
        );
    }

    @Override
    public List<SelfCareActivityBase> getAllSortedByScoreDesc() {
       
        List<SelfCareActivityBase> copy = new ArrayList<>(getAll());
        copy.sort((a, b) -> Integer.compare(b.estimateScore(), a.estimateScore()));
        return copy;
    }

    @Override
    public SelfCareActivityBase getById(int id) {
        return activityRepository.findById(id).orElse(null);
    }

    @Override
    public SelfCareActivityBase create(SelfCareActivityBase entity) {
        SelfCareActivityBase saved = activityRepository.create(entity);
        cache.invalidate(ALL_KEY);
        return saved;
    }

    @Override
    public SelfCareActivityBase update(SelfCareActivityBase entity) {
        SelfCareActivityBase updated = activityRepository.update(entity);
        cache.invalidate(ALL_KEY);
        return updated;
    }

    @Override
    public void delete(int id) {
        activityRepository.delete(id);
        cache.invalidate(ALL_KEY);
    }

    public void clearActivitiesCache() {
        cache.invalidateNamespace(NS);
    }
}
