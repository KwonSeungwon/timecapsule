<template>
  <div class="popup" :class="letterType">
    <div class="body">
      <span>{{contents}}</span>
    </div>
    <div class="footer">
      <button class="cancel"
              @click="cancel_f">닫기</button>
    </div>
  </div>
  <div class="dim"></div>
</template>

<script>
export default {
  name: "cPopup",
  props : {
    contents : {
      type : String,
      default: "종료하시겠습니까?"
    },
    letterType : {
      type : String,
      default : null
    }
  },
  data() {
    return {
      result : true
    }
  },
  methods : {
    cancel_f () {
      console.log(11);
      this.$emit('popup_res');
    }
  }
}
</script>

<style scoped>
.field {
  margin: 20% auto;
  width: 90%;
  height: 80%;
  background: url(../assets/images/field.png) no-repeat center transparent;
}
.popup {
  position: relative;
  z-index: 10;
}
.dim {
  position: fixed;
  top : 0;
  left: 0;
  z-index: 1;
  width: 100vh;
  height: 100vh;
  background-color: black;
  opacity: 0.6;
}
</style>