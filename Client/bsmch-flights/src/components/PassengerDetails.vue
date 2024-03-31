<template>
  <v-form v-model="valid" ref="form">
    <v-card class="mx-auto mb-3 my-3 text-xs-center" color="#26c6da" dark>
      <v-card-title>
        <div>
          <div class="font-weight-bold">{{ passengerType }}</div>
          <span>{{ subtitle }}</span>
        </div>
      </v-card-title>

      <v-card-text>
        <v-row wrap>
          <v-col xs3>
            <v-text-field
              label="שם מלא (באנגלית)"
              solo
              light=""
              type="text"
              prepend-inner-icon="person"
              class="mx-2"
              v-model="name"
              :rules="[rules.required, rules.name]"
            >
            </v-text-field>
          </v-col>
          <v-col xs3>
            <v-text-field
              label="דרכון"
              solo
              light=""
              type="text"
              prepend-inner-icon="fa-passport"
              class="mx-2"
              v-model="passport"
              :rules="[rules.required, rules.passportLength, rules.onlyDigits]"
            >
            </v-text-field>
          </v-col>
          <v-col xs3>
            <v-menu
              v-model="menu"
              :close-on-content-click="false"
              top
              offset-y
              max-width="290px"
              min-width="290px"
              ref="menu"
            >
              <template v-slot:activator="{ on }">
                <v-text-field
                  readonly
                  v-on="on"
                  v-model="computedDateFormatted"
                  class="mx-2"
                  persistent-hint
                  hint="תאריך לידה"
                  solo
                  light
                  prepend-inner-icon="fa-birthday-cake"
                  :rules="[rules.required, rules.date]"
                />
              </template>
              <v-date-picker
                locale="heb"
                v-model="date"
                light
                no-title
                @input="menu = false"
              />
            </v-menu>
          </v-col>
          <v-col xs2>
            <v-select
              autocomplete
              light
              solo
              v-model="sex"
              :items="['זכר', 'נקבה']"
              class="mx-2"
              :prepend-inner-icon="sexIcon"
              :rules="[rules.required]"
            ></v-select>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-form>
</template>

<script>
export default {
  data: function(vm) {
    return {
      date: new Date().toISOString().substring(0, 10),
      dateFormatted: vm.formatDate(new Date().toISOString().substring(0, 10)),
      menu: false,
      sex: '',
      name: '',
      passport: '',
      valid: true,
      isSeatTaken: this.subtitle == 'ללא מושב' ? false : true,
      rules: {
        required: (value) => !!value || 'חובה למלא',
        passportLength: (value) => value.length == 8 || 'דרכון לא תקין',
        onlyDigits: (value) =>
          /^[0-9]*$/.test(value) || 'חייב להכיל מספרים בלבד',
        date: (value) => {
          const [day, month, year] = value.split('/');
          const birthdate = new Date(year, month - 1, day);

          return birthdate < new Date() || 'תאריך לידה לא תקין';
        },
        name: (value) => {
          const pattern = /^[A-Za-z]+(([',. -][a-zA-Z])?[a-zA-Z]*)*$/;
          return pattern.test(value) || 'שם לא חוקי';
        },
      },
    };
  },
  props: ['passengerType', 'subtitle'],
  computed: {
    computedDateFormatted() {
      return this.formatDate(this.date);
    },
    sexIcon() {
      if (this.sex == 'זכר') {
        return 'fa-mars';
      } else if (this.sex == 'נקבה') {
        return 'fa-venus';
      } else {
        return 'fa-venus-mars';
      }
    },
  },
  watch: {
    date(val) {
      this.dateFormatted = this.formatDate(this.date);
    },
  },
  methods: {
    formatDate(date) {
      if (!date) return null;

      const [year, month, day] = date.split('-');
      return `${day}/${month}/${year}`;
    },
  },
};
</script>

<style scoped>
.v-card__title div div {
  font-size: xx-large;
}
</style>
