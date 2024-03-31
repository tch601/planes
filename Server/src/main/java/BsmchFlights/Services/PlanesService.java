package BsmchFlights.Services;

import BsmchFlights.Models.Plane;
import BsmchFlights.Repositories.PlanesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanesService {
    @Autowired
    private PlanesRepository planesRepository;

    public List<Plane> getAll() {
        return this.planesRepository.findAll();
    }
}
