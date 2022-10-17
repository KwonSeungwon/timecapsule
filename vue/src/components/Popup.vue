<template>
  <div class="popup">
    <div class="body">
      <span>{{contents}}</span>
    </div>
    <div class="footer">
      <button class="cancel"
              @click="cancel_f">{{cancel}}</button>
      <button class="confirm"
              v-if="!oneButton"
              @click="confirm_f">{{confirm}}</button>
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
    cancel : {
      type : String,
      default: "취소"
    },
    confirm : {
      type : String,
      default: "확인"
    },
    oneButton : {
      type : Boolean,
      default : false
    },
  },
  data() {
    return {
      result : true
    }
  },
  methods : {
    confirm_f () {
      this.$emit('popup_res', true);
    },
    cancel_f () {
      this.$emit('popup_res', false);
    }
  }
}
</script>

<style scoped>
.popup {
  position: absolute;
  top : 40%;
  z-index: 2;
  left: calc(50% - 125px);
  z-index: 2;
  width: 250px;
  background: url(../assets/images/field.png) no-repeat center transparent;
  background-size: cover;
}
.dim {
  position: fixed;
  top : 0;
  left: 0;
  z-index: 1;
  width: 100%;
  height: 100%;
  background-color: black;
  opacity: 0.6;
}
</style>