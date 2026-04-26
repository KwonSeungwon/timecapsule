<template>
  <div class="field">
    <div class="title">
      <p>내용 작성</p>
    </div>
    <div class="selectField">
      <textarea v-model="letter" style="width: 100%; box-sizing: border-box;"></textarea>
      <p>해당 캡슐은 오픈될 때까지 비밀이에요.</p>
      <p>마음을 담아 작성해주세요.</p>
    </div>
    <Footer
        next="전송"
        @click ="test()"
        :disable_next="letter === null || letter.length < 10"
        v-on:footer_res="next('next')"></Footer>
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
        content : `캡슐에 편지를 넣고 있어요.\n편지 내용은 ${this.openDay}일 전까지 비밀로 해드릴게요 ;)`,
        btnCancel : '확인'
      },
      beSend : false,
      coordinates: null,
      sender: null,
      letterPaperType: null,
    }
  },
  created() {
    this.coordinates = this.$route.query.coordinates;
    this.sender = this.$route.query.sender;
    this.letterPaperType = this.$route.query.letterPaperType || 'LETTER';
    // openDay is normally fetched or passed. we will set it randomly for now or from backend if possible.
    this.openDay = '오픈';
  },
  methods : {
    test :function() {
      // Not used anymore as we submit on next
    },
    next :function (next) {
      if (next) {
        axios.post('/api/v1/letter', {
          coordinates: this.coordinates,
          sender: this.sender,
          letterPaperType: this.letterPaperType,
          content: this.letter,
          requestorInfo: '웹'
        }).then(() => {
          this.beSend = true;
          this.popup.btnCancel = '확인';
          this.popup.content = `캡슐에 편지를 넣고 있어요.\n편지 내용은 오픈될 때까지 비밀로 해드릴게요 ;)`;
          this.popup.open = true;
        }).catch(() => {
          this.beSend = false;
          this.popup.btnCancel = '취소';
          this.popup.content = '캡슐에 문제가 있어 편지가 잘 보내지지 않았어요.\n다시 시도해주세요!';
          this.popup.open = true;
        });
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
