<template>
  <div class="footer">
    <button class="btn cancel" @click="prev_f">{{prev}}</button>
    <button class="btn confirm" @click="next_f">{{next}}</button>
  </div>
</template>

<script>
export default {
  name: 'cFooter',
  props : {
    prev : {
      type : String,
      default: "이전"
    },
    next : {
      type : String,
      default: "다음"
    },
  },
  data() {

  },
  methods : {
    prev_f () {
      this.$router.go(-1);
    },
    next_f () {

    }
  }
}
</script>

<style scoped>
.btn {
  width: 100px;
  height: 45px;
  border: 0;
  background: url(../assets/images/field_abled.png) no-repeat center transparent;
  background-size: cover;
}
</style>