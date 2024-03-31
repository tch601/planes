package BsmchFlights.Repositories;

import BsmchFlights.Models.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportsRepository extends JpaRepository<Airport, String> {
    public Airport findByIata(String iata);
}
