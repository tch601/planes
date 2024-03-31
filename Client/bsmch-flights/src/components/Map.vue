<template>
  <div id="map" :class="getColor">
    <l-map
      style="max-height: 45vh; width: 100%"
      :zoom="zoom.default"
      :minZoom="zoom.min"
      :maxZoom="zoom.max"
      :center="center"
      :bounds="bounds"
      :worldCopyJump="true"
      @update:zoom="zoomUpdated"
      @update:bounds="boundsUpdated"
      @update:center="centerUpdated"
      ref="myMap"
    >
      <l-tile-layer :url="url"></l-tile-layer>
      <l-circle-marker
        v-for="(airport, index) in airports"
        :lat-lng="airport.lat_lng"
        :radius="getCircleRadius(airport, index)"
        :color="circle.color"
        :fill="true"
        :fillColor="
          airport.name == originAirport.name
            ? userCircle.fillColor
            : circle.fillColor
        "
        :fillOpacity="
          airport.name == originAirport.name
            ? userCircle.fillOpacity
            : circle.fillOpacity
        "
        @click="selectDestination(airport)"
        @mouseover="
          (optionalPoint.latlngs = [originAirport.lat_lng, airport.lat_lng]),
            updateDottedLine(),
            polylineHover(true)
        "
        @mouseleave="polylineHover(false)"
        :key="index"
      >
        <l-tooltip
          :options="{ direction: 'top' }"
          :content="airport.city.name"
        />
      </l-circle-marker>

      <!-- Selected Airport -->
      <l-circle-marker
        v-if="showTempSelected"
        :lat-lng="selectedAirport.lat_lng"
        :radius="circle.selectedRadius"
        :color="circle.color"
        :fill="true"
        :fillColor="
          selectedAirport.iata == originAirport.iata
            ? userCircle.fillColor
            : circle.fillColor
        "
        :fillOpacity="circle.fillOpacity"
      >
        <l-tooltip
          :options="{ direction: 'top' }"
          :content="selectedAirport.city.name"
        />
      </l-circle-marker>
    </l-map>
  </div>
</template>

<script>
import { LMap, LTileLayer, LCircleMarker, LTooltip } from "vue2-leaflet";
import "leaflet";
import "leaflet-arc";
import Geolib from "geolib";
import api from "@/api/api";
import eventbus from "@/plugins/eventbus";
import { toggleFocusState } from "../plugins/vue-credit-card/components/Card.vue";

export default {
  components: {
    "l-map": LMap,
    "l-tile-layer": LTileLayer,
    "l-circle-marker": LCircleMarker,
    "l-tooltip": LTooltip,
  },
  data() {
    return {
      url: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
      zoom: {
        default: 5,
        min: 3,
        max: 7,
      },
      center: [48.84222496564701, 7.495074369161092],
      circle: {
        radius: 6,
        biggerRadius: 9,
        selectedRadius: 12,
        color: "white",
        fillColor: "rgba(66, 133, 244)",
        fillOpacity: 0.7,
      },
      userCircle: {
        fillColor: "red",
        fillOpacity: 1,
      },
      bounds: {
        _southWest: {
          lat: 30.692511531584913,
          lng: -18.857461887069885,
        },
        _northEast: {
          lat: 57.43180924053573,
          lng: 33.2616787379301,
        },
      },
      optionalPoint: {
        latlngs: [[], []],
      },
      currentPolyLine: null,
      currentPolyLines: [],
      currentDottedPolyLine: null,
      maxBounds: [
        [26.8676365708402, -31.523438662290495],
        [61.66902436927203, 35.91308582723143],
      ],
      airports: [],
      originAirport: { name: "", lat_lng: [] },
      selectedAirport: { name: "", lat_lng: [] },
      showTempSelected: false,
      selectedPolylines: [],
    };
  },
  watch: {
    selectedPolylines() {
      this.currentPolyLines.forEach((line) => {
        line.remove(this.$refs.myMap.mapObject);
      });
      this.currentPolyLines = [];
      this.selectedPolylines.forEach((line) => {
        const polyline = L.Polyline.Arc(
          [line[0][0], line[0][1]],
          [line[1][0], line[1][1]]
        );
        polyline.addTo(this.$refs.myMap.mapObject);
        this.currentPolyLines.push(polyline);
      });
    },

    selectedPolyline() {
      if (this.currentPolyLine) {
        this.currentPolyLine.remove(this.$refs.myMap.mapObject);
      }
      this.currentPolyLine = L.Polyline.Arc(
        this.selectedPolyline.latlngs[0],
        this.selectedPolyline.latlngs[1]
      );
      this.currentPolyLine.addTo(this.$refs.myMap.mapObject);
    },
    bounds() {
      const query = [
        {
          key: "bounds",
          parameters: [...this.boundsAsArray[0], ...this.boundsAsArray[1]],
        },
      ];
      api
        .Airports()
        .getAirportsInMapBounds(query)
        .then((res) => {
          this.airports = res.map((airport) => {
            const newAirport = Object.assign({}, airport);
            newAirport.lat_lng = [airport.lat, airport.lon];

            return newAirport;
          });
        });
    },
  },
  mounted() {
    eventbus.$on("flight", (flight) => {
      if (this.currentPolyLine) {
        this.currentPolyLine.remove(this.$refs.myMap.mapObject);
      }
      
      for (let i = 0; i < this.currentPolyLines.length; i++) {
        this.currentPolyLines[i].remove(this.$refs.myMap.mapObject);
      }

      const newLines = [];
      
      if (flight.length !== 0) {
        flight[0].flights.forEach((fl) => {
          newLines.push([
            [fl.takeoffAirport.lat, fl.takeoffAirport.lon],
            [fl.arrivalAirport.lat, fl.arrivalAirport.lon],
          ]);
        });
      } else {
        newLines.push([
          this.originAirport.lat_lng,
          this.selectedAirport.lat_lng,
        ]);
      }

      this.selectedPolylines = newLines;
    });
    eventbus.$on("origin", (origin) => {
      this.getAirportByIata(origin.iata).then((res) => {
        this.originAirport = {
          ...res,
          lat_lng: [res.lat, res.lon],
        };
        this.center = this.originAirport.lat_lng;
      });
    });
    eventbus.$on("destination", (destination) => {
      if (this.selectedAirport.iata != destination.iata) {
        if (destination.iata == undefined) {
          this.center = this.originAirport.lat_lng;
          this.selectedAirport = this.originAirport;
          this.zoom.default = 5;
        } else {
          this.getAirportByIata(destination.iata).then((res) => {
            const wantedAirport = res;
            wantedAirport.lat_lng = [wantedAirport.lat, wantedAirport.lon];
            this.selectedAirport = wantedAirport;

            if (
              !this.doesPointInMap(this.LatLngToPoint(wantedAirport.lat_lng))
            ) {
              this.center = this.selectedAirport.lat_lng;
              this.zoom.default = 6;
            }
          });
        }
      }
    });
  },
  created() {
    const query = [
      {
        key: "bounds",
        parameters: [...this.boundsAsArray[0], ...this.boundsAsArray[1]],
      },
    ];
    api
      .Airports()
      .getAirportsInMapBounds(query)
      .then((res) => {
        this.airports = res.map((airport) => {
          const newAirport = Object.assign({}, airport);
          newAirport.lat_lng = [airport.lat, airport.lon];
          return newAirport;
        });

        this.originAirport = this.airports.find(
          (curr) => curr.name == "דבלין, אירלנד"
        );
      });
  },
  computed: {
    destinationDistance() {
      if (this.selectedAirport.name == "") {
        return 0;
      }

      const userPoint = {
        latitude: this.originAirport.lat_lng[0],
        longitude: this.originAirport.lat_lng[1],
      };
      const destPoint = {
        latitude: this.selectedAirport.lat_lng[0],
        longitude: this.selectedAirport.lat_lng[1],
      };

      return Geolib.getDistance(userPoint, destPoint);
    },

    boundsAsArray() {
      return [
        [this.bounds._southWest.lat, this.bounds._southWest.lng],
        [this.bounds._northEast.lat, this.bounds._northEast.lng],
      ];
    },
    selectedPolyline() {
      return {
        color: "rgba(66, 133, 244)",
        latlngs: [this.originAirport.lat_lng, this.selectedAirport.lat_lng],
      };
    },
    optionalPolyline() {
      return {
        color: "rgba(66, 133, 244)",
        latlngs: this.optionalPoint.latlngs,
        dashArray: "6",
      };
    },
    getColor() {
      return this.$vuetify.theme.dark ? "dark" : "light";
    },
  },
  methods: {
    polylineHover(isHover) {
      isHover
        ? this.currentDottedPolyLine.addTo(this.$refs.myMap.mapObject)
        : this.currentDottedPolyLine.remove(this.$refs.myMap.mapObject);
    },
    updateDottedLine() {
      this.currentDottedPolyLine = L.Polyline.Arc(
        this.optionalPoint.latlngs[0],
        this.optionalPoint.latlngs[1],
        {
          dashArray: this.optionalPolyline.dashArray,
        }
      );
    },
    zoomUpdated(zoom) {
      this.zoom.default = zoom;
    },
    boundsUpdated(bounds) {
      this.bounds = bounds;
    },
    centerUpdated(center) {
      this.center = center;
    },
    selectDestination(airport) {
      this.selectedAirport = airport;
      this.selectedPolyline.latlngs = [
        this.originAirport.lat_lng,
        airport.lat_lng,
      ];
      eventbus.$emit("selectDestination", {
        ...this.selectedAirport,
        distanceTo: parseInt(this.destinationDistance / 1000),
      });
    },
    LatLngToPoint(lat_lng) {
      return { lat: lat_lng[0], lng: lat_lng[1] };
    },
    doesPointInMap(point) {
      return (
        point.lat >= this.boundsAsArray[0][0] &&
        point.lat <= this.boundsAsArray[1][0] &&
        point.lng >= this.boundsAsArray[0][1] &&
        point.lng <= this.boundsAsArray[1][1]
      );
    },
    getCircleRadius(airport, index) {
      let wantedRadius;

      if (
        this.optionalPolyline.latlngs[1].join("") == airport.lat_lng.join("") ||
        airport.iata == this.selectedAirport.iata ||
        airport.iata == this.originAirport.iata
      ) {
        wantedRadius = this.circle.selectedRadius;
      } else if (index % 4 == 0) {
        wantedRadius = this.circle.biggerRadius;
      } else {
        wantedRadius = this.circle.radius;
      }

      return wantedRadius;
    },
    getAirportByIata(iata) {
      return api.Airports().fetchAirportByIata(iata);
    },
  },
};
</script>

<style>
#map.dark {
  filter: invert(100%) hue-rotate(180deg) brightness(95%) contrast(90%);
}
</style>
