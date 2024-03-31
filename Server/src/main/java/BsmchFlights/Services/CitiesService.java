package BsmchFlights.Services;

import BsmchFlights.Models.City;
import BsmchFlights.Repositories.CitiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitiesService {
    @Autowired
    private CitiesRepository citiesRepository;

    public List<City> getAll() {
        return this.citiesRepository.findAll();
    }
}
