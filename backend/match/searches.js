const http = require('http');
var express = require("express")
const { IPv4 } = require("ipaddr.js")
const app = express()

const searches_router = express.Router()

const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)

const user_collection = mongoClient.db("shopeer_database").collection("user_collection")


// // Get Active searches GET https://shopeer.com/match/searches
// // Gets a list of all active_searches under the user from User Database
// // Body: User Id Token
// // Response: list of search ids
// searches_router.get("/searches", async (req, res) => {
//     var profile_email = req.query.email
//     try {
//         var find_cursor = await user_collection.findOne({ email: profile_email })

//         var temp_arry = await find_cursor.searches
//         console.log(temp_arry)
//         res.status(200).send(temp_arry)
//     }
//     catch (err) {
//         console.log(err)
//         res.status(400).send(err)
//     }
// })



// // Add New Active Search POST https://shopeer.com/match/searches
// // Adds a new active search for suggested matches to User Database
// // Body: User Id Token, location, distance, search_name, budget
// // Response: search id
// searches_router.post("/searches", async (req, res) => {
//     var profile_email = req.query.email
//     var search_object = create_search_object(req.body.search)
//     // console.log(search_object)
//     try {
//         var find_cursor = await user_collection.findOne({ email: profile_email })
//         // console.log(find_cursor)
//         // res.status(200).send("ok")
//         // console.log(find_cursor.searches.length)

//         if (find_cursor.searches.length == 0) {
//             var debug_res = await user_collection.updateOne(
//                 { email: profile_email }, 
//                 { $push: { searches: search_object } })
//             var find_cursor = await user_collection.findOne({ email: profile_email })
//             res.status(200).send(find_cursor)
//         } else {

//             // --- overwrite duplicate
//             for (let i = 0; i < find_cursor.searches.length; i++) {
//                 if (find_cursor.searches[i].search_name == search_object.search_name) {
//                     var debug_res = await user_collection.updateOne({ email: profile_email }, { $pull: { searches: find_cursor.searches[i] } })
//                     var debug_res = await user_collection.updateOne({ email: profile_email }, { $push: { searches: search_object } })
//                     var find_cursor = await user_collection.findOne({ email: profile_email })
//                     // console.log(find_cursor)
//                     console.log("Overwrote prev search")
//                     res.status(200).send("Overwrote prev search")
//                 } else {
//                     var debug_res = await user_collection.updateOne({ email: profile_email }, { $push: { searches: search_object } })
//                     var find_cursor = await user_collection.findOne({ email: profile_email })
//                     console.log(find_cursor)
//                     // res.status(200).send(find_cursor)
//                     break
//                 }
//             }



//             // --- deny duplicate and overwriting
//             // for (let i = 0; i < find_cursor.searches.length; i++) {
//             //     if (find_cursor.searches[i].search_name == search_object.search_name) {
//             //         console.log("Search already added")
//             //         res.status(200).send("Search already added")
//             //     } else {
//             //         var debug_res = await user_collection.updateOne({ email: profile_email }, { $push: { searches: search_object } })
//             //         var find_cursor = await user_collection.findOne({ email: profile_email })
//             //         res.status(200).send(find_cursor)
//             //         break
//             //     }
//             // }
//         }
//     }
//     catch (err) {
//         console.log(err)
//         res.status(400).send(err)
//     }
// })

// function create_search_object(body) {
//     var ret_object = {
//         search_name: body.search_name,
//         activity: body.activity,
//         location: body.location,
//         max_range: body.max_range,
//         max_budget: body.max_budget
//     }
//     return ret_object
// }


// // Delete active search DELETE https://shopeer.com/match/searches?search_id=[id]
// // Removes a current active search from active_searches to past_searches in User Database
// // Param: search id
// // Body: User Id Token
// // Response: success/ fail
// searches_router.delete("/searches", async (req, res) => {
//     var profile_email = req.query.email
//     var search = req.body.search
//     try {
//         var find_cursor = await user_collection.findOne({ email: profile_email })
//         console.log(search)
//         var no_match_flag = 0
//         for (let i = 0; i < find_cursor.searches.length; i++) {
//             if (find_cursor.searches[i].search_name == search.search_name) {
//                 var debug_res = await user_collection.updateOne({ email: profile_email }, { $pull: { searches: {search_name: search.search_name} } })
//                 var find_cursor = await user_collection.findOne({ email: profile_email })
//                 res.status(200).send(find_cursor)
//                 no_match_flag = 1
//                 break
//             }
//         }
//         if (!no_match_flag) {
//             // var debug_res = await user_collection.updateOne({email:profile_email},{ $pull: { searches: { $match: search } } } )
//             var find_cursor = await user_collection.findOne({ email: profile_email })
//             console.log("Search not in existence")
//             res.status(200).send(find_cursor)
//         }
//     }
//     catch (err) {
//         console.log(err)
//         res.status(400).send(err)
//     }
// })





module.exports = searches_router;

