<template>
  <div class="field">
    <div class="title">
      <p>내용 작성</p>
    </div>
    <div class="selectField">
      <textarea v-model="letter"></textarea>
      <p>해당 캡슐은 {{openDay}}일에 오픈돼요.</p>
      <p>편지는 오픈되기 한 달 전까지 보낼 수 있어요.</p>
    </div>
    <Footer></Footer>
  </div>
  <Popup></Popup>
</template>

<script>
import Footer from '@/components/Footer';
import Popup from "@/components/Popup";

export default {
  name: "writeLetter",
  components : {Footer, Popup},
  data () {
    return {
      letter : null,
      openDay : null,
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