<template>
  <div class="popup">
    <div class="header">

    </div>
    <div class="body">
      <span>{{contents}}</span>
    </div>
    <div class="footer">
      <button class="cancel" @click="">{{cancel}}</button>
      <button class="confirm" @click="">{{confirm}}</button>
    </div>
  </div>
  <div class="dim"></div>
</template>

<script>
export default {
  name: "popup",
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
  },
  data() {
    return {
      result : true
    }
  },
  methods : {
    confirm_f () {

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
</style>