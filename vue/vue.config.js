const path = require("path");
module.exports = {
  // ⚠️ 빌드 산출물이 vue/dist 가 아니라 스프링 static 에 **직접** 떨어진다.
  // vue-cli-service build 는 outputDir 을 기본으로 비우고 시작하므로,
  // `npm run build` 한 번이 src/main/resources/static 을 통째로 갈아엎는다.
  // 여기에 손으로 만든 정적 파일을 두지 말 것 — 다음 빌드에 사라진다.
  outputDir: path.resolve(__dirname, "../src/main/resources/static"),
  lintOnSave: false,
  // 소스맵은 원본 .vue 전체를 그대로 담고 있다. WAR 에 실려 나가면 누구나
  // /js/app.*.js.map 으로 받아서 프론트엔드 소스를 통째로 복원할 수 있다.
  // 배포본에는 필요 없으므로 끈다(dev 서버는 이 옵션과 무관하게 소스맵을 쓴다).
  productionSourceMap: false,
  devServer: {
    port: 3030,
    proxy: {
      '/api': {
        target: 'http://localhost:3000'
      }
    }
  },
};
