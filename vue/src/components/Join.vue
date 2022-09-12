<template>
  <div class="field">
    <div class="title">
      <p>타임캡슐 생성</p>
    </div>
    <div>
      <label>내 좌표</label>
      <input :value="coordinates.xcoordinates + ',' + coordinates.ycoordinates" readonly ref="coordinates"><button @click="copy()">copy</button>
    </div>
    <div>
      <label>비밀번호</label>
      <input v-model="password">
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
  data () {
    return {
      coordinates : '',
      password : ''
    }
  },
  mounted() {
    this.$nextTick(function () {
      axios.get('/api/timecapsule/join').then(res => {
        this.coordinates = res.data;
      })
    });
  },
  methods : {
    footer_res (next) {
      if (next) {
        this.$router.push('/join/capsule');
      }
    },
    copy: function() {
      this.$refs.coordinates.select();
      document.execCommand('copy');
      alert('복사되었습니다.');
    },
    isEmpty: function(target) {
      if (target === undefined || target === null || isNaN(target) || target.trim().length === 0) {
        console.log('in');
        return true;
      }
    },
    join: function (){
      if (this.isEmpty(this.password)) {
        alert('사용하실 비밀번호를 입력해주세요.');
      }
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
