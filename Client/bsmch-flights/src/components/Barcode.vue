<template>
  <svg
    class="barcode"
    jsbarcode-format="upc"
    :jsbarcode-value="code"
    jsbarcode-textmargin="0"
    jsbarcode-fontoptions="bold"
  ></svg>
</template>

<script>
export default {
  props: {
    code: {
      type: String,
    },
  },
  mounted() {
    JsBarcode(".barcode").init();
  }
};
</script>

<style scoped></style>
