/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import entity.Airport;
import entity.Flight;
import entity.FlightInstance;
import entity.Passenger;
import entity.Reservation;
import facades.FlightsFacade;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Date;
import java.util.List;

/**
 *
 * @author William Pfaffe
 */
public class ObjSerialize {

    /*
    Ikke for børn under 15 år
    Support for flere airlines
     */
//    public HashMap<String, LinkedList<FlightInstance>> getMapFromFlights(List<FlightInstance> t){
//        HashMap<String, LinkedList<FlightInstance>>airLineNames = new HashMap();
//    
//        for(FlightInstance fi : t){
//            String airlineName = fi.getFkflightNumber().getFkAirlineId().getName();
//            
//            if(airLineNames.get(airlineName) == null){
//                airLineNames.put(airlineName, new LinkedList<FlightInstance>());
//            }
//            
//            LinkedList<FlightInstance> tempList = airLineNames.get(airlineName);
//            tempList.add(fi);
//            airLineNames.put(airlineName, tempList);
//        }
//        
//        return airLineNames;
//    }
    public JsonElement serializeFlightDetailView(FlightInstance fi, List<Reservation> reList) {
        JsonObject obj = new JsonObject();
        JsonObject obj2 = new JsonObject();
        JsonArray arr = new JsonArray();
        
        obj.addProperty("flightInstanceId", fi.getFlightInstanceId());
        obj.addProperty("departureDate", fi.getDepartureDate());
        obj.addProperty("departureTime", fi.getDepartureTime());
        obj.addProperty("travelTime", fi.getTravelTime());
        obj.addProperty("availableSeats", fi.getAvailableSeats());
        obj.addProperty("price", fi.getPrice());
        obj.addProperty("arrivalTime", fi.getArrivalTime());
        obj.addProperty("arrivalDate", fi.getArrivalDate());
        
        obj2.addProperty("flightNumber", fi.getFkflightNumber().getFlightNumber());
        obj2.addProperty("seats", fi.getFkflightNumber().getSeats());
        obj2.addProperty("currentAirport", fi.getFkflightNumber().getFkcurrentAirport().getIATACode());
        obj.add("flight", obj2);
        
        for(int i = 0; i<reList.size(); i++){
            Reservation r = reList.get(i);
            JsonArray passArr = new JsonArray();
            JsonObject obj3 = new JsonObject();
            obj3.addProperty("reservationID", r.getReservationId());
            obj3.addProperty("totalPrice", r.getTotalPrice());
            obj3.addProperty("reservEmail", r.getReservEmail());
            obj3.addProperty("reservPhone", r.getReservPhone());
            obj3.addProperty("reservName", r.getReservName());
            arr.add(obj3);
            for(int k = 0; k<r.getPassengerList().size(); k++){
                Passenger pass = r.getPassengerList().get(k);
                JsonObject obj4 = new JsonObject();
                obj4.addProperty("passengerId", pass.getPassengerId());
                obj4.addProperty("firstName", pass.getFirstName());
                obj4.addProperty("lastName", pass.getLastName());
                passArr.add(obj4);
            }
            
            obj3.add("passengers", passArr);
            
        }
        
        obj.add("reservations", arr);
        return obj;

    }

    public JsonElement serializeReservationResponse(FlightInstance fi, Reservation res, List<Passenger> pas, int numberOfSeats) {
        JsonObject obj = new JsonObject();
        JsonArray passArr = new JsonArray();
        Flight flight = fi.getFkflightNumber();

        obj.addProperty("flightNumber", flight.getFlightNumber());
        Airport start = fi.getFkstartingAirport();
        obj.addProperty("origin", (start.getName() + "(" + start.getIATACode() + ")"));
        Airport dest = fi.getFkdestinationAirport();
        obj.addProperty("destination", (dest.getName() + "(" + dest.getIATACode() + ")") );
        obj.addProperty("date", fi.getDepartureDate() + "T" + fi.getDepartureTime() + ":00.000Z");
        obj.addProperty("flightTime", fi.getTravelTime());
        obj.addProperty("numberOfSeats", numberOfSeats);
        obj.addProperty("reserveeName", res.getReservName());

        for (Passenger p : pas) {
            JsonObject jsP = new JsonObject();
            jsP.addProperty("firstName", p.getFirstName());
            jsP.addProperty("lastName", p.getLastName());
            passArr.add(jsP);
        }

        obj.add("passengers", passArr);
        return obj;
    }

    public JsonElement serializeListOfFlights(List<FlightInstance> t, int tickets) {
        JsonObject obj = new JsonObject();
        JsonArray flightsArr = new JsonArray();
        obj.addProperty("airline", "Sunde Fly");

        for (FlightInstance fi : t) {
            flightsArr.add(serializeFlightInstance(fi, tickets));
        }

        obj.add("flights", flightsArr);

        return obj;
    }

    public JsonObject serializeFlightInstanceAdmin(FlightInstance fi) {
        JsonObject j = new JsonObject();
        j.addProperty("flightID", fi.getFkflightNumber().getFlightNumber());
        j.addProperty("flightNumber", fi.getFlightInstanceId());
        j.addProperty("date", fi.getDepartureDate());
        j.addProperty("availableSeats", fi.getAvailableSeats());
        j.addProperty("seatPrice", fi.getPrice());
        j.addProperty("traveltime", fi.getTravelTime());
        j.addProperty("origin", fi.getFkstartingAirport().getIATACode());
        j.addProperty("destination", fi.getFkdestinationAirport().getIATACode());
        return j;
    }

    public JsonObject serializeFlightInstance(FlightInstance fi, int tickets) {
        JsonObject j = new JsonObject();
        j.addProperty("flightID", fi.getFkflightNumber().getFlightNumber());
        j.addProperty("flightNumber", fi.getFlightInstanceId());
        j.addProperty("date", fi.getDepartureDate() + "T" + fi.getDepartureTime() + ":00.000Z");
        j.addProperty("numberOfSeats", tickets);
        j.addProperty("totalPrice", fi.getPrice() * tickets);
        j.addProperty("traveltime", fi.getTravelTime());
        j.addProperty("origin", fi.getFkstartingAirport().getIATACode());
        j.addProperty("destination", fi.getFkdestinationAirport().getIATACode());
        return j;
    }

//    public JsonElement serializeReservationResponse(){
//        
//    }
    public JsonElement serializeToCompleteFlightList(List<FlightInstance> flightInstanceList) {
        JsonObject json = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (FlightInstance flightInstance : flightInstanceList) {
            if (json.get("airline") == null) {
                json.addProperty("airline", flightInstance.getFkflightNumber().getFkAirlineId().getName());
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("flightID", flightInstance.getFkflightNumber().getFlightNumber());
            jsonObject.addProperty("flightNumber", flightInstance.getFlightInstanceId());
            jsonObject.addProperty("date", flightInstance.getDepartureDate());
            jsonObject.addProperty("availableSeats", flightInstance.getAvailableSeats());
            jsonObject.addProperty("totalSeats", flightInstance.getFkflightNumber().getSeats());
            jsonObject.addProperty("traveltime", flightInstance.getTravelTime());
            jsonObject.addProperty("origin", flightInstance.getFkstartingAirport().getIATACode());
            jsonObject.addProperty("destination", flightInstance.getFkdestinationAirport().getIATACode());
            jsonObject.addProperty("pricePerSeat", flightInstance.getPrice());
            int totalEarnings = flightInstance.getPrice() * (flightInstance.getFkflightNumber().getSeats() - flightInstance.getAvailableSeats());
            jsonObject.addProperty("totalEarnings", totalEarnings);
            jsonArray.add(jsonObject);
        }
        json.add("flights", jsonArray);
        return json;
    }

    public JsonArray serializePlanes(List<Flight> flights) {
        JsonArray jsonArray = new JsonArray();
        //System.out.println("Flight Amount: " + flights.size());
        for (Flight f : flights) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("planeID", f.getFlightNumber());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    public JsonObject serializeAirports(List<Airport> airports) {
        JsonObject jsonObject = new JsonObject();
        for (Airport a : airports) {
            jsonObject.addProperty(a.getIATACode() + " - " + a.getName() + " - " + a.getCountry(), "null");
        }
        return jsonObject;
    }

    public FlightInstance createFlightInstanceFromJson(JsonObject obj, FlightsFacade facade) throws ParseException {
        FlightInstance instance = new FlightInstance();
        //JSON Things:

        /*
            planeID: this.state.planeID,    int;    49
            startAirport: start,            String; CPH - Copenhagen Airport - Denmark
            destAirport: end,               String; TXL - Berlin Tegel Airport - Germany
            travelDate: dateVal,            String; 20. Maj, 2017
            arriveDate: endDate,            String; 20. Maj, 2017
            startTime: startTime,           String; 04:30
            endTime: endTime,               String; 07:30
            seatPrice: seatPrice            int;    1000
         */
        int planeID = obj.get("planeID").getAsInt();
        String startAirportCode = obj.get("startAirport").getAsString().substring(0, 3);
        String destAirportCode = obj.get("destAirport").getAsString().substring(0, 3);
        String travelDate = obj.get("travelDate").getAsString();
        String arriveDate = obj.get("arriveDate").getAsString();
        String startTime = obj.get("startTime").getAsString();
        String endTime = obj.get("endTime").getAsString();
        int seatPrice = obj.get("seatPrice").getAsInt();

        //Get Plane from ID
        Flight f = facade.getFlightFromID(planeID);
        Airport startAirport = facade.getAirport(startAirportCode);
        Airport destAirport = facade.getAirport(destAirportCode);
        String formattedDate = getFormatDate(travelDate);
        String formattedDate2 = getFormatDate(arriveDate);

        instance.setAvailableSeats(f.getSeats());
        instance.setDepartureDate(formattedDate);
        instance.setArrivalDate(formattedDate2);
        instance.setFkstartingAirport(startAirport);
        instance.setFkdestinationAirport(destAirport);
        instance.setFkflightNumber(f);
        instance.setPrice(seatPrice);
        instance.setDepartureTime(startTime);
        instance.setArrivalTime(endTime);

        String startDateTime = formattedDate + "T" + startTime + "Z";
        String endDateTime = formattedDate2 + "T" + endTime + "Z";

        String dateFormat = "yyyy-MM-dd'T'HH:mm'Z'";

        DateFormat df = new SimpleDateFormat(dateFormat);

        Date startDate = df.parse(startDateTime);

        Date endDate = df.parse(endDateTime);

        long milli = Math.abs(endDate.getTime() - startDate.getTime());
        long minutes = (milli / 1000) / 60;

        instance.setTravelTime("" + minutes);

        return instance;
    }

    public String getFormatDate(String date) {
        String[] arr = date.split(" ");
        System.out.println("Date to Format: " + date + ", splitted: " + customArrayToString(arr));
        String day = arr[0];
        if (day.length() == 1) {
            day = "0" + day;
        }
        String year = arr[2];
        String monthName = arr[1].replaceAll(",", "");
        String monthNumber = "" + Month.valueOf(monthName.toUpperCase()).getValue();
        if (monthNumber.length() == 1) {
            monthNumber = "0" + monthNumber;
        }
        return year + "-" + monthNumber + "-" + day;
    }

    private String customArrayToString(String[] str) {
        String result = "";
        for (String s : str) {
            result += "[" + s + "], ";
        }
        return result;
    }

}
