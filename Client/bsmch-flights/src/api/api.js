import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: `http://10.35.112.183:9000`,
  // baseURL: `http://${process.env.SERVER_IP}:${process.env.SERVER_PORT}`,
  // baseURL: "http://localhost:8080",
});

export default {
  Airports() {
    return {
      async getAllAirports() {
        const { data } = await axiosInstance.get('/airports/reduced');

        return data;
      },
      async getAirportsInMapBounds(query) {
        const { data } = await axiosInstance.post(`/airports/query`, query);

        return data;
      },
      async fetchAirportByIata(iata) {
        const { data } = await axiosInstance.get(`/airports/${iata}`);

        return data;
      },
    };
  },
  Flights() {
    return {
      async getAllFlights() {
        const { data } = await axiosInstance.get(
          `/flights`
        );

        return data;
      },
      async getAllFlightsFromToOn(originIata, destinationIata, Date, stops) {
        const { data } = await axiosInstance.get(
          `/flights/from/${originIata}/to/${destinationIata}/on/${Date}/stops/${stops}`
        );

        return data;
      },
      async getAllFlightsFromOn(originIata, date) {
        const { data } = await axiosInstance.get(
          `/flights/allfrom/${originIata}/on/${date}`
        );

        return data;
      },
      async changeFlightsPrice(id, price) {
        const { data } = await axiosInstance.patch(
          `/flights/flight-id/${id}/price/${price}`
        );

        return data;
      },
    };
  },
  Users() {
    return {
      async loginUser(email, password) {
        const { data } = await axiosInstance.get(
          `/users/login/${email}/${password}`
        );

        return data;
      },
      async getUser(jwt) {
        const { data } = await axiosInstance.get(`/users/jwt/${jwt}`);

        return data;
      },
      async register(user) {
        const { data } = await axiosInstance.post('/users/insert', user);

        return data;
      },
      async updateUser(user) {
        const { data } = await axiosInstance.post("/users/update", user);

        return data;
      },
    };
  },
  Reservations() {
    return {
      async getReservationsByUserId(id) {
        const { data } = await axiosInstance.get(`/reservations/userId/${id}`);

        return data;
      },
      async cancelReservationsByOrderConformation(orderConformation) {
        await axiosInstance.post(`/reservations/cancel/order-conformation/${orderConformation}`);
      },
      async addReservation(reservations) {
        const { data } = await axiosInstance.post(
          '/reservations/addList',
          reservations
        );

        return data;
      },
    };
  },
  Recommendations() {
    return {
      async getAllRecommendations() {
        const { data } = await axiosInstance.get('/recommendations');

        return data;
      },
      async addRecommendation(recommendation) {
        const { data } = await axiosInstance.put(
          '/recommendations/add',
          recommendation
        );

        return data;
      },
    };
  },
};
