package BsmchFlights.Services;

import BsmchFlights.Exceptions.CantCancelFlight;
import BsmchFlights.Models.Reservation;
import BsmchFlights.Repositories.ReservationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Collections;
import java.util.List;

@Service
public class ReservationsService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationsService.class);
    @Autowired
    private ReservationsRepository reservationsRepository;

    public void cancelReservationById(int reservationId) throws CantCancelFlight {
        Reservation reservation = this.findById(reservationId);

        if (isReservationCanCancel(reservation)) {
            this.reservationsRepository.updateIsSeatTakenForIdReservation(false, reservationId);
            logger.info("cancel reservation with " + reservationId);
        } else {
            logger.info("don't cancel reservation with " + reservationId);

            throw new CantCancelFlight("this reservation is over 1000 price or over 3 days ago");
        }

    }

    private boolean isReservationCanCancel(Reservation reservation){
        final long DAYS_TO_MILLISECOND = 24 * 60 * 60 * 1000L;
        final int AMOUNT_DAYS_TO_EXPIRE = 3;
        final int MAX_PRICE_TO_CANCEL = 1000;
        final Date DATE_OF_EXPIRED_DATE = new Date(new Date().getTime() -
                AMOUNT_DAYS_TO_EXPIRE * DAYS_TO_MILLISECOND);

        return reservation.getInsertionDate().compareTo(DATE_OF_EXPIRED_DATE) > 0
                && reservation.getPayment() < MAX_PRICE_TO_CANCEL;
    }
    public void cancelReservationByOrderConformation(String orderConformation) throws CantCancelFlight {
        Reservation reservation = this.getOneReservationByOrderConformation(orderConformation);

        if (isReservationCanCancel(reservation)) {
            this.reservationsRepository.updateIsSeatTakenForCodeReservation(false, orderConformation);
            logger.info("cancel reservation with " + orderConformation);
        } else {
            logger.info("don't cancel reservation with " + orderConformation);

            throw new CantCancelFlight("this reservation is over 1000 price or over 3 days ago");
        }

    }
    public List<Reservation> getAll() {
        List<Reservation> reservations = this.reservationsRepository.findAll();
        logger.info("got all reservations with " + reservations.size() + " length");

        return reservations;
    }

    public void add(Reservation newRes) {
        logger.info("adding one reservation with id " + newRes.getReservationId());

        this.reservationsRepository.save(newRes);
    }

    public void addList(List<Reservation> newRes) {
        logger.info("adding list of reservations with size " + newRes.size());

        for (Reservation currRes : newRes) {
            this.reservationsRepository.save(currRes);
        }
    }

    public Reservation findById(int id) {
        logger.info("finding reservation by id " + id);

        return this.reservationsRepository.findById(id).get();
    }
    public List<Reservation> findByOrderConformation(String orderConformation) {
        logger.info("finding reservation by order confirmation " + orderConformation);

        return this.reservationsRepository.findByOrderConfirmation(orderConformation);
    }

    private Reservation getOneReservationByOrderConformation(String orderConformation) {
        return this.findByOrderConformation(orderConformation).get(0);
    }
//    public List<Reservation> findByConfirmationCode(String confirmationCode) {
//        logger.info("finding reservation by Confirmation Code " + confirmationCode);
//
//        return this.reservationsRepository.findByOrderConfirmation(confirmationCode);
//    }

    public long getNumberOfSeatsTakenForFlight(int flightId) {
        logger.info("get number of seats taken for flight " + flightId);

        return this.reservationsRepository.countByFlightId_idAndIsSeatTaken(flightId, true);
    }

    public List<Reservation> getByUserId(int userId) {
        logger.info("get user by id " + userId);

        List<Reservation> reservationsOfUser = this.reservationsRepository.findByUserId_id(userId);
        Collections.reverse(reservationsOfUser);

        return reservationsOfUser;
    }
}
