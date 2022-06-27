<template>
  <div class="field">
    <div>
      <p>내 좌표</p>
    </div>
    <div>
      <ul>
        <li>
          <input placeholder="좌표명">
        </li>
        <li>
          <input placeholder="password" type="password">
        </li>
      </ul>
    </div>
    <div>
      <p>좌표가 없으신가요? <router-link to="/join">새로 만들기 ></router-link></p>
    </div>
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
  width: 90%;
  height: 80%;
  background: url(../assets/images/field.png) no-repeat center transparent;
  /*background-size: cover;*/
}
</style>