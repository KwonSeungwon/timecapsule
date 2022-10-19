<template>
  <div class="footer">
    <button class="btn cancel"
            @click="prev_f">
      {{prev}}
    </button>
    <button class="btn confirm"
            v-if="!oneButton"
            @click="next_f"
            :disabled="disable_next">
      {{next}}
    </button>
  </div>
</template>

<script>
export default {
  name: 'cFooter',
  props : {
    prev: {
      type: String,
      default: "이전"
    },
    next: {
      type: String,
      default: "다음"
    },
    prev_back: {
      type : Boolean,
      default : true
    },
    oneButton : {
      type : Boolean,
      default : false
    },
    disable_next : {
      type : Boolean,
      default : false
    }
  },
  data() {

  },
  methods : {
    prev_f () {
      if (this.prev_back) {
        if (window.history.length > 2) {
          this.$router.go(-1);
        } else {
          this.$router.push('/');
        }
      } else {
        this.$emit('footer_res', false);
      }
    },
    next_f () {
      this.$emit('footer_res', true);
    }
  }
}
</script>

<style scoped>
.btn {
  width: 105px;
  height: 41px;
  border: 0;
  background: url(../assets/images/field_abled.png) no-repeat center transparent;
  background-size: cover;
}

.beach .btn {
  background: url(../assets/images/beach/cta_btn.png) no-repeat center transparent;
  background-size: cover;
}

.desert .btn {
  background: url(../assets/images/desert/cta_btn.png) no-repeat center transparent;
  background-size: cover;
}

.mountain .btn {
  background: url(../assets/images/mountain/cta_btn.png) no-repeat center transparent;
  background-size: cover;
}



</style>