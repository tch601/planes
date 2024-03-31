<template>
  <v-container>
    <v-card color="#303030" class="pa-3">
      <v-row align="center" justify="center" row>
        <v-col>
          <v-alert
            v-if="noReservations"
            type="info"
            outlined
            class="text-xs-center"
            icon="x"
            style="background-color: rgba(255, 255, 255, 0.8)"
            id="noHistory"
          >
            &nbsp; לא קיימות הזמנות עבורך
          </v-alert>
          <template v-for="(flights, index) in reservationsByConfirmationCode" >
            <SearchResults
              :results="[flights]"
              :titles="titles"
              :isHistory=true
              @row-chosen="chooseFlight($event)"
              :key="index"
            >
              <template v-slot:row-title>
                <v-row class="font-weight-black ml-5">
                  <v-col class="text-xs-center">
                    {{
                      flights.date.getDate() +
                      "/" +
                      (flights.date.getMonth() + 1) +
                      "/" +
                      flights.date.getFullYear()
                    }}
                  </v-col>
                </v-row>
              </template>
              <template v-slot:row-action> אישורי ההזמנה </template>
            </SearchResults>
          </template>
        </v-col>
      </v-row>
    </v-card>

    <v-dialog v-model="dialog" max-width="700px">
      <v-card style="font-size: x-large">
        <v-card-text>
          <v-row row wrap id="codes" justify="end" align="center">
            <v-col
              class="mb-4 pa-2 text-xs-left"
              xs6
              v-for="reservation in ticketCodesArray"
              :key="reservation.reservationId"
            >
              <strong
                ><span class="ml-4">{{
                  reservation.passengerName
                }}</span></strong
              ><br />
              <span class="ml-4">{{ reservation.takeoffAirport.iata }}</span>
              <v-icon class="mx-2">fa-arrow-right</v-icon>
              <span>{{ reservation.arrivalAirport.iata }}</span
              ><br />
              <barcode :code="reservation.ticketCode" />
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" text @click="dialog = false">סיימתי פה</v-btn>
          <v-btn
            color="primary"
            text
            type="button"
            onclick="printJS({printable:'codes', type:'html'})"
          >
            הדפס כרטיסים
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
var moment = require("moment");
moment.locale("he");
import SearchResults from "@/components/SearchResults";
import Axios from "@/api/api";
import { mapState } from "vuex";
import Swal from "sweetalert2";
import eventbus from "@/plugins/eventbus";
import print from "print-js";
import barcode from "../components/Barcode.vue";

export default {
  name: "history",
  components: {
    SearchResults,
    barcode,
  },
  data() {
    return {
      reservations: [],
      noReservations: false,
      titles: [
        { realName: "code", displayName: "מספר הזמנה" },
        { realName: "payment", displayName: "מחיר" },
      ],
      dialog: false,
      ticketCodesArray: [],
      user: {},
    };
  },
  computed: {
    ...mapState(["jwt"]),
    reservationsWithFlights() {
      return this.reservations.map((reservation) => {
        const keys = Object.keys(reservation.flightId);
        for (let key in keys) {
          reservation[keys[key]] = reservation.flightId[keys[key]];
        }

        return reservation;
      });
    },
    reservationsFlightsWithTimes() {
      return this.reservationsWithFlights.map((curr) => {
        let temp = Object.assign(curr);
        let duration = moment.duration(
          Math.abs(moment(temp.takeofftime).diff(moment(temp.arrivaltime)))
        );
        temp.trip_time = duration.humanize();

        return temp;
      });
    },
    reservationsByConfirmationCode() {
      let rs = {};
      this.reservationsFlightsWithTimes.forEach((reservation) => {
        if (!("c" + reservation.orderConfirmation in rs)) {
          rs["c" + reservation.orderConfirmation] = {
            flights: [],
            tickets: [],
            id: reservation.flightId.id,
            payment: reservation.payment,
            date: new Date(reservation.flightId.takeofftime),
            code: reservation.orderConfirmation,
          };
        }

        rs["c" + reservation.orderConfirmation].flights.push(reservation);
        rs["c" + reservation.orderConfirmation].tickets.push(reservation);
      });

      Object.keys(rs).forEach((key) => {
        rs[key].flights = rs[key].flights.reduce((acc, current) => {
          const x = acc.find(
            (reservation) => reservation.flightId.id === current.flightId.id
          );
          if (!x) {
            return acc.concat([current]);
          } else {
            return acc;
          }
        }, []);

        rs[key].flights.reverse();
        rs[key].date = new Date(rs[key].flights[0].takeofftime);
      });

      return rs;
    },
  },
  mounted() {
    this.initUser().then(() => {
      if (this.user.permissions.id === 3) {
        window.location.replace("/");
      }

      Axios.Reservations()
        .getReservationsByUserId(this.user.id)
        .then((res) => {
          res.length == 0
            ? (this.noReservations = true)
            : (this.reservations = res);
        })
        .catch((error) => {
          Swal.fire({
            type: "error",
            title: "מצטערים, משהו לא עבד",
            showConfirmButton: false,
            timer: 2000,
          });
        });
    });
  },
  methods: {
    chooseFlight(reservation) {
      this.ticketCodesArray =
        this.reservationsByConfirmationCode["c" + reservation.code].tickets;
      this.dialog = true;
    },
    async initUser() {
      this.user = await Axios.Users().getUser(this.jwt);

      if (this.user.id === -1) {
        Swal.fire({
          type: "error",
          title: "אופס",
          text: "נראה שצריך שתתחבר שוב",
        });
      }
    },
  },
};
</script>

<style scoped></style>
