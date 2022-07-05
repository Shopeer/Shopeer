const http = require('http');
var express = require("express")
const { IPv4 } = require("ipaddr.js")
const app = express()

const suggestions_algo_router = express.Router()

const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)














module.exports = suggestions_algo_router;

