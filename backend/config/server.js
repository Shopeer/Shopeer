const PORT = 8080;
const app = require('./app.js')
app.listen(PORT, function () {
    console.log("App running at http://:%s", PORT)
})
