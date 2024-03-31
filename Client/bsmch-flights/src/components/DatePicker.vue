<template>
  <v-menu
    v-model="menu"
    :close-on-content-click="false"
    transition="scale-transition"
    offset-y
    max-width="290px"
    min-width="290px"
  >
    <template v-slot:activator="{ on }">
      <v-text-field
        readonly
        rounded
        v-on="on"
        hint="DD/MM/YYYY"
        v-model="computedDateFormatted"
        label="בן גוריון, תל אביב"
        solo
        :class="getTheme"
      />
    </template>
    <v-date-picker
      locale="heb"
      v-model="date"
      no-title
      @input="menu = false"
    />
  </v-menu>
</template>

<script>
export default {
  data() {
    return {
      date: new Date().toISOString().substring(0, 10),
      menu: false,
    };
  },
  computed: {
    computedDateFormatted() {
      return this.formatDate(this.date);
    },
    getTheme() {
      return this.$vuetify.theme.dark ? "dark" : "light";
    }
  },
  watch: {
    date(val) {
      this.$emit('date', this.date);
    },
  },
  methods: {
    formatDate(date) {
      if (!date) return null;

      const [year, month, day] = date.split('-');
      return `${day}/${month}/${year}`;
    },
    parseDate(date) {
      if (!date) return null;

      const [month, day, year] = date.split('/');
      return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;
    },
  },
};
</script>

<style scoped>
.v-text-field >>> .v-input__slot {
  background: #C4C4C4 !important;
}
.v-text-field.dark >>> .v-input__slot {
  background: #555 !important;
}
</style>