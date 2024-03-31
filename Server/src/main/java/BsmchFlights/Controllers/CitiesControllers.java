package BsmchFlights.Controllers;

import BsmchFlights.Models.City;
import BsmchFlights.Services.CitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cities")
public class CitiesControllers {
    @Autowired
    private CitiesService citiesService;

    @CrossOrigin
    @GetMapping("")
    public List<City> getAll() {
        return this.citiesService.getAll();
    }
}
