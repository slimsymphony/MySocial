package frank.incubator.android.mysocial.model;

import com.alibaba.fastjson.JSON;

import java.sql.Timestamp;
import java.util.List;

/**
 * Basic Data Unit for social communication.
 * Created by f78wang on 8/20/14.
 */
public class Feed {
    private long id;
    private String topic;
    private String content;
    private List<Attachment> attachments;
    private Timestamp timestamp;
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString(){
       return JSON.toJSONString(this);
    }
}
