
const path = require("path");
module.exports = {
  outputDir: path.resolve(__dirname, "../src/main/resources/static"),,
  indexPath: path.resolve(__dirname, "../src/main/resources/static/index.html"),
  devServer: {
    port: 3030,
    proxy: {
      '/api': {
        target: 'http://localhost:3000'
      }
    }
  },

};
