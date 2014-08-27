package frank.incubator.android.mysocial.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import frank.incubator.android.mysocial.common.CommonUtils;
import frank.incubator.android.mysocial.common.Constants;
import frank.incubator.android.mysocial.model.Attachment;
import frank.incubator.android.mysocial.model.Feed;

/**
 * FeedManager's default implementation. Use sqlite to save all the feeds and attachment metadata.
 * Use file to save all the attachment entities.
 *
 * Created by f78wang on 8/20/14.
 */
public class DefaultFeedManagerImpl implements FeedManager {

    private static DefaultFeedManagerImpl instance;

    public static DefaultFeedManagerImpl getInstance(Context context){
        if(instance == null)
            instance = new DefaultFeedManagerImpl(context);
        return instance;
    }

    private DefaultFeedManagerImpl(Context context){
        helper = new SqlHelper(context);
        db = helper.getDb();
        this.context = context;
    }

    //private final static String NEW_FEED = "INSERT INTO feeds( topic,content,timestamp,status) VALUES(?,?,?,?)";
    //private final static String DEL_FEED = "DELETE FROM feeds WHERE id = ?";
    //private final static String UPDATE_FEED = "UPDATE feeds set topic=?,content=?,status=? WHERE id = ?";
    //private final static String QUERY_FEED = "SELECT * FROM feeds where ";
    private final static String FEED_TABLE = "feeds";
    private final static String ATTACHMENT_TABLE = "attachments";
    private final static String STORE_ATTACHMENT_EXT = "store_attachment_external";
    private Context context;
    private SQLiteDatabase db;
    private SqlHelper helper;


    private void checkConnection(){
        try{
            if(db == null){
                db = helper.getDb();
            }
            if(!db.isOpen()){
                CommonUtils.close(db);
                db = helper.getDb();
            }
        }catch(Exception ex){
            Log.e(Constants.LOG_TAG,"check sqlite connection met exception.\n" + CommonUtils.getStack(ex));
            db = helper.getDb();
        }
    }

    @Override
    public long newFeed(Feed feed) {
        if( feed != null ){
            ContentValues values = new ContentValues();
            values.put("topic", feed.getTopic());
            values.put("content", feed.getContent());
            values.put("timestamp", feed.getTimestamp().getTime());
            values.put("status", feed.getStatus());
            checkConnection();
            long id = db.insert( FEED_TABLE, null, values );
            feed.setId(id);
            return id;
            //db.execSQL( NEW_FEED, new Object[]{ feed.getTopic(), feed.getContent(), feed.getTimestamp(), feed.getStatus() } );
        }
        return -1;
    }

    @Override
    public boolean delFeed(long feedId) {
        boolean ret = false;
        if( feedId > 0 ){
            checkConnection();
            ret = ( db.delete( FEED_TABLE,"id=?", new String[]{String.valueOf(feedId)} ) > 0 );
        }
        return ret;
    }

    @Override
    public boolean updateFeed(Feed feed) {
        boolean ret;
        try{
            ContentValues values = new ContentValues();
            values.put("topic", feed.getTopic());
            values.put("content", feed.getContent());
            values.put("status", feed.getStatus());
            checkConnection();
            ret = ( db.update(FEED_TABLE, values, "id=?", new String[]{String.valueOf(feed.getId())}) > 0 );
            //db.execSQL(UPDATE_FEED, new Object[]{ feed.getTopic(), feed.getContent(), feed.getTimestamp(), feed.getStatus(), feed.getId() } );
        }catch(SQLException ex){
            Log.e(Constants.LOG_TAG, "Update Feed failed.feed=" + feed+".\n" + CommonUtils.getStack(ex));
            ret = false;
        }
        return ret;
    }

    @Override
    public List<Feed> queryFeed( Map<String, Object> condition, int limit, String orderBy ) {
        List<Feed> feeds = new ArrayList<Feed>();
        String[] vals = null;
        Cursor cursor = null;
        StringBuilder sb = new StringBuilder( 50 );
        if(condition != null){
            vals = new String[condition.size()];
            int i=0;
            for( String k : condition.keySet() ){
                if( i >0 )
                    sb.append(" and ");
                sb.append(k).append("=?");
                vals[i++] = String.valueOf(condition.get(k));
            }
        }
        try{
            checkConnection();
            cursor = db.query(FEED_TABLE,null,sb.toString(),vals,null,null,orderBy,String.valueOf(limit));
            if(cursor.getCount() != 0){
                cursor.moveToFirst();
                Feed feed;
                do{
                    feed = new Feed();
                    feed.setId( cursor.getLong(cursor.getColumnIndex("id")) );
                    feed.setTimestamp(new Timestamp(cursor.getLong(cursor.getColumnIndex("timestamp"))));
                    feed.setTopic(cursor.getString(cursor.getColumnIndex("topic")));
                    feed.setContent(cursor.getString(cursor.getColumnIndex("content")));
                    feeds.add(feed);
                }while( cursor.moveToNext());
            }
        }catch(SQLException ex){
            Log.e(Constants.LOG_TAG, "Query Feed failed. condition=" + condition+", limit=" + limit + ", orderBy="+ orderBy + ".\n" + CommonUtils.getStack(ex));
        }finally {
            if(cursor != null)
                cursor.close();
        }
        return feeds;
    }

    @Override
    public long newAttachment(Attachment attach, InputStream in) {
        long current = System.currentTimeMillis();
        ContentValues vals = new ContentValues();
        attach.setOriginalName(current + "_" + attach.getOriginalName());
        File f;
        if(CommonUtils.getConfig(context,STORE_ATTACHMENT_EXT, "0" ).equals("1")){
            f = CommonUtils.saveFileToExternal(context,in,"attachments/" + attach.getOriginalName(),true,false,null);
        }else{
            f = CommonUtils.saveFileToInternal(context,in,"attachments/" + attach.getOriginalName(),true);
        }
        attach.setLink(f.getAbsolutePath());
        vals.put("feedId", attach.getFeedId());
        vals.put("desc", attach.getDesc());
        vals.put("format", attach.getFormat());
        vals.put("originalName", attach.getOriginalName());
        vals.put("Link", attach.getLink());
        checkConnection();
        return db.insert(ATTACHMENT_TABLE, null, vals);
    }


    @Override
    public List<Attachment> getAttachmentsByFeed(long feedId) {
        Cursor cursor = null;
        List<Attachment> atts = new ArrayList<Attachment>();
        Attachment att;
        try{
            checkConnection();
            cursor = db.query(ATTACHMENT_TABLE,null,"feedId=?",new String[]{String.valueOf(feedId)},null,null,null);
            if( cursor.getCount() >0 ){
                cursor.moveToFirst();
                do{
                    att = new Attachment();
                    att.setId(cursor.getLong(cursor.getColumnIndex("id")));
                    att.setFeedId(feedId);
                    att.setDesc(cursor.getString(cursor.getColumnIndex("desc")));
                    atts.add(att);
                }while( cursor.moveToNext() );
            }
        }finally{
            if( cursor != null )
                cursor.close();
        }
        return atts;
    }

    @Override
    public boolean delAttachment(long attachId) {
        checkConnection();
        return (db.delete(ATTACHMENT_TABLE,"id=?", new String[]{String.valueOf(attachId)}) > 0 );
    }

    @Override
    public Attachment getAttachment(long id) {
        Cursor cursor = null;
        Attachment att = null;
        try{
            checkConnection();
            cursor = db.query(ATTACHMENT_TABLE,null,"id=?",new String[]{String.valueOf(id)},null,null,null);
            if( cursor.getCount() >0 ){
                cursor.moveToFirst();
                att = new Attachment();
                att.setId(id);
                att.setFeedId(cursor.getLong(cursor.getColumnIndex("feedId")));
                att.setDesc(cursor.getString(cursor.getColumnIndex("desc")));
            }
        }finally{
            if( cursor != null )
                cursor.close();
        }
        return att;
    }

    @Override
    public InputStream getAttachmentContent(long id) {
        Cursor cursor = null;
        String link;
        try{
            checkConnection();
            cursor = db.query(ATTACHMENT_TABLE,new String[]{"link"},"id=?",new String[]{String.valueOf(id)},null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                link = cursor.getString(1);
                return getAttachmentContent(link);
            }
        }finally {
            CommonUtils.close(cursor);
        }
        return null;
    }

    public InputStream getAttachmentContent(String link){
        InputStream in = null;
        try{
            in = new FileInputStream(link);
        }catch(IOException ex){
            Log.e(Constants.LOG_TAG,"Get attachment Stream met exception.Link=" + link +".\n" + CommonUtils.getStack(ex) );
        }
        return in;
    }

}
