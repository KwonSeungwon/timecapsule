<template>
  <div class="toast" :class="{'show' : showToast}">{{toastMessage}}</div>
  <div class="field">
    <div class="title">
      <p>타임캡슐 생성</p>
    </div>
    <div class="content">
      <div class="input-field inBtn">
        <label>내 좌표</label>
        <a class="input-copy-btn" @click="copy">copy</a>
        <input :value="coordinates" readonly ref="coordinates">
        <p class="hint"></p>
      </div>
      <div class="input-field">
        <label>비밀번호</label>
        <input type="password" v-model="password">
        <p class="hint error-content">{{hint.password}}</p>
      </div>
      <div class="input-field">
        <label>오픈일</label>
        <select>
          <option>01.01</option>
          <option>02.28</option>
          <option>12.31</option>
        </select>
      </div>
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
      password : '',
      hint : {
        coordinates : '',
        password : '',
        error : ''
      },
      showToast : false,
      toastMessage : ''
    }
  },
  mounted() {
    this.$nextTick(function () {
      this.createCoordinates();
    });
  },
  methods : {
    footer_res (next) {
      if (next) {
        if (this.isEmpty(this.password)) {
          this.hint.password = '사용하실 비밀번호를 입력해 주세요.';
          return;
        }
        this.$router.push({name : 'selectCapsule',
          query: {coordinates : this.coordinates, password : this.password}
        });
      }
    },
    createCoordinates : function() {
      axios.get('/api/timecapsule/join').then(res => {
        if (res.statusText === 'OK') {
          this.coordinates = res.data.xcoordinates + ',' + res.data.ycoordinates;
        } else {
          this.toast('오류가 발생하였습니다. 잠시후에 다시 요청해주세요.');
        }
      })
    },
    copy: function() {
      this.$refs.coordinates.select();
      document.execCommand('copy');
      this.toast('복사되었습니다.');
    },
    isEmpty: function(target) {
      return target === undefined || target === null || isNaN(target) || target.trim().length === 0;
    },
    toast : function (message) {
      this.showToast = true;
      this.toastMessage = message;
      setTimeout(() => { this.showToast = false; this.toastMessage = '';}, 3000);
    }
  }
}
</script>

<style scoped>
.field {

}
</style>
