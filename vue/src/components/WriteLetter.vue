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
    <Footer
        next="전송"
        :disable_next="letter === null || letter.length < 10"
        v-on:footer_res="next"></Footer>
  </div>
  <Popup v-if="popup.open"
         :one-button="beSend"
         :cancel="popup.btnCancel"
         confirm="재시도"
         :contents="popup.content"
         v-on:popup_res="popup_f"></Popup>
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
      popup : {
        open : false,
        content : `캡슐에 편지를 넣고 있어요.\n편지 내용은 ${this.openDay}일 전까지 비밀로 해드릴게요 ;)`,
        btnCancel : '확인'
      },
      beSend : false
    }
  },
  methods : {
    next (next) {
      if (next) {
        //axios 전송후 결과

        //실패
        if (!this.beSend) {
          this.popup.btnCancel = '취소';
          this.popup.content = '캡슐에 문제가 있어 편지가 잘 보내지지 않았어요.\n다시 시도해주세요!';
        } else {
          this.popup.btnCancel = '확인';
          this.popup.content = `캡슐에 편지를 넣고 있어요.\n편지 내용은 ${this.openDay}일 전까지 비밀로 해드릴게요 ;)`;
        }
        this.popup.open = true;
      }
    },
    popup_f(close) {
      if (this.beSend) {
        if (!close) {
          this.popup.open = false;
          this.$router.replace('/');
        }
      } else {
        if (!close) {
          this.popup.open = false;
        } else {
          //재전송
          this.next(true);
        }
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