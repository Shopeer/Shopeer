hw// npm install nodem --save-dev
// ./CPEN321VM_key.pem
// connect:
// ssh -i ./CPEN321VM_key.pem azureuser@40.69.135.158 
// copy files to vm:
// scp -i ./M2/CPEN321VM_key.pem -r M2/ azureuser@40.69.135.158:M2
const { response } = require("express")
var express = require("express")
const { MongoClient } = require("mongodb")
var app = express()

const {MongoCLient} = require("mongodb")
const { runInNewContext } = require("vm")
// from MongoDBCompass
// const uri = "mongodb://localhost:27017"
 const uri = "mongodb://127.0.0.1:27017"

const client = new MongoClient(uri)

// this middleware formats requests into json format for us
app.use(express.json())

// callback: request, response
// when we try to GET /, the response is to send "hello world" to the server
app.get("/", (req, res) => {
    res.send("hello world!")
})




app.get("/serverIP", async (req, res) => {
    try {

        fetch("https://checkip.amazonaws.com/") 
            .then(response => response.text())
            .then(data => {
                console.log(data)
                res.send(data)
            })
            
    } catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})
/////////////////////////////
app.get("/serverTime", async (req, res) => {
    try {
        var today = new Date();
        var time = String(today.getHours()).padStart(2, "0") + ":" + String(today.getMinutes()).padStart(2, "0")  + ":" + String(today.getSeconds()).padStart(2, "0")
        res.status(200).send(time)
    } catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

app.get("/myName", async (req, res) => {
    try {
        res.send("Grace Zhang")
    } catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

////////////////////////////////////////////////////////////////////////////////////////
app.post("/todolist", async (req, res) => {
    try {
        await client.db("test").collection("todolist").insertOne(req.body)
        // status 200: success
        res.status(200).send("todo item added successfully\n")
    } catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

// curl -X "PUT" -H "Content-Type: application/json" -d '{"task": "finish this tutorial", "status": "heyy"}' localhost:8081/todolist
app.put("/todolist", async (req, res) => {
    try {
        await client.db("test").collection("todolist").replaceOne({"task": "finish this tutorial"}, req.body)
        // status 200: success
        res.status(200).send("todo item modified successfully")
    } catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

app.delete("/todolist", async (req, res) => {
    try {
        await client.db("test").collection("todolist").deleteOne({"task": req.body.task})
        // status 200: success
        res.status(200).send("todo item deleted successfully")
    } catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

app.get("/todolist", async (req, res) => {
    try {
        const result = client.db("test").collection("todolist").find(req.body)
        await result.forEach(console.dir)
        res.send("todo items retrieved successfully \n")
    } catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})



async function run() {
    try {
        //connect returns a Promise, which is an asynchronous function
        // we will wait until we finish connecting before we move on
        await client.connect()
        console.log("successfully connected to database")
        // after we connect to the database, we start the server
        var server = app.listen(8081, (req, res) => {
        var host = server.address().address
        var port = server.address().port
        console.log("example server running at http://%s%s", host, port)

})
    } catch (err) {
        console.log(err)
        await client.close()
    }
}

run()