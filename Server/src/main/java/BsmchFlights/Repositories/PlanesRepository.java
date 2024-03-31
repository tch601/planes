package BsmchFlights.Repositories;

import BsmchFlights.Models.Plane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanesRepository extends JpaRepository<Plane, Integer> {
}
