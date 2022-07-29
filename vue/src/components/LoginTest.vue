<template>
  <html>
  <head>
<!--    <link href="https://fonts.googleapis.com/css?family=DungGeunMo&display=swap" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css?family=SF+Pro+Text&display=swap" rel="stylesheet"/>-->
  </head>
  <body>
  <div class="field">
  <div class="content">
    <div class="name"></div>
    <span class="title">타임캡슐 확인</span>
    <div style="top: 129px; position: absolute; left: 41px;">
      <div class="box">
        <a>좌표</a>
      </div>
      <div style="margin-top: 8px;">
        <div class="inputField">
          <input id="id" placeholder="좌표명">
          <!--      <div class="v229_584"></div>-->
        </div>
      </div>
    </div>

    <span class="v238_590">좌표가 없으신가요? 새로 만들기 ></span>
    <div style="top: 305px; left: 41px; position: absolute;">
      <div class="v229_595">
        <div class="v229_596">
          <div class="v229_597"></div>
          <span class="v229_598"></span></div>
        <div class="v229_599">
          <div class="v229_600"></div>
          <input id="id" placeholder="비밀번호"></div>
      </div>
    </div>

    <div class="v229_631">
      <div class="next"></div>
      <div class="cancel"></div>
      <span class="v229_634">취소</span><span class="v229_635">다음</span></div>
    <div class="name"></div>
    <div class="v229_609">
      <div class="v229_611">
        <div class="v229_612">
          <div class="v229_613"></div>
          <div class="v229_614"></div>
          <div class="v229_615"></div>
        </div>
        <div class="name"></div>
        <div class="name"></div>
      </div>
    </div>
  </div>
  </div>
  </body>
  </html>
  <!--  <div class="field">
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
    </div>-->
</template>

<script>
export default {
  name: "cLogin"
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

body {
  font-size: 14px;
}
.field {
  margin: 20% auto;
  width: 88%;
  height: 80%;
  background: url(../assets/images/field.png) no-repeat center transparent;
  /*background-size: cover;*/
}
.content {
  width: 375px;
  height: 812px;
  opacity: 1;
  position: absolute;
  overflow: hidden;
}

.name {
  color: #fff;
}

/*.v229_577 {
  width: 341px;
  height: 653px;
  !*  background: url("../images/v229_577.png");*!
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 0.5;
  position: absolute;
  top: 79px;
  left: 17px;
  overflow: hidden;
}*/

.title {
  width: 166px;
  color: rgba(255, 255, 255, 1);
  position: absolute;
  top: 48px;
  left: 39px;
  text-shadow: 1px 1px 0px rgb(0 0 1);
  font-weight: Regular;
  font-size: 25px;
  opacity: 1;
  text-align: left;
  text-shadow: -1px 0px #6698ff, 3px 1px #6698ff, 3px 0px #6698ff, 2px -2px #6698ff;
}

.box {
  width: 124px;
  height: 37px;
  background: url("../assets/images/boxImage.png");
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  overflow: hidden;
}
.box a{
  color: white;
  text-shadow: -1px 0 #677ce3, 0 1px #3949b7, 1px 0 #000, 0 -1px #000;
  font-size: 13px;
  margin-left: 48px;
  position: absolute;
  top: 11px;
}
.v254_1477 {
  width: 124px;
  height: 37px;
  /*  background: url("../images/v254_1477.png");*/
  opacity: 1;
  position: absolute;
  top: 0px;
  left: 0px;
}

.v254_1479 {
  width: 49px;
  height: 12px;
  background: rgba(255, 255, 255, 1);
  opacity: 1;
  position: absolute;
  top: 12px;
  left: 36px;
}

.v254_1478 {
  width: 52px;
  height: 14px;
  background: rgba(37, 76, 175, 1);
  opacity: 1;
  position: absolute;
  top: 10px;
  left: 34px;
}

.inputField input{
  width: 260px;
  height: 34px;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAASUAAAAqCAYAAAD1Rk0WAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAFKSURBVHgB7dfBSSRRFIbR96QdhlmN7sUQxAhsMzADzcRYzKBDqBBsIxBEFwriTkW7rl2gKZT/4pzF4/IC+Li3t283VSdjbYYGMLPqdXrcd4dpXkzPuur8X23O9tpOA5jbY42X6/o8POqLq8W0If2pzcXf1pf7vTeAub1WX75VvWx7dNuvx8862G5IggT8pueqdtdG9xqQRZSAKKIERBElIIooAVFECYgiSkAUUQKiiBIQRZSAKKIERBElIIooAVFECYgiSkAUUQKiiBIQRZSAKKIERBElIIooAVFECYgiSkAUUQKiiBIQRZSAKKIERBElIIooAVFECYgiSkAUUQKiiBIQRZSAKKIERNmpXqcvva3ua2wAv+Fh25+nPg5TjxbHfXdYVx1+tPH/c6tlA5jZW++r91arqUf95/Om6mSszdAAZjZtSFOQpvkLj0xVbXJ/DsgAAAAASUVORK5CYII=);
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  background-repeat: round;
  opacity: 1;
  position: absolute;
  overflow: hidden;
  border-top: none;
  border-bottom: none;
  border-left: none;
  border-right: none;
  border-radius: 8px;
}

.v229_584 {
  width: 293px;
  height: 42px;
  /*background: url("../images/v229_584.png");*/
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  position: absolute;
  top: 0px;
  left: 0px;
  overflow: hidden;
}

.v229_585 {
  width: 177px;
  color: rgba(51, 51, 51, 1);
  position: absolute;
  top: 13px;
  left: 13px;
  font-family: DungGeunMo;
  font-weight: Regular;
  font-size: 15px;
  opacity: 1;
  text-align: left;
}

.v238_590 {
  width: 179px;
  color: rgba(37, 76, 175, 1);
  position: absolute;
  top: 271px;
  left: 44px;
  font-family: DungGeunMo;
  font-weight: Regular;
  font-size: 12px;
  opacity: 1;
  text-align: left;
}

.v229_595 {
  width: 293px;
  height: 87px;
  /* background: url("../assets/images/myCodinate.png");;*/
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  position: absolute;
  top: 305px;
  left: 41px;
  overflow: hidden;
}

.v229_596 {
  width: 124px;
  height: 37px;
    background: url("../assets/images/password.png");
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  position: absolute;
  top: 0px;
  left: 0px;
  overflow: hidden;
}

.v229_597 {
  width: 124px;
  height: 37px;
  /*  background: url("../images/v229_597.png");*/
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  position: absolute;
  top: 0px;
  left: 0px;
  overflow: hidden;
}

.v229_598 {
  width: 60px;
  color: rgba(255, 255, 255, 1);
  position: absolute;
  top: 10px;
  left: 31px;
  font-family: DungGeunMo;
  font-weight: Regular;
  font-size: 15px;
  opacity: 1;
  text-align: center;
}

.v229_599 {
  width: 293px;
  height: 42px;
  /*  background: url("../images/v229_599.png");*/
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  position: absolute;
  top: 45px;
  left: 0px;
  overflow: hidden;
}

.v229_600 {
  width: 293px;
  height: 42px;
  /*  background: url("../images/v229_600.png");*/
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  position: absolute;
  top: 0px;
  left: 0px;
  overflow: hidden;
}

.v229_601 {
  width: 62px;
  color: rgba(170, 170, 170, 1);
  position: absolute;
  top: 14px;
  left: 13px;
  font-family: DungGeunMo;
  font-weight: Regular;
  font-size: 13px;
  opacity: 1;
  text-align: left;
}

.v229_631 {
  width: 217px;
  height: 41px;

  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  position: absolute;
  top: 660px;
  left: 79px;
  overflow: hidden;
}

.next {
  width: 104px;
  height: 41px;
    background: url("../assets/images/next.png");
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  position: absolute;
  top: 0px;
  left: 113px;
  overflow: hidden;
}

.cancel {
  width: 104px;
  height: 41px;
  background: url("../assets/images/cancle.png");
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  position: absolute;
  top: 0px;
  left: 0px;
  overflow: hidden;
}

.v229_634 {
  width: 33px;
  color: rgba(255, 255, 255, 1);
  position: absolute;
  top: 12px;
  left: 36px;
  font-family: DungGeunMo;
  font-weight: Regular;
  font-size: 16px;
  opacity: 1;
  text-align: center;
}

.v229_635 {
  width: 33px;
  color: rgba(255, 255, 255, 1);
  position: absolute;
  top: 12px;
  left: 149px;
  font-family: DungGeunMo;
  font-weight: Regular;
  font-size: 16px;
  opacity: 1;
  text-align: center;
}

.name {
  color: #fff;
}

.v229_609 {
  width: 375px;
  height: 44px;
  /*  background: url("../images/v229_609.png");*/
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  padding: 11px 16px;
  margin: 222px;
  opacity: 1;
  position: absolute;
  top: 0px;
  left: 0px;
  overflow: hidden;
}

.v229_610 {
  width: 54px;
  color: rgba(20, 20, 20, 1);
  position: absolute;
  top: 12px;
  left: 16px;
  font-family: SF Pro Text;
  font-weight: Semibold;
  font-size: 15px;
  opacity: 1;
  text-align: center;
}

.v229_611 {
  width: 67px;
  height: 12px;
  background: rgba(255, 255, 255, 1);
  opacity: 1;
  position: absolute;
  top: 16px;
  left: 292px;
  overflow: hidden;
}

.v229_612 {
  width: 24px;
  height: 11px;
  /*  background: url("../images/v229_612.png")*/;
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  position: absolute;
  top: 0px;
  left: 42px;
  overflow: hidden;
}

.v229_613 {
  width: 22px;
  height: 11px;
  /*background: url("../images/v229_613.png");*/
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 0.3499999940395355;
  position: absolute;
  top: 0px;
  left: 0px;
  border: 1px solid rgba(20, 20, 20, 1);
  border-top-left-radius: 2px;
  border-top-right-radius: 2px;
  border-bottom-left-radius: 2px;
  border-bottom-right-radius: 2px;
  overflow: hidden;
}

.v229_614 {
  width: 1px;
  height: 4px;
  background: rgba(20, 20, 20, 1);
  opacity: 0.4000000059604645;
  position: absolute;
  top: 3px;
  left: 23px;
}

.v229_615 {
  width: 18px;
  height: 7px;
  background: rgba(20, 20, 20, 1);
  opacity: 1;
  position: absolute;
  top: 2px;
  left: 2px;
  border-top-left-radius: 1px;
  border-top-right-radius: 1px;
  border-bottom-left-radius: 1px;
  border-bottom-right-radius: 1px;
  overflow: hidden;
}

.name {
  color: #fff;
}

.name {
  color: #fff;
}

/*.field {
  margin: 20% auto;
  width: 88%;
  height: 80%;
  background: url(../assets/images/field.png) no-repeat center transparent;
  !*background-size: cover;*!
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
}*/


</style>
