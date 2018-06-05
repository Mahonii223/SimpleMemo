package UnitTests;

import com.sun.istack.internal.NotNull;
import org.junit.*;
import org.mockito.Mockito;
import webapp.DBConnector;
import webapp.ListMemos;

import javax.json.Json;
import javax.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.TestCase.fail;

public class ListMemosTest {

    @Test
    public void getListTest(){

        DBConnector dbc = Mockito.mock(DBConnector.class);
        ListMemos list = new ListMemos(dbc);

        try{
            list.getMemos();
            Mockito.verify(dbc).listMemos();
        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    public void getMemoTest(){

        int id = 2;

        DBConnector dbc = Mockito.mock(DBConnector.class);
        ListMemos list = new ListMemos(dbc);

        try{
            list.getMemo(id);
            Mockito.verify(dbc).getMemo(id);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getMemoLogTest(){
        DBConnector dbc = Mockito.mock(DBConnector.class);
        ListMemos list = new ListMemos(dbc);

        try{
            list.getMemoVersions(2);
            Mockito.verify(dbc).getMemoLog(2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void addMemoTest(){
        String title = "title";
        String content = "content";

        JsonObject json = Json.createObjectBuilder()
                .add("title", title)
                .add("content", content)
                .build();

        DBConnector dbc = Mockito.mock(DBConnector.class);
        ListMemos list = new ListMemos(dbc);

        try{
            list.addMemo(json);
            Mockito.verify(dbc).save(title, content, dateFormat(new Date()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void editMemoTest(){

        String title = "title";
        String content = "content";
        int threadid = 4;

        JsonObject json = Json.createObjectBuilder()
                .add("title", title)
                .add("content", content)
                .add("threadid", threadid)
                .build();

        DBConnector dbc = Mockito.mock(DBConnector.class);
        ListMemos list = new ListMemos(dbc);

        try{
            list.editMemo(json);
            Mockito.verify(dbc).update(title, content, threadid, dateFormat(new Date()));
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void deleteTest(){
        int id = 2;

        DBConnector dbc = Mockito.mock(DBConnector.class);
        ListMemos list = new ListMemos(dbc);

        try{
            list.delete(id);
            Mockito.verify(dbc).remove(id);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    //Simple method to convert Date variable into sql-readable format
    private String dateFormat(@NotNull Date date){
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-mm-dd");
        return dt1.format(date);
    }
}
