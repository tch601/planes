package BsmchFlights.Models;

import javax.persistence.*;

@Entity
@Table(name="t_recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int recommendationId;

    @Column(name="username")
    private String username;

    @Column(name="img")
    private String img;

    @Column(name="rating")
    private double rating;

    @Column(name="content")
    private String content;

    public Recommendation(int recommendationId,
                       String username,
                       String img,
                       double rating,
                       String content) {
        this.recommendationId = recommendationId;
        this.username = username;
        this.img = img;
        this.rating = rating;
        this.content = content;
    }

    public Recommendation() {}

    public int getRecommendationId() {
        return this.recommendationId;
    }

    public void setRecommendationId(int recommendationId) {
        this.recommendationId = recommendationId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getRating() {
        return this.rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
