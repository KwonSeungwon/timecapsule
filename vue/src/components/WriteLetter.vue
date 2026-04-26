<template>
  <div class="field">
    <div class="title">
      <p>내용 작성</p>
    </div>
    <div class="selectField">
      <textarea v-model="letter" placeholder="최소 10자 이상 작성해주세요."></textarea>
      <p v-if="openDay">해당 캡슐은 {{openDay}}일에 오픈돼요.</p>
      <p>편지는 오픈되기 전까지 안전하게 보관됩니다. ;)</p>
    </div>
    <Footer
        next="전송"
        :disable_next="letter === null || letter.length < 10"
        v-on:footer_res="submitLetter"></Footer>
  </div>
  <Popup v-if="popup.open"
         :one-button="beSend"
         :cancel="popup.btnCancel"
         confirm="재시도"
         :contents="popup.content"
         v-on:popup_res="handlePopupResponse"></Popup>
</template>

<script>
import Footer from '@/components/Footer';
import Popup from "@/components/Popup";
import axios from 'axios';

export default {
  name: "writeLetter",
  components : {Footer, Popup},
  data () {
    return {
      letter : null,
      openDay : null,
      popup : {
        open : false,
        content : '',
        btnCancel : '확인'
      },
      beSend : false
    }
  },
  methods : {
    submitLetter() {
      const self = this;
      axios.post('/api/v1/letter', {
        coordinates : this.$route.query.coordinates || "data",
        sender: "Anonymous",
        password: "123",
        letterPaperType: "LETTER",
        content: this.letter,
        userId: this.$route.query.userId || 1
      }).then(function (response){
        self.beSend = true;
        self.popup.content = '캡슐에 편지를 성공적으로 넣었어요!';
        self.popup.open = true;
      }).catch(function (error){
        self.beSend = false;
        self.popup.content = '편지 전송에 실패했습니다. 다시 시도해주세요.';
        self.popup.open = true;
      });
    },
    handlePopupResponse(close) {
      this.popup.open = false;
      if (this.beSend) {
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
textarea {
  width: 100%;
  height: 150px;
  background: transparent;
  border: 0;
  resize: none;
  font-family: inherit;
  padding: 10px;
}
</style>
