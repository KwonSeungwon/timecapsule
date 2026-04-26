<template>
  <div class="popup" :class="letterType">
    <div class="body">
      <span>{{contents}}</span>
    </div>
    <div class="footer">
      <button class="btn cancel"
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
  z-index: 10;
  position: relative;
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

.popup .body {
  width: 225px;
  height: 322px;
  padding: 40px;
  margin: 40% auto 30px;
}

.popup.sheepskin .body {
  background: url(../assets/images/common/letters/large/sheepskin_large.png) no-repeat center transparent;
  background-size: cover;
}
.popup.letter .body {
  background: url(../assets/images/common/letters/large/letter_large.png) no-repeat center transparent;
  background-size: cover;
}
.popup.note .body {
  background: url(../assets/images/common/letters/large/note_large.png) no-repeat center transparent;
  background-size: cover;
}
.popup.polaroid .body {
  background: url(../assets/images/common/letters/large/polaroid.png) no-repeat center transparent;
  background-size: cover;
}

.popup .body span {
  display: inline-block;
}

.popup.note .body span {
  margin-top: -16px;
  line-height: 35.5px;
}

.popup.letter .body span {
  line-height: 41px;
  margin-top: 12px;
}
.popup.polaroid .body span {
  line-height: 25px;
}

.popup.sheepskin .body span {
  margin-top: 10px;
  line-height: 25px;
}

.popup .footer {
  text-align: center;
}

.btn {
  width: 100px;
  height: 45px;
  border: 0;
  background: url(../assets/images/field_abled.png) no-repeat center transparent;
  background-size: cover;
}

</style>