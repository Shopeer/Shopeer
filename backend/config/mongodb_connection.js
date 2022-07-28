const { MongoClient } = require("mongodb")  // this is multiple return
const uri = "mongodb://admin:shopeer@20.230.148.126:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)
const user_collection = mongoClient.db("shopeer_database").collection("user_collection")
mongoClient.connect()

module.exports = user_collection