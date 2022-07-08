var express = require("express")
const { MongoClient } = require("mongodb")
var app = express()
app.use(express.json())
const uri = "mongodb://127.0.0.1:27017"
// const uri = "mongodb://localhost:27017"
const client = new MongoClient(uri)

const roomsRouter = require('./chat/room');
app.use('/chat/room', roomsRouter)

const mssgRouter = require('./chat/message');
app.use('/chat/message', mssgRouter)


async function run() {
    try {
        await client.connect()
        console.log("connected to db")
        var server = app.listen(8081, (req, res) => {
            var host = server.address().address
            var port = server.address().port
            console.log("example server running at http://%s%s", host, port)

        })
    } catch (err) {
        console.log("um")
        await client.close()
    }
}

run()



