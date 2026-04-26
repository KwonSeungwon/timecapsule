<template>
  <div class="toast" :class="{'show' : showToast}">{{toastMessage}}</div>
  <div class="field">
    <div class="title">
      <p>타임캡슐 생성</p>
    </div>
    <div class="content" style="overflow-y: auto; max-height: calc(100vh - 200px); padding-bottom: 20px;">
      <div class="input-field inBtn">
        <label>내 좌표</label>
        <a class="input-copy-btn" @click="copy">copy</a>
        <input :value="coordinates" readonly ref="coordinates">
        <p class="hint"></p>
      </div>
      <div class="input-field">
        <label>비밀번호</label>
        <input type="password" v-model="password" placeholder="비밀번호 (4자리 이상)">
        <p class="hint error-content">{{hint.password}}</p>
      </div>
      <div class="input-field">
        <label>캡슐명</label>
        <input type="text" v-model="name" placeholder="캡슐 이름">
        <p class="hint error-content">{{hint.name}}</p>
      </div>
      <div class="input-field">
        <label>이메일</label>
        <input type="email" v-model="email" placeholder="알림받을 이메일 주소">
        <p class="hint error-content">{{hint.email}}</p>
      </div>
      <div class="input-field">
        <label>오픈일</label>
        <select v-model="openDayType">
          <option value="NEW_YEAR">01.01</option>
          <option value="SPRING_BREAK">02.28</option>
          <option value="END_YEAR">12.31</option>
        </select>
        <p class="hint error-content">{{hint.openDayType}}</p>
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
      name : '',
      email : '',
      openDayType : 'NEW_YEAR',
      hint : {
        coordinates : '',
        password : '',
        name : '',
        email : '',
        openDayType : '',
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
        this.hint.password = '';
        this.hint.name = '';
        this.hint.email = '';
        let valid = true;

        if (this.isEmpty(this.password) || this.password.length < 4) {
          this.hint.password = '비밀번호를 4자리 이상 입력해 주세요.';
          valid = false;
        }
        if (this.isEmpty(this.name)) {
          this.hint.name = '캡슐 이름을 입력해 주세요.';
          valid = false;
        }
        if (this.isEmpty(this.email) || !this.email.includes('@')) {
          this.hint.email = '유효한 이메일을 입력해 주세요.';
          valid = false;
        }

        if(!valid) return;

        this.$router.push({name : 'selectCapsule',
          query: {
            coordinates : this.coordinates,
            password : this.password,
            name : this.name,
            email : this.email,
            openDayType : this.openDayType
          }
        });
      }
    },
    createCoordinates : function() {
      if (this.isEmpty(localStorage.getItem("COORDINATES"))) { //이미 좌표가 있는경우 호출하지않음
        this.coordinates = localStorage.getItem("COORDINATES").replaceAll('"', '');
        return;
      }
      axios.get('/api/timecapsule/join').then(res => {
        if (res.statusText === 'OK') {
          this.coordinates = res.data.xcoordinates + ',' + res.data.ycoordinates;
          localStorage.setItem("COORDINATES", JSON.stringify(this.coordinates));
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
