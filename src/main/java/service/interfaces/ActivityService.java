package service.interfaces;

import model.base.SelfCareActivityBase;
import java.util.List;

public interface ActivityService extends CrudService<SelfCareActivityBase> {
    List<SelfCareActivityBase> getAllSortedByScoreDesc();
}
