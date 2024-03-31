package BsmchFlights.Controllers;

import BsmchFlights.Exceptions.CantCancelFlight;
import BsmchFlights.Models.Reservation;
import BsmchFlights.Services.ReservationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationsController {
    private final Logger logger = LoggerFactory.getLogger(ReservationsController.class);
    @Autowired
    private ReservationsService reservationsService;

    @CrossOrigin
    @GetMapping("")
    public ResponseEntity<Object> getAll() {
        try {
            return new ResponseEntity<>(this.reservationsService.getAll(), HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("error getting all reservations");

            return new ResponseEntity<>("Error getting all reservations "
                    + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @CrossOrigin
    @PostMapping("cancel/reservation-id/{id}")
    public ResponseEntity<Object> cancelByReservationId(@PathVariable int id) {
        try {
            this.reservationsService.cancelReservationById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CantCancelFlight exception) {
            logger.info(exception.getMessage());

            return new ResponseEntity<>("Error cancel repository by id " + id + " "
                    + exception.getMessage(), HttpStatus.FORBIDDEN);
        }

    }
    @CrossOrigin
    @PostMapping("cancel/order-conformation/{orderConformation}")
    public ResponseEntity<Object> cancelByReservationOrderConformation(@PathVariable String orderConformation) {
        try {
            this.reservationsService.cancelReservationByOrderConformation(orderConformation);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CantCancelFlight exception) {
            logger.info(exception.getMessage());

            return new ResponseEntity<>("Error cancel repository by id " + orderConformation + " "
                    + exception.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable int id) {
        try {
            return new ResponseEntity<>(this.reservationsService.findById(id), HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("error finding reservation by id " + id);

            return new ResponseEntity<>("Error finding repository by id " + id + " "
                    + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @CrossOrigin
    @GetMapping("/order/{confirmation_code}")
    public ResponseEntity<Object> findByConfirmationCode(@PathVariable String confirmation_code) {
        try {
            return new ResponseEntity<>(this.reservationsService.findByOrderConformation(confirmation_code), HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("error finding reservation by confirmation code " + confirmation_code);

            return new ResponseEntity<>("Error finding repository by confirmation code " + confirmation_code + " "
                    + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/add")
    public ResponseEntity<String> addRes(@RequestBody Reservation newReservation) {
        try {
            this.reservationsService.add(newReservation);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("error adding reservation with id " + newReservation.getReservationId());

            return new ResponseEntity<>("Error adding new reservation "
                    + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/addList")
    public ResponseEntity<String> addResList(@RequestBody List<Reservation> newReservations) {
        try {
            this.reservationsService.addList(newReservations);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("error adding list of reservations with length " + newReservations.size());

            return new ResponseEntity<>("Error adding new reservation list "
                    + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/numberOfSeatsTaken/flight/{flightId}")
    public ResponseEntity<Object> getNumberOfSeatsTakenForFlight(@PathVariable int flightId) {
        try {
            return new ResponseEntity<>(this.reservationsService.getNumberOfSeatsTakenForFlight(flightId), HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("error getting number of seats taken for flight " + flightId);

            return new ResponseEntity<>("Error getting number of seats taken for flights "
                    + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/userId/{userId}")
    public ResponseEntity<Object> findByUserId(@PathVariable int userId) {
        try {
            return new ResponseEntity<>(this.reservationsService.getByUserId(userId), HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("error finding user by id " + userId);

            return new ResponseEntity<>("Error user by id "
                    + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
