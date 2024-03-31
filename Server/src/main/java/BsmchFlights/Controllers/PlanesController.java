package BsmchFlights.Controllers;

import BsmchFlights.Models.Plane;
import BsmchFlights.Services.PlanesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/planes")
public class PlanesController {
    @Autowired
    private PlanesService planesService;

    @CrossOrigin
    @GetMapping("")
    public List<Plane> getAll() {
        return this.planesService.getAll();
    }
}
