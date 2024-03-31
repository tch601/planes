package BsmchFlights.Services;

import BsmchFlights.Models.ConnectionFlight;
import BsmchFlights.Models.Flight;
import BsmchFlights.Models.Reservation;
import BsmchFlights.Repositories.FlightsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightsService {
    private final Logger logger = LoggerFactory.getLogger(FlightsService.class);
    @Autowired
    private FlightsRepository flightsRepository;
    @Autowired
    private ReservationsService reservationsService;

    public List<Flight> getAll() {
        logger.info("getting all flights");

        return this.flightsRepository.findAll();
    }

    public List<Flight> getFiveFirstFlightsFromIata(String iata, Date takeOffDate) {
        logger.info("getting first 5 flights from " + iata + " at date " + takeOffDate.toString());

        return this.getFlightsFromOn(iata, takeOffDate).stream().limit(5).collect(Collectors.toList());

    }

    public List<Flight> getFlightsToCityName(String name) {
        logger.info("getting all flights from " + name);

        return this.flightsRepository.findByArrivalAirport_city_name(name);
    }

    public List<Flight> getFlightsFromAirportIata(String iata) {
        logger.info("getting all flights from " + iata);

        return this.flightsRepository.findByTakeoffAirport_iata(iata);
    }


    public List<Flight> getFlightsFromTo(String from, String to) {
        logger.info("getting all flight from " + from + " to " + to);

        return this.flightsRepository.findByTakeoffAirport_iataAndArrivalAirport_iata(from, to);
    }

    private Date getDateOfTomorrow(Date today) {
        Date tomorrow = new Date(today.getTime());
        tomorrow.setDate(today.getDate() + 2);

        return tomorrow;
    }

    public List<Flight> getFlightsFromToOn(String from, String to, Date takeoffDate) {
        logger.info("getting flights from " + from + " to " + to + " at date " + takeoffDate.toString());

        return this.flightsRepository.
                findByTakeoffAirportAndArrivalAirportAndTakeofftimeAndNotFull
                        (from, to, takeoffDate, getDateOfTomorrow(takeoffDate));
    }

    public List<Flight> getFlightsFromOn(String from, Date takeOffDate) {
        logger.info("getting all flights from " + from + " at date " + takeOffDate.toString());

        return this.flightsRepository.findByTakeoffAirportAndTakeofftimeAndFlightNotFull
                (from, takeOffDate, getDateOfTomorrow(takeOffDate));
    }

    public List<Flight> getFlightsTo(String to) {
        logger.info("getting all flights to " + to);

        return this.flightsRepository.findByArrivalAirport_iata(to);
    }

    public List<Flight> getFlightsToOn(String to, Date takeOffDate) {
        logger.info("getting all flights to " + to + " at date " + takeOffDate.toString());

        return this.flightsRepository.
                findAllByTakeofftimeBetweenAndArrivalAirport_iata(takeOffDate, getDateOfTomorrow(takeOffDate), to);
    }

    public List<Flight> getFlightsToOnNotFrom(String to, Date takeOffDate, String from) {
        logger.info("getting all flights to " + to + " at date " + takeOffDate.toString());

        return this.flightsRepository.
                findAllByTakeofftimeBetweenAndArrivalAirport_iataAndTakeoffAirport_iataNotLike
                        (takeOffDate, getDateOfTomorrow(takeOffDate), to, from);
    }

    public void changeFlightsPrice(int id, int price) {
        logger.info("changing price of flight with id " + id);

        Flight flight = this.flightsRepository.findById(id).get();
        System.out.println(flight.getId());
        System.out.println(flight.getTakeofftime());
        flight.setPrice(price);

        this.flightsRepository.save(flight);
    }


    private List<Flight> filterDate(List<Flight> flights, Date takeOffDate) {
        logger.info("filtering flights that take off at " + takeOffDate.toString());
        int counter = 0;
        Iterator flightsInDate = flights.iterator();
        Flight flight;

        while (flightsInDate.hasNext()) {
            flight = (Flight) flightsInDate.next();
            if (flight.getTakeofftime().getYear() != takeOffDate.getYear()
                    || flight.getTakeofftime().getMonth() != takeOffDate.getMonth()
                    || flight.getTakeofftime().getDate() != takeOffDate.getDate()) {
                flightsInDate.remove();
                counter++;
            }
        }
        logger.info(counter + " flights have took off and were filtered out");

        return flights;
    }

    public List<Flight> filterSeats(List<Flight> flights) {
        logger.info("filtering flights that aren't full");
        int counter = 0;
        Iterator flightsWithOpenSeats = flights.iterator();
        Flight flight;

        while (flightsWithOpenSeats.hasNext()) {
            flight = (Flight) flightsWithOpenSeats.next();
            if (flight.isFlightFull()) {
                flightsWithOpenSeats.remove();
                counter++;
            }
        }
        logger.info(counter + " flights are full and were filtered out");

        return flights;
    }

    public List<ConnectionFlight> formatFlights(List<Flight> flights) {
        logger.info("formatting flights");
        List<ConnectionFlight> formattedFlights = new ArrayList<>();

        flights.forEach(flight -> {
            formattedFlights.add(new ConnectionFlight(flight.getTakeoffAirport(),
                    flight.getArrivalAirport(),
                    flight));
        });

        return formattedFlights;
    }
}
