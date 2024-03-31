package BsmchFlights.Services;

import BsmchFlights.Models.Airport;
import BsmchFlights.Models.ConnectionFlight;
import BsmchFlights.Models.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class ConnectionsService {
    private final Logger logger = LoggerFactory.getLogger(ConnectionsService.class);
    @Autowired
    private AirportsService airportsService;
    @Autowired
    private FlightsService flightsService;

    public ConnectionsService() {
    }

    public List<ConnectionFlight> getFlightsWithConnections(List<ConnectionFlight> allValidFlights,
                                                            String from, String to,
                                                            String originalTo, Date on,
                                                            int numOfCurrentStops) {
        List<Flight> allFlightsTo = this.flightsService.getFlightsToOnNotFrom(to, on, from);

        logger.info("getting connection flights from airport with iata " + from + " to airport with iata " + originalTo);

        for (Flight flight : allFlightsTo) {
            if (isDistanceValid(this.airportsService.getByIata(from),
                    this.airportsService.getByIata(to),
                    flight.getTakeoffAirport())) {
                allValidFlights.addAll(findAllPossibleConnectionsWithFlight(flight, from,
                        flight.getTakeoffAirport().getIata(),
                        originalTo, on,
                        numOfCurrentStops - 1));
            }
        }

        return allValidFlights;
    }

    public void addFlightsNoConnections(String from, String to,
                                       Date on, List<ConnectionFlight> allValidFlights) {
        this.flightsService.getFlightsFromToOn(from, to, on).forEach(flight -> {
            allValidFlights.add(createNewConnectionFlight(from, to, flight));
            logger.info("flight with id " + flight.getId() + " fetched successfully");
        });
    }

    public List<ConnectionFlight> getFlights(String from, String to,
                                             String originalTo, Date on,
                                             int numOfCurrentStops) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        Date date = new Date();

        System.out.println("get flights "+formatter.format(date));

        List<ConnectionFlight> allValidFlights = new ArrayList<>();

        if (noMoreStops(numOfCurrentStops)) {
            addFlightsNoConnections(from, to,
                    on,  allValidFlights);
        } else {
            getFlightsWithConnections(allValidFlights, from, to,
                    originalTo, on,
                    numOfCurrentStops);
        }

        return allValidFlights;
    }

    private boolean noMoreStops(int stops) {
        return stops == 0;
    }


    private ConnectionFlight createNewConnectionFlight(String from, String to, Flight flight) {
        return new ConnectionFlight(this.airportsService.getByIata(from),
                this.airportsService.getByIata(to),
                flight);
    }

    private List<ConnectionFlight> findAllPossibleConnectionsWithFlight(Flight flight,
                                                                        String from,
                                                                        String to,
                                                                        String originalTo,
                                                                        Date on,
                                                                        int numOfCurrentStops
                                                                        ) {
        List<ConnectionFlight> flights =
                this.getFlights(from, to, originalTo, on, numOfCurrentStops);

        flights.forEach(innerFlight -> {
            innerFlight.addFlight(flight);
        });

        return flights;
    }

    public boolean isDistanceValid(Airport origin, Airport destination, Airport connection) {
        boolean isValid = true;

        double maxDistance = this.distance(origin.getLat(), origin.getLon(),
                destination.getLat(), destination.getLon()) / 2;

        if ((this.distance(origin.getLat(), origin.getLon(),
                connection.getLat(), connection.getLon()) > maxDistance) &&
                (this.distance(destination.getLat(), destination.getLon(),
                        connection.getLat(), connection.getLon()) > maxDistance)) {
            isValid = false;
        }

        return (isValid);
    }

    public double distance(double lat1, double lon1, double lat2, double lon2) {
        final int MINUTES_IN_DEGREE = 60;
        final double STATUTE_MILES_IN_NAUTICAL_MILE = 1.1515;

        double distance = 0;

        if ((lat1 != lat2) || (lon1 != lon2)) {
            double theta = lon1 - lon2;
            distance = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.cos(Math.toRadians(theta));
            distance = Math.toDegrees(Math.acos(distance));
            distance *= MINUTES_IN_DEGREE * STATUTE_MILES_IN_NAUTICAL_MILE;
        }

        return distance;
    }
}

