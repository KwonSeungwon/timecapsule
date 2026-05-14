<template>
  <div class="footer-actions">
    <button class="pixel-btn" @click="prev_f">
      {{ prev }}
    </button>
    <button class="pixel-btn primary"
            v-if="!oneButton"
            @click="next_f"
            :disabled="disable_next">
      {{ next }}
    </button>
  </div>
</template>

<script>
export default {
  name: 'cFooter',
  props : {
    prev: { type: String, default: "이전" },
    next: { type: String, default: "다음" },
    prev_back: { type : Boolean, default : true },
    oneButton : { type : Boolean, default : false },
    disable_next : { type : Boolean, default : false }
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
.footer-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  width: 100%;
  margin-top: 20px;
}

.pixel-btn {
  flex: 1;
  min-width: 100px;
}
</style>