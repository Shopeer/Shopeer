var makeApp = require('./app.js')

const app = makeApp()

app.listen(8080, () => console.log("listening on port 8080"))