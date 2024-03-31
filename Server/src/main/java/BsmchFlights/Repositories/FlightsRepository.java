package BsmchFlights.Repositories;

import BsmchFlights.Models.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FlightsRepository extends JpaRepository<Flight, Integer> {
    List<Flight> findByTakeoffAirport_iataAndArrivalAirport_iata(String takeOffLocation_Iata, String arrivalLocation_Iata);

    List<Flight> findByTakeoffAirport_city_name(String takeOffCity);
    @Query("SELECT flight FROM Flight flight WHERE flight.takeoffAirport.iata = ?1 AND flight.arrivalAirport.iata = ?2 AND flight.passengers.size < CAST(flight.plane.seatQuantity AS int) AND flight.takeofftime between ?3 and ?4")
    List<Flight> findByTakeoffAirportAndArrivalAirportAndTakeofftimeAndNotFull(String takeOffLocation_Iata, String arrivalLocation_Iata,Date Yesterday,Date tomorrow);

    List<Flight> findByTakeoffAirport_iataAndArrivalAirport_iataAndTakeofftimeBetween(String takeOffLocation_Iata, String arrivalLocation_Iata, Date Yesterday, Date tomorrow);

    List<Flight> findByArrivalAirport_city_name(String takeOffCity);

    List<Flight> findByTakeoffAirport_iata(String TakeoffAirportIata);

    List<Flight> findByTakeoffAirport_iataAndTakeofftimeBetween(String TakeoffAirportIata, Date Yesterday, Date tomorrow);
    @Query("SELECT flight FROM Flight flight WHERE flight.takeoffAirport.iata = ?1 AND flight.passengers.size < CAST(flight.plane.seatQuantity AS int) AND flight.takeofftime between ?2 and ?3")
    List<Flight> findByTakeoffAirportAndTakeofftimeAndFlightNotFull(String takeoffAirport_iata, Date Yesterday, Date tomorrow);

    List<Flight> findByArrivalAirport_iata(String iata);

    List<Flight> findByArrivalAirport_iataAndTakeofftime(String ArrivalAirportIata, Date takeOffDate);

    List<Flight> findAllByTakeofftimeBetween(Date Yesterday, Date tomorrow);

    List<Flight> findAllByTakeofftimeBetweenAndArrivalAirport_iata(Date Yesterday, Date tomorrow, String to);

    List<Flight> findAllByTakeofftimeBetweenAndArrivalAirport_iataAndTakeoffAirport_iataNotLike(Date Yesterday, Date tomorrow, String to, String from);

}
