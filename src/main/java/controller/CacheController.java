package controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.impl.ActivityServiceImpl;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    private final ActivityServiceImpl activityService;

    public CacheController(ActivityServiceImpl activityService) {
        this.activityService = activityService;
    }

    @DeleteMapping("/activities")
    public ResponseEntity<Void> clearActivitiesCache() {
        activityService.clearActivitiesCache();
        return ResponseEntity.noContent().build();
    }
}
