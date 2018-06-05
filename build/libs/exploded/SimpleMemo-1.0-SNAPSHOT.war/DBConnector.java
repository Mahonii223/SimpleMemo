import com.sun.istack.internal.NotNull;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBConnector {

    private final static String DBURL = "jdbc:mysql://www.db4free.net:3306/simplememos";
    private final static String DBUSER = "webapp";
    private final static String DBPASS = "webapppwd";
    private final static String DBDRIVER = "com.mysql.jdbc.Driver";


    private Connection connection;
    private PreparedStatement preparedStatement;

    private int updateWrapper(String update) throws SQLException{
        int result = 0;

        try {
            Class.forName(DBDRIVER).newInstance();
            connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            preparedStatement = connection.prepareStatement(update);
            result = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (InstantiationException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }

        return result;
    }


    private Memo queryWrapper(String query){
        Memo memo = null;
        try {
            Class.forName(DBDRIVER).newInstance();
            connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            preparedStatement = connection.prepareStatement(query);

            memo = new Memo(preparedStatement.executeQuery());

            preparedStatement.close();
            connection.close();
        }
        catch( Exception e){
            e.printStackTrace();
        }
        finally {
            return memo;
        }
    }

    public int save(@NotNull String title, @NotNull String content, @NotNull Date date) throws SQLException{
        StringBuilder builder = new StringBuilder();

        String dateString = dateFormat(date);

        //Check for lowest available thread id
        int thread = getThreadAvailable();

        builder.append("INSERT INTO memos(title, content, version, modified, created, threadid)");
        builder.append("VALUES( '"+title+"', '"+content+"', 1, '"+dateString+"', '"+dateString+"', '"+thread+"')");

        return updateWrapper(builder.toString());
    }

    private int getThreadAvailable() throws SQLException{
        try {
            Class.forName(DBDRIVER).newInstance();
            connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            preparedStatement = connection.prepareStatement("select max(threadid) from memos");

            int thread = 0;

            ResultSet result = preparedStatement.executeQuery();

            if (result.next())
                thread = result.getInt("max(threadid)");

            preparedStatement.close();
            connection.close();

            return thread + 1;
        } catch(Exception e){
            e.printStackTrace();
        }
        return 1;
    }

    private int getVersionAvailable(int threadid) throws SQLException{
        try {
            Class.forName(DBDRIVER).newInstance();
            connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            preparedStatement = connection.prepareStatement("select max(version) from memos where threadid =" + threadid);

            ResultSet result = preparedStatement.executeQuery();
            int version = 0;
            if (result.next()) {
                version = result.getInt("max(version)");
            }
            preparedStatement.close();
            connection.close();
            return version + 1;
        } catch(Exception e){
            e.printStackTrace();
        }
        return 1;
    }

    private String getThreadCreation(int threadid) throws SQLException{
        try {
            Class.forName(DBDRIVER).newInstance();
            connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            preparedStatement = connection.prepareStatement("select distinct created from memos where threadid = " + threadid);
            ResultSet result = preparedStatement.executeQuery();
            String date = null;
            DateFormat dt = new SimpleDateFormat();
            if (result.next()) {
                date = result.getString("created");
            }

            preparedStatement.close();
            connection.close();
            return date;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Memo> listMemos(){
        try {
            Class.forName(DBDRIVER).newInstance();
            connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            preparedStatement = connection.prepareStatement("select a.* from memos as a " +
                    "left outer join memos as b " +
                    "on a.deleted = 0 and b.deleted = 0 " +
                    "and a.threadid = b.threadid " +
                    "and a.version < b.version " +
                    "where a.deleted = 0 and b.id is null");

            ResultSet result = preparedStatement.executeQuery();

            List<Memo> list = new ArrayList<>();
            while (result.next()) {
                Memo memo = new Memo(result);
                list.add(memo);
            }

            connection.close();
            preparedStatement.close();

            return list;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Memo getMemo(int id){
        try {
            Class.forName(DBDRIVER).newInstance();
            connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            preparedStatement = connection.prepareStatement("select * from memos where id = "+id);

            ResultSet result = preparedStatement.executeQuery();
            if(result.next())
                return new Memo(result);

        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Memo> getMemoLog(int id){
        try{
        Class.forName(DBDRIVER).newInstance();
        connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
        preparedStatement = connection.prepareStatement("select * from memos where threadid = ( " +
                "select threadid from memos where id = "+id+" ) " +
                "order by version");

        List<Memo> list = new ArrayList<>();
        ResultSet result = preparedStatement.executeQuery();

        while(result.next()){
            Memo memo = new Memo(result);
            list.add(memo);
        }

        preparedStatement.close();
        connection.close();

        return list;}
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int update(@NotNull String title, @NotNull String content, @NotNull int threadid, @NotNull Date date) throws SQLException{

        int availableVersion = getVersionAvailable(threadid);
        String creationDate = getThreadCreation(threadid);
        String modifiedDate = dateFormat(date);

        String update = "insert into memos(title, content, version, modified, created, threadid) " +
                "values( '"+title+"', '"+content+"', "+availableVersion+", '"+modifiedDate+"', '"+creationDate+"', "+threadid+")";

        return updateWrapper(update);
    }

    public int remove(int id) throws SQLException{
        String update = "update memos set deleted = 1 where id = "+id;
        return updateWrapper(update);
    }

    private String dateFormat(@NotNull Date date){

        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-mm-dd");
        return dt1.format(date);
    }

}
