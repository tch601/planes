<template>
  <div>
    <v-list justify="space-between" three-line subheader>
      <v-expansion-panels dark>
        <v-expansion-panel
          id="searchResult"
          :ripple="true"
          v-for="item in resultsPerPage"
          class="animated fadeIn"
          :key="item.flights[0].id + Math.random()"
        >
          <v-expansion-panel-header>
            <v-list-item>
              <v-list-item-avatar class="ml-5">
                <img src="@/assets/BasmachAirways-IconOnly.png" />
              </v-list-item-avatar>

              <slot name="row-title"> </slot>

              <v-list-item-content
                v-for="title in titles"
                :key="title.realName"
              >
                <p>
                  <v-list-item-title
                    v-if="title.realName == 'stops' && item.flights.length == 1"
                    class="item-title text-xs-center"
                    >ללא עצירות
                  </v-list-item-title>
                  <v-list-item-title
                    v-else-if="title.realName == 'stops'"
                    class="item-title text-xs-center"
                    >{{ item.flights.length - 1 }}
                  </v-list-item-title>
                  <v-list-item-title v-else class="item-title text-xs-center">{{
                    item[title.realName]
                  }}</v-list-item-title>
                  <v-list-item-subtitle>{{
                    title.displayName
                  }}</v-list-item-subtitle>
                </p>
              </v-list-item-content>

              <v-row id="plain" justify="space-between" align="center">
                <v-icon style="transform: scaleX(-1)"
                  >fa-plane-departure</v-icon
                >
                <v-divider></v-divider>
                <v-icon
                  v-for="index in item.flights.length - 1"
                  :key="index"
                  style="font-size: 50%"
                  >far fa-circle</v-icon
                >
                <v-divider></v-divider>
                <v-icon style="transform: scaleX(-1)">fa-plane-arrival</v-icon>
              </v-row>

              <v-list-item-action>
                <v-chip
                  color="light-blue darken-3"
                  class="mr-5 mt-3"
                  @click="openModal()"
                >
                  <slot name="row-action"> </slot>
                  <v-avatar>
                    <v-icon class="">fa-arrow-left</v-icon>
                  </v-avatar>
                </v-chip>
              </v-list-item-action>
              <EditModal
              :flight="flight"
                v-show="modalOpen"
                :modalOpen="modalOpen"
                title="ערוך טיסה"
              >
                


                
                  <v-btn class="ml-3" color="primary" @click="closeModal()"
                    >סגור</v-btn
                  >
                
            
              </EditModal>
            </v-list-item>
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            &nbsp; פרטים נוספים על הטיסה
            <br /><br />
            <v-row>
              <v-col :key="flight.id" v-for="flight in item.flights" cols="4">
                <v-row>
                  <v-col class="text-center">
                    {{ flight.takeoffAirport.name }}
                    <v-icon small c>fa-arrow-left</v-icon>
                    {{ flight.arrivalAirport.name }}
                    <br />
                  </v-col>
                </v-row>
                <v-row justify="center">
                  <v-col
                    cols="4"
                    class="pa-1"
                    v-for="chip in flightToChips(flight)"
                    :key="chip.id"
                  >
                    <v-chip style="width: 95%">
                      <v-icon
                        small
                        style="transform: rotateY(180deg)"
                        class="pr-2"
                        >{{ chip.icon }}</v-icon
                      >
                      {{ chip.text }}
                    </v-chip>
                  </v-col>
                </v-row>
              </v-col>
            </v-row>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>

      <div v-if="flights.length > 6" class="text-xs-center mt-4">
        <v-pagination
          class="mb-2"
          v-model="page"
          :length="pageLength"
          circle
          light
          :total-visible="7"
        ></v-pagination>
      </div>
    </v-list>
  </div>
</template>

<script>
import eventbus from "@/plugins/eventbus";
import EditModal from "@/components/EditModal";


export default {
  name: "SearchResults",
  props: ["results", "titles", "flight", "modalOpen"],
  components: {
    EditModal,
  },
  data() {
    return {
      
      page: 1,
      numOfFlightsPerPage: 5,
      newPrice: null,
    };
  },
  methods: {
    openModal() {
      this.modalOpen = true;
    },
    closeModal() {
      this.modalOpen = false;
    },


    choseRow(item) {
      this.$emit("row-chosen", item);
    },
    flightToChips(flight) {
      return [
        { id: 1, icon: "fa-plane-departure", text: "המראה:" },
        {
          id: 2,
          icon: "fa-calendar-alt",
          text: flight.takeofftime.toLocaleDateString("en-GB"),
        },
        {
          id: 3,
          icon: "fa-clock",
          text:
            ("0" + flight.takeofftime.getHours()).slice(-2) +
            ":" +
            ("0" + flight.takeofftime.getMinutes()).slice(-2),
        },
        { id: 4, icon: "fa-plane-arrival", text: "נחיתה:" },
        {
          id: 5,
          icon: "fa-calendar-alt",
          text: flight.arrivaltime.toLocaleDateString("en-GB"),
        },
        {
          id: 6,
          icon: "fa-clock",
          text:
            ("0" + flight.arrivaltime.getHours()).slice(-2) +
            ":" +
            ("0" + flight.arrivaltime.getMinutes()).slice(-2),
        },
      ];
    },
  },
  mounted() {
    eventbus.$on("search", () => {
      this.page = 1;
    });
  },
  computed: {
    flights() {
      return this.results.map((result) => {
        result.flights.forEach((flight) => {
          flight.arrivaltime = new Date(flight.arrivaltime);
          flight.takeofftime = new Date(flight.takeofftime);
        });

        return result;
      });
    },
    resultsPerPage() {
      return this.flights.slice(
        (this.page - 1) * this.numOfFlightsPerPage,
        this.page * this.numOfFlightsPerPage
      );
    },
    pageLength() {
      let value = parseInt(this.flights.length / this.numOfFlightsPerPage);

      if (this.flights.length % this.numOfFlightsPerPage != 0) {
        value++;
      }

      return value;
    },
  },
};
</script>

<style scoped>
.v-application--is-rtl .v-chip .v-avatar {
  margin-right: 0px;
  margin-left: 0.25px;
}
</style>
