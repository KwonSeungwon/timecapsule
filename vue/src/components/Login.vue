<template>
  <div class="field">
    <div class="title">
      <p>타임캡슐 확인</p>
    </div>
    <div class="content">
      <div class="input-field">
        <label for="id">내 좌표</label>
        <input id="id" placeholder="좌표명" v-model="id">
        <p class="hint">좌표가 없으신가요? <router-link to="/join">새로 만들기 ></router-link></p>
      </div>
      <div class="input-field">
        <label for="pw">비밀번호</label>
        <input id="pw" placeholder="password" type="password" v-model="pw">
        <p class="hint error-content">{{error}}</p>
      </div>
    </div>
    <Footer prev="취소" v-on:footer_res="next"></Footer>
  </div>
</template>

<script>
import Footer from "@/components/Footer"
export default {
  name: "cLogin",
  components : {Footer},
  data () {
    return {
      id : null,
      pw : null,
      error : null
    }
  },
  methods : {
    next (next) {
      if (next) {
        //로그인 로직
        if (!this.id) {
          this.error = '아이디가 입력되지 않았습니다.';
        } else if (!this.pw) {
          //TODO : 비번 양식체크 들어가야함
          this.error = '비밀번호가 입력되지 않았습니다.';
        } else {
          //로그인 api
          this.$router.push('/mypage');
        }
      }
    }
  }
}
</script>

<style scoped>


.title {

}

.content {
  /*display: flex;*/
}






</style>