package frank.incubator.android.mysocial.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import frank.incubator.android.mysocial.model.Attachment;
import frank.incubator.android.mysocial.model.Feed;

/**
 * Interface of Feed/Attachment Manager.
 * Defined the api for content related operations.
 * Created by f78wang on 8/20/14.
 */
public interface FeedManager {
    long newFeed(Feed feed);

    boolean delFeed(long feedId);

    boolean updateFeed(Feed feed);

    List<Feed> queryFeed(Map<String, Object> condition, int limit, String orderBy);

    long newAttachment(Attachment attach, InputStream in);

    boolean delAttachment(long attachId);

    List<Attachment> getAttachmentsByFeed(long feedId);

    Attachment getAttachment(long id);

    InputStream getAttachmentContent(long id);
}
