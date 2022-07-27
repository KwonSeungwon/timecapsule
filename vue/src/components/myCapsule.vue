<template>
  <div class="field">
    <div class="title">
      <p>내 타임캡슐</p>
    </div>
    <div class="total-info">
      <p>안읽음 {{newCount}}</p>
      <p>전체 {{totalCount}}</p>
    </div>
    <div class="select-letter top left">
      <button class="capsule"></button>
      <span class="content"></span>
    </div>
    <div class="select-letter top center">
      <button class="capsule"></button>
      <span class="content"></span>
    </div>
    <div class="select-letter top right">
      <button class="capsule"></button>
      <span></span>
    </div>
    <div class="capsule">
      <button></button>
    </div>
    <div class="select-letter bottom left">
      <button class="capsule"></button>
      <span></span>
    </div>
    <div class="select-letter bottom center">
      <button class="capsule"></button>
      <span class="content"></span>
    </div>
    <div class="select-letter bottom right">
      <button class="capsule"></button>
      <span class="content"></span>
    </div>
    <Footer prev="홈으로" one-button :prev_back="false" v-on:footer_res="next"></Footer>
  </div>
  <Popup></Popup>
</template>

<script>
import Footer from '@/components/Footer';
import Popup from "@/components/Popup";

export default {
  name: "MyCapsule",
  components : {Footer, Popup},
  data () {
    return {
      newCount : 0,
      totalCount : 0
    }
  },
  methods : {
    next (next) {
      if (!next) {
        //TODO : 로그아웃필요
        this.$router.replace('/');
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