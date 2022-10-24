<template>
  <div class="toast" :class="{'show' : showToast}">{{toastMessage}}</div>
  <div class="field">
    <div class="title">
      <p>타임캡슐 확인</p>
    </div>
    <div class="content">
      <div class="input-field">
        <label>받는 사람</label>
        <input v-model="coordinates" placeholder="타임캡슐 좌표" @focusout="validCoordinates">
        <p class="hint error-content">{{error}}</p>
      </div>
      <div class="input-field">
        <label>보내는 사람</label>
        <input v-model="sender" placeholder="이름 또는 닉네임">
        <p class="hint error-content">{{senderError}}</p>
      </div>
    </div>
    <Footer prev="취소" v-on:footer_res="next"></Footer>
  </div>
</template>

<script>
import Footer from '@/components/Footer';
//import Popup from '@/components/Popup';
import axios from "axios";

export default {
  name: "cTarget",
  components : {Footer},
  data () {
    return {
      coordinates : null,
      sender : null,
      error : null,
      senderError : null,
      showToast : false,
      toastMessage : ''
    }
  },
  methods : {
    validCoordinates: function() {
      if (!this.isEmpty(this.coordinates)) {
          axios.get('/api/valid/Coordinates').then(res => {
            if (res.statusText === 'OK') {
              if (res.data === true) {
                this.error = '보낼수 있는 좌표입니다.';
              } else {
                this.error = '존재하지않는 좌표입니다.';
              }
            } else {
              this.toast('오류가 발생하였습니다. 잠시후에 다시 요청해주세요.');
            }
          });
      }
    },
    isEmpty: function(target) {
      return target === undefined || target === null || isNaN(target) || target.trim().length === 0;
    },
    toast : function (message) {
      this.showToast = true;
      this.toastMessage = message;
      setTimeout(() => { this.showToast = false; this.toastMessage = '';}, 3000);
    },
    next (next) {
      if (next) {
        if (this.isEmpty(this.coordinates)) {
          this.error = '타임캡슐 좌표를 입력해주세요.';
          return;
        }
        if (this.isEmpty(this.sender)) {
          this.senderError = '이름이나 닉네임을 적어주세요.';
          return;
        }
        this.$router.push('/target/letter');
      }
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