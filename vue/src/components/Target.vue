<template>
  <div class="field">
    <div class="title">
      <p>타임캡슐 확인</p>
    </div>
    <div class="content">
      <div class="input-field">
        <label>받는 사람</label>
        <input v-model="coordinates">
        <p></p>
      </div>
      <div class="input-field">
        <label>보내는 사람</label>
        <input v-model="sender">
      </div>
      <p class="error-content">{{error}}</p>
    </div>
    <Footer prev="취소" v-on:footer_res="next"></Footer>
  </div>
</template>

<script>
import Footer from '@/components/Footer';
//import Popup from '@/components/Popup';

export default {
  name: "cTarget",
  components : {Footer},
  data () {
    return {
      coordinates : null,
      sender : null,
      error : null
    }
  },
  methods : {
    next (next) {
      if (next) {
        if (!this.coordinates) {
          this.error = '편지를 작성할 캡슐 좌표를 입력해 주세요.';
          return;
        }
        if (!this.sender) {
          this.error = '편지를 보내는 사람 이름이나 닉네임을 적어주세요.';
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