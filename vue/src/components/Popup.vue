<template>
  <transition name="pop">
    <div class="popup-mask">
      <div class="popup-container pixel-border">
        <div class="popup-content">
          <slot name="contents">
            <p class="default-contents">{{ contents }}</p>
          </slot>
        </div>
        <div class="popup-footer">
          <button class="pixel-btn" @click="cancel_f">{{ cancel }}</button>
          <button v-if="!oneButton" class="pixel-btn primary" @click="confirm_f">{{ confirm }}</button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
export default {
  name: "cPopup",
  props : {
    contents : { type: String, default: "종료하시겠습니까?" },
    cancel : { type: String, default: "취소" },
    confirm : { type: String, default: "확인" },
    oneButton : { type: Boolean, default: false },
  },
  methods : {
    confirm_f () { this.$emit('popup_res', true); },
    cancel_f () { this.$emit('popup_res', false); }
  }
}
</script>

<style scoped>
.popup-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  backdrop-filter: blur(2px);
}

.popup-container {
  width: 90%;
  max-width: 320px;
  padding: 24px;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.popup-content {
  text-align: center;
  color: #333;
  line-height: 1.6;
}

.default-contents {
  margin: 0;
  font-size: 16px;
  word-break: keep-all;
}

.popup-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.popup-footer .pixel-btn {
  flex: 1;
  font-size: 14px;
}

/* Animation */
.pop-enter-active {
  animation: pop-in 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}
.pop-leave-active {
  animation: pop-in 0.2s reverse ease-in;
}

@keyframes pop-in {
  0% { transform: scale(0.5); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}
</style>