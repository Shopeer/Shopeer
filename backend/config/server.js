// // local vm
// const IP = '192.168.64.15';
// azure vm
// const IP = "20.230.148.126";


const PORT = 3000;

const app = require('./app.js')

app.listen(PORT, function () {
    console.log("App running at http://:%s", PORT)
})
