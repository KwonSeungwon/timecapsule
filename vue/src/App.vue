<template>
  <HomeView>

  </HomeView>
</template>

<style>
button,
p,
label,
span {
  color: white;
  font-size: 14px;
}

.beach button, .beach p, .beach label, .beach span{
  text-shadow: 1px 1px #006484, -1px -1px #006484, 1px -1px #006484, -1px 1px #006484;
}

.mountain button, .mountain p, .mountain label, .mountain span {
  text-shadow: 1px 1px #315fd3, -1px -1px #315fd3, 1px -1px #315fd3, -1px 1px #315fd3;
}

.desert button, .desert p, .desert label, .desert span {
  text-shadow: 1px 1px #5E3432, -1px -1px #5E3432, 1px -1px #5E3432, -1px 1px #5E3432;
}

.space button, .space p, .space label, .space span{
  text-shadow: 1px 1px #2D459F, -1px -1px #2D459F, 1px -1px #2D459F, -1px 1px #2D459F;
}

.title {
  height: 82px;
}
.content {
  height: calc(100% - 162px);
}
.footer {
  height: 70px;
  text-align: center;
}
.footer button.btn:not(:first-child) {
  margin-left: 10px;
}
.title p {
  margin: 0;
  padding: 30px 20px;
  font-size: 22px;
}

.btn {
  width: 104px;
  height: 41px;
  border: 0;
  background: url(./assets/images/field_abled.png) no-repeat center transparent;
  background-size: cover;
}


.content .input-field {
  margin: 0 auto;
  width: calc(100% - 40px);
  padding: 10px 20px;
}

.input-field>label {
  display: block;
  width: 112px;
  height: 37px;
  text-align: center;
  line-height: 37px;
  margin-bottom: 10px;
}
.input-field.inBtn {
  position: relative;
}
.input-copy-btn {
  position: absolute;
  cursor: pointer;
  padding: 12px;
  right: 27px;
}
.beach .input-field>label {
  background: url(./assets/images/beach/input_title.png) no-repeat center transparent;
  background-size: cover;
}

.desert .input-field>label {
  background: url(./assets/images/desert/input_title.png) no-repeat center transparent;
  background-size: cover;
}

.mountain .input-field>label {
  width: 124px;
  background: url(./assets/images/mountain/input_title.png) no-repeat center transparent;
  background-size: cover;
}

.space .input-field>label {
  background: url(./assets/images/beach/input_title.png) no-repeat center transparent;
  background-size: cover;
}

.input-field>input {
  width: 273px;
  height: 22px;
  padding: 10px;
  border: 0;
  cursor: pointer;
  background: url(./assets/images/beach/input.png) no-repeat center transparent;
  /*background-size: cover;*/
}
.input-field>select {
  width: 292px;
  height: 54px;
  padding: 10px;
  border: 0;
  cursor: pointer;
  background: url(./assets/images/beach/input.png) no-repeat center transparent;
  /*background-size: cover;*/
}

.input-field>p.hint {
  font-size: 13px;
  text-shadow: none;
  height: 13px;
}

.beach .input-field>p.hint,
.beach .input-field>p.hint a {
  color: #006484;
}

.mountain .input-field>p.hint,
.mountain .input-field>p.hint a {
  color: #264CAF;
}

.desert .input-field>p.hint,
.desert .input-field>p.hint a{
  color: #5E3432;
}

.space .input-field>p.hint,
.space .input-field>p.hint a{
  color: #2D459F;
}

.field {
  margin: 20% auto;
  width: 88%;
  height: 80%;
}

.beach .field {
  background: url(./assets/images/beach/bg.png) no-repeat center transparent;
  background-size: cover;
}

.mountain .field {
  background: url(./assets/images/mountain/bg.png) no-repeat center transparent;
  background-size: cover;
}

.desert .field {
  background: url(./assets/images/desert/bg.png) no-repeat center transparent;
  background-size: cover;
}

.space .field {
  background: url(./assets/images/mountain/bg.png) no-repeat center transparent;
  background-size: cover;
}

button.letter,
label.letter {
  background: url(./assets/images/common/letters/small/letter_small.png) no-repeat center transparent;
  background-size: cover;
}

button.letter.open,
label.letter.open {
  opacity: 0.4;
  background: url(./assets/images/common/letters/small/letter_small.png) no-repeat center transparent;
  background-size: cover;
}

button.letter.note,
label.letter.note {
  background: url(./assets/images/common/letters/small/note_small.png) no-repeat center transparent;
  background-size: cover;
}

button.letter.note.open,
label.letter.note.open {
  background: url(./assets/images/common/letters/small/note_small_disabled.png) no-repeat center transparent;
  background-size: cover;
}

button.letter.polaroid,
label.letter.polaroid {
  background: url(./assets/images/common/letters/small/polaroid_small.png) no-repeat center transparent;
  background-size: cover;
}

button.letter.sheepskin,
label.letter.sheepskin {
  background: url(./assets/images/common/letters/small/sheepskin_small.png) no-repeat center transparent;
  background-size: cover;
}

button.letter.sheepskin.open,
label.letter.sheepskin.open {
  background: url(./assets/images/common/letters/small/sheepskin_small_disabled.png) no-repeat center transparent;
  background-size: cover;
}

.toast {
  position: fixed;
  left: 50%;
  top: 50px;
  width: 294px;
  padding: 15px;
  margin-left: -162px;
  visibility: hidden;
  color: #fff;
  text-align: center;
  z-index: 10;
  opacity: 0.6;
  background: url(./assets/images/common/toast.png) no-repeat center transparent;
  background-size: cover;
}
.toast.show {
  visibility: visible;
  -webkit-animation: fadein 0.5s, fadeout 0.5s 2.5s;
  animation: fadein 0.5s, fadeout 0.5s 2.5s;
}

@-webkit-keyframes fadein {
  from {top: 0; opacity: 0;}
  to {top: 50px; opacity: 0.6;}
}

@keyframes fadein {
  from {top: 0; opacity: 0;}
  to {top: 50px; opacity: 0.6;}
}

@-webkit-keyframes fadeout {
  from {top: 50px; opacity: 0.6;}
  to {top: 0; opacity: 0;}
}

@keyframes fadeout {
  from {top: 50px; opacity: 0.6;}
  to {top: 0; opacity: 0;}
}

@font-face {
  font-family: tcf;
  src: url(./assets/font/DungGeunMo.woff) format('woff');
}
#app {
  font-family: tcf, sans-serif;
  overflow-x: hidden;
}
</style>
<script>
import HomeView from "@/views/HomeView";
export default {
  components: {HomeView}
}
</script>