package webapp;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Memo {
    private String title;
    private String content;
    private String created;
    private String modified;
    private boolean deleted;
    private int version;
    private int threadid;
    private int id;

    public Memo(ResultSet set){
        try {
            DateFormat df = new SimpleDateFormat();

                title = set.getString("title");
                content = set.getString("content");
                created = set.getString("created");
                modified = set.getString("modified");
                deleted = set.getBoolean("deleted");
                version = set.getInt("version");
                threadid = set.getInt("threadid");
                id = set.getInt("id");

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }



    public boolean isDeleted() {
        return deleted;
    }

    public int getThreadid() {
        return threadid;
    }

    public int getId() {
        return id;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }

    public int getVersion() {
        return version;
    }

    public String toString(){
        DateFormat dt = new SimpleDateFormat("yyyy-mm-dd");
        return ("id: "+id+" version: "+version+" title: "+title+" content: "+content+" created: "+created+" modified: "+modified+" deleted: "+deleted+" threadid: "+threadid);
    }
}
