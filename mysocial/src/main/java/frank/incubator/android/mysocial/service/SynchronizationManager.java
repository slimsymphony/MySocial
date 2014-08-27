package frank.incubator.android.mysocial.service;

import frank.incubator.android.mysocial.model.Destination;
import frank.incubator.android.mysocial.model.DestinationEnum;
import frank.incubator.android.mysocial.model.Feed;

/**
 * Interface of Synchronize feeds with social network.
 * Created by f78wang on 8/22/14.
 */
public interface SynchronizationManager {
    Destination register(String user, DestinationEnum destType);
    boolean sync(Feed feed, Destination dest);
}
