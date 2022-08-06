if (process.env.NODE_ENV) {
    require("dotenv").config({
        path: `${__dirname}/../.env.${process.env.NODE_ENV}`
    })
} else {
    require("dotenv").config();
}
const { MongoClient } = require("mongodb")
const uri = "mongodb://"+process.env.DB_USER+":"+process.env.DB_PASS+"@20.230.148.126:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
//const uri = "mongodb://127.0.0.1:27017"
const mongoClient = new MongoClient(uri)
const user_collection = mongoClient.db(process.env.DB_DATABASE).collection("user_collection")
const room_collection = mongoClient.db(process.env.DB_DATABASE).collection("room_collection")
mongoClient.connect()
module.exports = {user_collection, room_collection}