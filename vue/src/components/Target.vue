<template>
  <div class="field">
    <div>
      <h1>캡슐 찾기</h1>
    </div>
    <div class="input-field">
      <label>받는 사람</label>
      <input>
      <p></p>
    </div>
    <div class="input-field">
      <label>보내는 사람</label>
      <input>
    </div>
    <Footer></Footer>
  </div>
  <Popup></Popup>
</template>

<script>
import Footer from '@/components/Footer';
import Popup from '@/components/Popup';

export default {
  name: "cTarget",
  components : {Footer, Popup}
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