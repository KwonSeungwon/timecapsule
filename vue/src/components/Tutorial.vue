<template>
  <div class="field">
    <div class="title">
      <p>사용법</p>
    </div>
    <div class="content">
      <p>1.</p>
      <p>2.</p>
      <p>3.</p>
      <p>4.</p>
    </div>
    <Footer prev="닫기" one-button></Footer>
  </div>
  <Popup></Popup>
</template>

<script>
import Footer from '@/components/Footer';
import Popup from "@/components/Popup";

export default {
  name: "cTutorial",
  components : {Footer, Popup},
  data () {
    return {

    }
  },
  methods : {

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