

import javax.json.Json;
import javax.json.JsonObject;

import javax.json.JsonString;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/listmemos")
public class ListMemos {

    DBConnector dbc = new DBConnector();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMemos() {
        return Response.ok("returned").build();
    }

    @GET
    @Path("versions/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMemoVersions(@PathParam("title") String title){
        return null;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMemo(@PathParam("id") int id){
        System.out.println(id);
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void addMemo(JsonObject json){

    }
}


