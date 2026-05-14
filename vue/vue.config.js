const path = require("path");
module.exports = {
  outputDir: path.resolve(__dirname, "../src/main/resources/static"),
  lintOnSave: false,
  devServer: {
    port: 3030,
    proxy: {
      '/api': {
        target: 'http://localhost:3000'
      }
    }
  },
};
