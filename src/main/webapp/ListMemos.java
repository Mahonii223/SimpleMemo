package webapp;

import com.sun.istack.internal.NotNull;

import javax.json.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Path("/memos")
public class ListMemos {

    webapp.DBConnector dbc;

    public ListMemos(webapp.DBConnector dbc){
        this.dbc = dbc;
    }

    public ListMemos(){
        dbc = new webapp.DBConnector();
    }


    //Handling get request to list newest versions of all memos
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMemos() {
        try {

            JsonArray array = toJsonArray(dbc.listMemos());
            return Response.ok(array).build();

        } catch (Exception e){
            return Response.status(500).build();
        }
    }

    //Handling get request to list all changes of a single memo thread
    @GET
    @Path("/versions/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMemoVersions(@PathParam("id") int id){
        try {

            JsonArray array = toJsonArray(dbc.getMemoLog(id));

            return Response.ok(array).build();
        } catch (Exception e){
            return Response.status(400).build();
        }
    }

    //Handling get request to display a single, specific  memo
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMemo(@PathParam("id") int id){
        try {
            Memo memo = dbc.getMemo(id);
            JsonObject json = Json.createObjectBuilder()
                    .add("id", memo.getId())
                    .add("version", memo.getVersion())
                    .add("title", memo.getTitle())
                    .add("content", memo.getContent())
                    .add("created", memo.getCreated())
                    .add("modified", memo.getModified())
                    .add("threadid", memo.getThreadid())
                    .build();
            return Response.ok(json).build();
        } catch (Exception e){}
        return Response.status(500).build();
    }

    //Handling post request to create first memo of a new thread
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMemo(JsonObject memo){
        try{
            dbc.save(memo.getString("title"), memo.getString("content"), dateFormat(new Date()));
            return Response.status(200).build();
        } catch( Exception e) {
            return Response.status(400).build();
        }
    }

    //Handling a put request to add a new memo to an existing thread
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editMemo(JsonObject memo){
        try{
            dbc.update(memo.getString("title"),
                    memo.getString("content"),
                    memo.getInt("threadid"),
                    dateFormat(new Date()));

            return Response.status(200).build();
        } catch (Exception e) {
            return Response.status(400).build();
        }
    }

    //Handling delete request to mark a single, specific memo as deleted
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id){
        try {
            dbc.remove(id);

            return Response.status(200).build();
        } catch (Exception e){
            return Response.status(400).build();
        }
    }

    //Simple method to convert Date variable into sql-readable format
    private String dateFormat(@NotNull Date date){
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-mm-dd");
        return dt1.format(date);
    }


    //Helper method to transform List of memos into JsonArray to avoid repeating code
    private JsonArray toJsonArray(List<Memo> list){
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonArrayBuilder memos = factory.createArrayBuilder();

        for(Memo m : list){
            memos.add(factory.createObjectBuilder()
                    .add("id", m.getId())
                    .add("title", m.getTitle())
                    .add("content", m.getContent())
                    .add("version", m.getVersion())
                    .add("created", m.getCreated())
                    .add("modified", m.getModified())
                    .add("threadid", m.getThreadid()));
        }

        return memos.build();
    }
}


