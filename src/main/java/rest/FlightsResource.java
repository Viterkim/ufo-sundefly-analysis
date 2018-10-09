
package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import facades.FlightsFacade;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.net.ssl.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Airport;
import entity.Flight;
import entity.FlightInstance;
import entity.Reservation;

import java.text.ParseException;

import rest.serialize.ObjSerialize;
import security.TrustAllX509TrustManager;
import service.FlightsService;


@Path("flights")
public class FlightsResource {

    @Context
    private UriInfo context;
    private ObjectMapper obm;
    private final FlightsFacade ff = new FlightsFacade();
    private ObjSerialize objSer;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();;
    private FlightsService flightsService = FlightsService.getSingleton();

    public FlightsResource() {
        this.obm = new ObjectMapper();
        this.objSer = new ObjSerialize();
    }


    @GET
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFlightsForAdmin(@QueryParam("page_count") int pageCount, @QueryParam("page_number") int pageNumber) {
        List<FlightInstance> allFlightsList = ff.getEverything(pageCount, pageNumber);
        JsonElement json = this.objSer.serializeToCompleteFlightList(allFlightsList);
        
        return Response
                .status(Response.Status.OK)
                .entity(json.toString())
                .build();
    }

    @GET
    @Path("all")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFlights() throws JsonProcessingException {
        List<FlightInstance> fiList = ff.getEverything();
        return Response
                .status(Response.Status.OK)
                .entity(obm.writerWithDefaultPrettyPrinter().writeValueAsString(fiList))
                .build();
    }
    
    @GET
    @Path("viewDetails/{id}")
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getViewDetails(@PathParam("id") int id) throws JsonProcessingException{
        FlightInstance arr = ff.getFlightInstanceFromId(id);
        List<Reservation> resList = ff.getReservationsFromFlightInstanceID(id);
        JsonElement elm = objSer.serializeFlightDetailView(arr, resList);
        return Response
                .status(Response.Status.OK)
                .entity(elm.toString())
                .build();
    }
    
    @DELETE
    @Path("/remove/{id}")
    @RolesAllowed("Admin")
    public Response deleteFlightInstance(@PathParam("id") int id){
        boolean result = ff.removeFlightInstance(id);
        
        String resultString = (result ? "{\"response\": \"OK\"}" : "{\"response\": \"ERROR\"}");
        
        return Response
                .status(Response.Status.OK)
                .entity(resultString)
                .build();
    }
    
    @GET
    @Path("planes") 
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPlanes() throws JsonProcessingException {
        List<Flight> fiList = ff.getAllFlights();
        JsonArray arr = objSer.serializePlanes(fiList);
        
        return Response
                .status(Response.Status.OK)
                .entity(arr.toString())
                .build();
    }
    
    @POST
    @Path("addFlight") 
    @RolesAllowed("Admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFlightInstance(String content) throws JsonProcessingException {
        System.out.println("addFLightInstance REST API Call!");
        JsonObject obj = getGson().fromJson(content, JsonObject.class);
        System.out.println(obj.toString());
        FlightInstance flight = null;
        try {
            flight = objSer.createFlightInstanceFromJson(obj, ff);
        } catch (ParseException e) {
            return Response
                .status(Response.Status.CONFLICT)
                .entity(e.toString())
                .build();
        }
        System.out.println(flight.toString());
        ff.addFlightInstance(flight);
        return Response
                .status(Response.Status.OK)
                .entity(obj.toString())
                .build();
    }

    @GET
    @Path("airports") 
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAirports(@QueryParam("search") String search, @QueryParam("limit") int limit) throws JsonProcessingException {
        List<Airport> airports = new ArrayList<Airport>();
        if (search == null || search == "" || search.isEmpty()) {
           airports = ff.getAirports(limit);
        } else {
            airports = ff.getAirports(search, limit);
        }
        JsonObject obj = objSer.serializeAirports(airports);
        
        return Response
                .status(Response.Status.OK)
                .entity(obj.toString())
                .build();
    }

    @GET
    @Path("{from}/{date}/{tickets}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlights(@PathParam("from") String from, @PathParam("date") String date, @PathParam("tickets") int tickets, @HeaderParam("user-agent") String userAgent) {
        System.out.println("...Getting travels from: " + from);
        System.out.println("...Getting travels on full date: " + date);
        System.out.println("...Getting travels on date: " + date.substring(0,10));
        System.out.println("...Getting travels with tickets: " + tickets);
        List<FlightInstance> tempList = ff.getFlightsFromDateTickets(from, date.substring(0,10), tickets);
        for (FlightInstance flightInstance : tempList
                ) {
            System.out.println("..." + flightInstance.toString());
        }
        JsonElement ourFLights = objSer.serializeListOfFlights(tempList, tickets);
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(ourFLights);
        System.out.println("...Tried to see the useragent: " + userAgent);
        boolean isFromOtherServer = true;
        if (userAgent.contains("Chrome") || userAgent.contains("Mozilla") || userAgent.contains("AppleWebKit")) {
            isFromOtherServer = false;
            getAllJsonFromOtherAirlines(jsonArray,from,date,tickets);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("...Tried to sleep, but couldn't do it, while waiting for other airlines.");
                e.printStackTrace();
            }
        }
        String returnString = "";
        if (isFromOtherServer) {
            returnString = jsonArray.get(0).toString();
        } else {
            returnString = jsonArray.toString();
        }
        return Response
                .status(Response.Status.OK)
                .entity(returnString)
                .build();
    }

    @GET
    @Path("{from}/{to}/{date}/{tickets}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlightsTo(@PathParam("from") String from, @PathParam("to") String to, @PathParam("date") String date, @PathParam("tickets") int tickets, @HeaderParam("user-agent") String userAgent) throws JsonProcessingException {
        System.out.println("...Getting travels from: " + from);
        System.out.println("...Getting travels to: " + to);
        System.out.println("...Getting travels on full date: " + date);
        System.out.println("...Getting travels on date: " + date.substring(0,10));
        System.out.println("...Getting travels with tickets: " + tickets);
        List<FlightInstance> tempList = ff.getFlightsFromToDateTickets(from, to, date.substring(0,10), tickets);
        for (FlightInstance flightInstance : tempList
             ) {
            System.out.println("..." + flightInstance.toString());
        }
        JsonElement ourFLights = objSer.serializeListOfFlights(tempList, tickets);
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(ourFLights);
        System.out.println("...Tried to see the useragent: " + userAgent);
        boolean isFromOtherServer = true;
        if (userAgent.contains("Chrome") || userAgent.contains("Mozilla") || userAgent.contains("AppleWebKit")) {
            isFromOtherServer = false;
            getAllJsonFromOtherAirlines(jsonArray,from,to,date,tickets);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("...Tried to sleep, but couldn't do it, while waiting for other airlines.");
                e.printStackTrace();
            }
            System.out.println("...Continued after waiting for other airlines to server us with flights.");
            flightsService.killAllAirlineFetches();
        }
        String returnString = "";
        if (isFromOtherServer) {
            returnString = jsonArray.get(0).toString();
            System.out.println("...Return String, in isFromOtherServer" + returnString);
        } else {
            returnString = jsonArray.toString();
            System.out.println("...Return String, in isFromOurApp" + returnString);
        }
        return Response
                .status(Response.Status.OK)
                .entity(returnString)
                .build();
    }

    private void getAllJsonFromOtherAirlines(JsonArray jsonArray, String from, String to, String date, int tickets) {
        setHttpsURLConnectionToNoCertNeed();
        flightsService.setAndStartAirlineFetch(jsonArray,"https://airline.skaarup.io/api/flights",from,to,date,tickets);
        flightsService.setAndStartAirlineFetch(jsonArray,"https://vetterlain.dk/AirWonDo/api/flights",from,to,date,tickets);
        flightsService.setAndStartAirlineFetch(jsonArray,"https://46.101.255.231.xip.io/airline/api/flights",from,to,date,tickets);
    }

    private void getAllJsonFromOtherAirlines(JsonArray jsonArray, String from, String date, int tickets) {
        setHttpsURLConnectionToNoCertNeed();
        flightsService.setAndStartAirlineFetch(jsonArray,"https://airline.skaarup.io/api/flights",from,null,date,tickets);
        flightsService.setAndStartAirlineFetch(jsonArray,"https://vetterlain.dk/AirWonDo/api/flights",from,null,date,tickets);
        flightsService.setAndStartAirlineFetch(jsonArray,"https://46.101.255.231.xip.io/airline/api/flights",from,null,date,tickets);
    }

    private void setHttpsURLConnectionToNoCertNeed() {
        // / If you want to see certificate options in console, start tomcat with these arguments: -Djavax.net.debug=all -Djavax.net.ssl.trustStore=trustStore
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("...Tried to get SSL Context instance TLS");
            e.printStackTrace();
        }
        try {
            sc.init(null, new TrustManager[]{new TrustAllX509TrustManager()}, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            System.out.println("...Tried to startup Trust Manager");
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String string, SSLSession ssls) {
                return true;
            }
        });
    }

    private Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().setPrettyPrinting().create();
        }
        return gson;
    }

}