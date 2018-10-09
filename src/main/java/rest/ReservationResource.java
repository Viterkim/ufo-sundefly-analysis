
package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.FlightInstance;
import entity.Passenger;
import entity.Reservation;
import facades.FlightsFacade;
import facades.ReservationFacade;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import rest.serialize.ObjSerialize;


@Path("reservation")
public class ReservationResource {

    @Context
    private UriInfo context;
    private ObjSerialize objSer;
    private final FlightsFacade ff = new FlightsFacade();
    private final ReservationFacade rf = new ReservationFacade();

    public ReservationResource() {
        objSer = new ObjSerialize();
    }
    
    
    @POST
    @Path("{flightId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String makeReservation(@PathParam("flightId") int paramFlightId, String content) throws JsonProcessingException {
        //Får imod input, som vi laver om til objekter, og gemmer
        JsonParser parser = new JsonParser();
        JsonObject jo = (JsonObject) parser.parse(content);
        
        //Could use the param
        int flightId = jo.get("flightID").getAsInt();
        
        Reservation res = new Reservation();
        FlightInstance fi = ff.getFlightInstanceFromId(flightId);
        res.setFkflightInstanceId(fi);
        int numSeats = jo.get("numberOfSeats").getAsInt();
        
        //Checks if there are enough seats
        if(numSeats > fi.getAvailableSeats()){
            //Fix exception to throw something more appropriate
            throw new NotFoundException("Not enough seats available!");
        }
        
        fi.setAvailableSeats(fi.getAvailableSeats() - numSeats);
        int totalPrice = fi.getPrice() * numSeats;
        res.setTotalPrice(totalPrice);
        
        res.setReservName(jo.get("reserveName").getAsString());
        res.setReservPhone(jo.get("reservePhone").getAsString());
        res.setReservEmail(jo.get("reserveEmail").getAsString());
        
        //Saves
        ff.mergeFlightInstance(fi);
        rf.persistReservation(res);
        
        //Passenger input etc
        JsonArray jsonPass = jo.getAsJsonArray("passengers");
        ArrayList<Passenger> javaPass = new ArrayList<>();
        
        for(int i = 0; i < jsonPass.size(); i++){
            JsonObject temp = (JsonObject) parser.parse(jsonPass.get(i).toString());
            Passenger tempPas = new Passenger();
            String firstName = temp.get("firstName").getAsString();
            tempPas.setFirstName(firstName);
            String lastName = temp.get("lastName").getAsString();
            tempPas.setLastName(lastName);
            tempPas.setFkreservationId(res);
            
            //Store temp passenger
            rf.persistPassenger(tempPas);
            //For the response
            javaPass.add(new Passenger(firstName, lastName));
        }
        
        return objSer.serializeReservationResponse(fi, res, javaPass, numSeats).toString();
    }
}
