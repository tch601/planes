package BsmchFlights.Controllers;

import BsmchFlights.Models.Recommendation;
import BsmchFlights.Services.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    private final Logger logger = LoggerFactory.getLogger(ReservationsController.class);

    @Autowired
    private RecommendationService recommendationService;

    @CrossOrigin
    @GetMapping("")
    public ResponseEntity<Object> getAll() {
        try {
            return new ResponseEntity<>(this.recommendationService.getAll(), HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("error getting all recommendations");

            return new ResponseEntity<>("Error getting all recommendations "
                    + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @CrossOrigin
    @PutMapping("/add")
    public ResponseEntity<Object> addRecommendation(@RequestBody Recommendation newRecommendation) {
        try {
            this.recommendationService
                    .addRecommendation(newRecommendation);
            return new ResponseEntity<>("added recommendation successfully", HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("error add recommendation");

            return new ResponseEntity<>("Error add recommendation "
                    + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
