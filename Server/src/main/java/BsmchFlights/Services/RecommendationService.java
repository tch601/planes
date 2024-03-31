package BsmchFlights.Services;

import BsmchFlights.Models.Recommendation;
import BsmchFlights.Repositories.RecommendationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationsService.class);

    @Autowired
    private RecommendationRepository recommendationRepository;

    public List<Recommendation> getAll() {
        List<Recommendation> recommendations = this.recommendationRepository.findAll();
        logger.info("got all recommendations with " + recommendations.size() + " length");

        return recommendations;
    }

    public void addRecommendation(Recommendation newRecommendation) {
        recommendationRepository.save(newRecommendation);
        logger.info("added recommendation from user " + newRecommendation.getUsername());
    }
}
