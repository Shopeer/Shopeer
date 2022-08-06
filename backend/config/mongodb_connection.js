require("dotenv").config()
const { MongoClient } = require("mongodb")
// const uri = "mongodb://admin:shopeer@20.230.148.126:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const uri = "mongodb://"+process.env.DB_USER+":"+process.env.DB_PASS+"@20.230.148.126:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
//const uri = "mongodb://127.0.0.1:27017"
const mongoClient = new MongoClient(uri)
const user_collection = mongoClient.db("shopeer_database").collection("test_collection")
mongoClient.connect()

module.exports = user_collection