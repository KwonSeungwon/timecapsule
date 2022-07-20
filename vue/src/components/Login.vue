<template>
  <div class="field">
    <div class="title">
      <p>내 좌표</p>
    </div>
    <div class="content">
      <div class="input-field">
        <input id="id" placeholder="좌표명">
      </div>
      <div class="input-field">
        <input id="pw" placeholder="password" type="password">
      </div>
      <p>좌표가 없으신가요? <router-link to="/join">새로 만들기 ></router-link></p>
    </div>
    <Footer></Footer>
  </div>
</template>

<script>
export default {
  name: "cLogin"
}
</script>

<style scoped>
.field {
  margin: 20% auto;
  width: 88%;
  height: 80%;
  background: url(../assets/images/field.png) no-repeat center transparent;
  /*background-size: cover;*/
}

.title {
  height: 20%;
}
.title p {
  text-align: center;
  margin: 0;
  padding: 10%;
}
.content {
  height: 60%;
  display: flex;
}

.content .input-field {
  margin: 10px auto;
  width: 100%;
  padding: 10px;
}

.input-field input {
  width: 100%;
}


</style>