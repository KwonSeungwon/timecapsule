<template>
  <div class="field">
    <div class="title">
      <p>타임캡슐 생성</p>
      <button @click="join()">테스트</button>
    </div>
    <div>
      <label>내 좌표</label>
      <input>
    </div>
    <div>
      <label>비밀번호</label>
      <input>
    </div>
    <div>
      <label>오픈일</label>
      <select>
        <option>01.01</option>
        <option>02.28</option>
        <option>12.31</option>
      </select>
    </div>
    <Footer prev="취소" v-on:footer_res="footer_res"></Footer>
  </div>
</template>

<script>
import Footer from "@/components/Footer"
import axios from 'axios';
export default {
  name: "cJoin",
  components : {Footer},
  methods : {
    footer_res (next) {
      if (next) {
        this.$router.push('/join/capsule');
      }
    },
    join: function (){
      axios.post('/api/timecapsule/userCreate').then((res)=> {
        console.log(res);
      });
    },
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
