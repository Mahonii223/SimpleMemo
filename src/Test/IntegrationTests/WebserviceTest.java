package IntegrationTests;


import ch.vorburger.mariadb4j.DB;
import org.junit.Test;
import webapp.DBConnector;
import webapp.ListMemos;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

//This test will utilize in-memory database to check behavior of the whole system
public class WebserviceTest {

    DB db;
    DBConnector dbc;

    String DBURL;
    String DBUSER;
    String DBPASS;
    String DBDRIVER;

    public WebserviceTest(){
        try{
            DBURL = "jdbc:mysql://localhost/memos";
            DBUSER = "webapp";
            DBPASS = "webapppwd";
            DBDRIVER = "com.mysql.jdbc.Driver";

            db = DB.newEmbeddedDB(3306);
            db.createDB("memos", DBUSER, DBPASS);
            db.start();
            db.source("setup.sql");

            dbc = new DBConnector(DBURL, DBUSER, DBPASS, DBDRIVER);
        } catch (Exception e) {
            throw new RuntimeException("Could not create local database");
        }
    }

    @Test
    public void addMemoTest(){
        try {
            ListMemos list = new ListMemos(dbc);

            String title = "title1";
            String content = "content1";

            JsonObject json = Json.createObjectBuilder()
                    .add("title", title)
                    .add("content", content)
                    .build();

            Response response = list.addMemo(json);

            assertEquals(200, response.getStatus());

            Class.forName(DBDRIVER).newInstance();
            Connection connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            PreparedStatement preparedStatement = connection.prepareStatement("select * from memos where id = SCOPE_IDENTITY()");
            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                assertEquals(title, result.getString("title"));
                assertEquals(content, result.getString("content"));
            } else {
                fail("No record was inserted");
            }

            preparedStatement.close();
            connection.close();
        } catch ( Exception e) {
            fail(e.getMessage());
        }

    }


}
