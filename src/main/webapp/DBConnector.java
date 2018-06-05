package webapp;

import com.sun.istack.internal.NotNull;

import java.sql.*;

import java.util.ArrayList;

import java.util.List;

public class DBConnector {

    private String DBURL;
    private String DBUSER;
    private String DBPASS;
    private String DBDRIVER;

    public DBConnector(String DBURL, String DBUSER, String DBPASS, String DBDRIVER){
        this.DBURL = DBURL;
        this.DBUSER = DBUSER;
        this.DBPASS = DBPASS;
        this.DBDRIVER = DBDRIVER;
    }

    public DBConnector(){
        this.DBURL = "jdbc:mysql://www.db4free.net:3306/simplememos";
        this.DBUSER = "webapp";
        this.DBPASS = "webapppwd";
        this.DBDRIVER = "com.mysql.jdbc.Driver";
    }

    //Connection and prepared statement
    private Connection connection;
    private PreparedStatement preparedStatement;


    //As opposed to queries, updates can be wrapped in a separate method, as they do not require operation on result set
    //while connection is still open
    private int updateWrapper(String update) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        int result = 0;

            Class.forName(DBDRIVER).newInstance();
            connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            preparedStatement = connection.prepareStatement(update);
            result = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

        return result;
    }


    private Memo queryWrapper(String query) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        Memo memo = null;

        Class.forName(DBDRIVER).newInstance();
        connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
        preparedStatement = connection.prepareStatement(query);
        memo = new Memo(preparedStatement.executeQuery());
        preparedStatement.close();
        connection.close();

        return memo;
    }

    public int save(@NotNull String title, @NotNull String content, @NotNull String date) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        StringBuilder builder = new StringBuilder();

        //Check for lowest available thread id
        int thread = getThreadAvailable();

        builder.append("INSERT INTO memos(title, content, version, modified, created, threadid)");
        builder.append("VALUES( '"+title+"', '"+content+"', 1, '"+date+"', '"+date+"', '"+thread+"')");

        return updateWrapper(builder.toString());
    }

    private int getThreadAvailable() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{

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

    }

    private int getVersionAvailable(int threadid) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{

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

    }

    private String getThreadCreation(int threadid) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{

            Class.forName(DBDRIVER).newInstance();
            connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            preparedStatement = connection.prepareStatement("select distinct created from memos where threadid = " + threadid);
            ResultSet result = preparedStatement.executeQuery();
            String date = null;

            if (result.next()) {
                date = result.getString("created");
            }

            preparedStatement.close();
            connection.close();
            return date;

    }

    public List<Memo> listMemos() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{

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

    }

    public Memo getMemo(int id) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{

        Class.forName(DBDRIVER).newInstance();
        connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
        preparedStatement = connection.prepareStatement("select * from memos where deleted = 0 and id = "+id);

        ResultSet result = preparedStatement.executeQuery();
        if(result.next())
            return new Memo(result);
        return null;
    }

    public List<Memo> getMemoLog(int id) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{

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

        return list;
    }

    public int update(@NotNull String title, @NotNull String content, @NotNull int threadid, @NotNull String modifiedDate) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{

        int availableVersion = getVersionAvailable(threadid);
        String creationDate = getThreadCreation(threadid);

        String update = "insert into memos(title, content, version, modified, created, threadid) " +
                "values( '"+title+"', '"+content+"', "+availableVersion+", '"+modifiedDate+"', '"+creationDate+"', "+threadid+")";

        return updateWrapper(update);
    }

    public int remove(int id) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        String update = "update memos set deleted = 1 where id = "+id;
        return updateWrapper(update);
    }


}
