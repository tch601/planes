<template>
  <div>
    <v-container>
      <v-form v-model="valid">
        <v-card color="'grey' lighten-1" class="mb-10 ml-5">
          <v-row>
            <v-container>
              <v-row>
                <v-col>
                  <v-text-field
                    label="מספר אשראי"
                    solo
                    style="direction: ltr"
                    name="number"
                    v-model="cardDetail.number"
                    :rules="[rules.required, rules.minCreditNum]"
                    :maxlength="cardDetail.number.includes(' ') ? 19 : 16"
                  >
                  </v-text-field>
                </v-col>
              </v-row>
              <v-row>
                <v-col>
                  <v-text-field
                    label="שם בעל הכרטיס"
                    solo
                    style="direction:rtl;"
                    type="text"
                    v-model="cardDetail.name"
                    :rules="[rules.required, rules.name]"
                  >
                  </v-text-field>
                </v-col>
              </v-row>
              <v-row>
                <v-col>
                  <v-text-field
                    label="MM YY"
                    solo
                    type="text"
                    name="expiry"
                    v-model="cardDetail.expiry"
                    maxlength="4"
                    :rules="[rules.required, rules.expiry]"
                    style="direction: ltr"
                    reverse
                  >
                  </v-text-field>
                </v-col>
                <v-col>
                  <v-text-field
                    label="CVC"
                    solo
                    type="text"
                    name="cvc"
                    v-model="cardDetail.cvc"
                    @focus="back = true"
                    @blur="back = false"
                    :rules="[rules.required, rules.cvc]"
                    reverse
                  >
                  </v-text-field>
                </v-col>
              </v-row>
            </v-container>
          </v-row>
        </v-card>
        <div class="card-wrapper" style="direction:ltr;">
          <card ref="cardRef" v-model="cardDetail" :toInvertCVC="back"></card>
        </div>
      </v-form>
    </v-container>
    <v-btn :disabled="!valid" color="primary" @click="$emit('continue')"
      >המשך</v-btn
    >
    <v-btn text @click="$emit('back')">חזור</v-btn>
  </div>
</template>

<script>
import card from '@/plugins/vue-credit-card/components/Card';
import eventbus from '@/plugins/eventbus';
let defaultProps = {
  number: '',
  name: '',
  expiry: '',
  cvc: '',
};

export default {
  name: 'Card',
  components: {
    card,
  },
  props: {
    numberCardPut: {
      type: String,
      required: false,
    },
  },
   watch: {
    numberCardPut() {
      this.changeInfo();
    }},
  created(){
    this.changeInfo();
  },
    methods: {
     changeInfo() {
    if(this.numberCardPut){
        this.cardDetail.number=this.numberCardPut;
      }
    
    }},
  data() {
    return {
      cardDetail: defaultProps,
      back: false,
      valid: true,
      today: new Date(),
      someday: new Date(),
      rules: {
        required: (value) => !!value || 'חובה למלא שדה זה',
        cvc: (value) =>
          (value.length === 3 && /^[0-9]*$/.test(value)) || 'CVC לא תקין',
        expiry: (value) => {
          const [month, year] = value.split(' / ');
          this.someday.setFullYear('20' + year, month, 1);

          return (
            (month.toString() <= 12 && this.someday >= this.today) ||
            'תאריך תפוגה לא חוקי'
          );
        },
        name: (value) => {
          const pattern = /^[א-ת ]+$/i;
          return pattern.test(value) || 'שם לא חוקי';
        },
        minCreditNum: (value) => value.length >= 8 || 'מספר לא תקין',
      },
    };
  },
  computed: {
    fullCredit() {
      return this.cardDetail.number.replace(/\s/g, '') + this.cardDetail.cvc;
    },
  },
};
</script>

<style scoped>
div >>> .jp-card-front {
  background: #424242 !important;
}
div >>> .jp-card-back {
  background: #424242 !important;
}
div >>> .jp-card-display {
  color: white !important;
}
</style>
