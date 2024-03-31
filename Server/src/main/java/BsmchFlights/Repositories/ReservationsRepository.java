package BsmchFlights.Repositories;

import BsmchFlights.Models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReservationsRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByOrderConfirmation(String confirmationCode);

    long countByFlightId_idAndIsSeatTaken(int flightId, boolean isSeatTaken);

    List<Reservation> findByUserId_id(int userId);

    //    @Query("UPDATE reservation FROM Reservation reservation SET reservation.isSeatTaken = ?1 WHERE reservation.reservationId = ?2")
//    void updateIsSeatTakenForReservation(boolean isSeatTaken,int reservationId);
    @Modifying
    @Transactional
    @Query("UPDATE Reservation r SET r.isSeatTaken = :isSeatTaken WHERE r.reservationId = :reservationId")
    void updateIsSeatTakenForIdReservation(@Param("isSeatTaken") boolean isSeatTaken, @Param("reservationId") int reservationId);
    @Modifying
    @Transactional
    @Query("UPDATE Reservation r SET r.isSeatTaken = :isSeatTaken WHERE r.orderConfirmation = :orderConfirmation")
    void updateIsSeatTakenForCodeReservation(@Param("isSeatTaken") boolean isSeatTaken, @Param("orderConfirmation") String orderConfirmation);

}
