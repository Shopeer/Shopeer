
//======================app.js============================
//========================================================
var user_collection = require('./mongodb_connection.js')

app.get("/match/searches", async (req, res) => {
    var profile_email = req.query.email
    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (!find_cursor) {
            res.status(404).json({response: "User not found."})
            return
        }
        var temp_arry = await find_cursor.searches
        console.log(temp_arry)
        res.status(200).send(temp_arry)
        
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

//========================================================


// var express = require('express')
// const app = express()

// app.use(express.json())



// app.post('/users', async (req, res) => {
//     const { password, username } = req.body
//     if (!password || !username) {
//         res.sendStatus(400)
//         return
//     }

//     const userId = await database.createUser(username, password)

//     res.send({ userId })
// })

// module.exports = { app }

// function makeApp() {
//     var express = require('express')
//     const app = express()

//     app.use(express.json())


//     const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
//     const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
//     const mongoClient = new MongoClient(uri)


//     app.post('/users', async (req, res) => {
//         const { password, username } = req.body
//         if (!password || !username) {
//             res.sendStatus(400)
//             return
//         }

//         const userId = await database.createUser(username, password)

//         res.send({ userId })
//     })
//     return app
// }

// module.exports = { makeApp }

// export default async function() {

//     var express = require("express")
//     const app = express()


//     const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
//     const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
//     const mongoClient = new MongoClient(uri)

//     const user_collection = mongoClient.db("shopeer_database").collection("user_collection")

//     await mongoClient.connect()  // waits for sync op to finish
//     console.log("Successfully connected to the database")

//     app.use(express.json())

//     app.get("/", (req, res) => {
//         res.send("Hello world\n")
//     })
//     app.post("/", (req, res) => {
//         res.send(req.body.text)
//     })

//     // Express Routers
//     const user_profile_router = require('../user/profile.js');
//     app.use('*', user_profile_router);
//     app.use('/user', user_profile_router)

//     const user_peers_router = require('../user/peers.js');
//     app.use('*', user_peers_router);
//     app.use('/user', user_peers_router)


//     // const searches_router = require('../match/searches');
//     // app.use('*', searches_router);
//     // app.use('/match', searches_router)

//     const suggestions_algo_router = require('../match/suggestions_algo.js');
//     app.use('*', suggestions_algo_router);
//     app.use('/match', suggestions_algo_router)

//     const roomsRouter = require('../chat/room');
//     app.use('/chat/room', roomsRouter)

//     const mssgRouter = require('../chat/message');
//     app.use('/chat/message', mssgRouter)

//     // // local vm
//     // const IP = '192.168.64.15';
//     const PORT = 8080;

//     // azure vm
//     // const IP = "20.230.148.126";
//     // const PORT = "8080";

//     // grace
//     // const IP = "206.116.227.202";
//     // const PORT = "3000";
//     //-------------------------------------------------------------------------------
//     //-------------------------------------------------------------------------------
//     //-------------------------------------------------------------------------------



//     /////////////// match requests //////////////
//     // Get Active searches GET https://shopeer.com/match/searches
//     // Gets a list of all active_searches under the user from User Database
//     // Body: User Id Token
//     // Response: list of search ids
//     app.get("/match/searches", async (req, res) => {
//         var profile_email = req.query.email
//         try {
//             var find_cursor = await user_collection.findOne({ email: profile_email })
//             if (!find_cursor) {
//                 res.status(404).json({ response: "User not found." })
//                 return
//             }
//             var temp_arry = await find_cursor.searches
//             console.log(temp_arry)
//             res.status(200).send(temp_arry)

//         }
//         catch (err) {
//             console.log(err)
//             res.status(400).send(err)
//         }
//     })



//     // Add New Active Search POST https://shopeer.com/match/searches
//     // Adds a new active search for suggested matches to User Database
//     // Body: User Id Token, location, distance, search_name, budget
//     // Response: search id


//     //  if the search param is empty, then create a new search, 
//     // if it's not empty, use it to find that search object and replace it with the search in the body
//     app.post("/match/searches", async (req, res) => {
//         var profile_email = req.query.email
//         var search_id = req.query.search
//         var newsearchname = req.body.search.search_name
//         var search_object = create_search_object(req.body.search)
//         // console.log(search_object)
//         try {
//             var find_cursor = await user_collection.findOne({ email: profile_email })
//             if (find_cursor == null) {
//                 res.status(404).json({ response: 'User not found' })
//                 return

//             }
//             // console.log(find_cursor)
//             // res.status(200).send("ok")
//             // console.log(find_cursor.searches.length)

//             // if this email has no searches yet, push the search

//             if (find_cursor.searches.length == 0) {
//                 var debug_res = await user_collection.updateOne({ email: profile_email }, { $push: { searches: search_object } })
//                 // var find_cursor = await user_collection.findOne({ email: profile_email })
//                 res.status(201).json({ response: 'first search added!' });
//                 return
//             } else {

//                 // if this email has made searches, 
//                 // --- overwrite duplicate
//                 for (let i = 0; i < find_cursor.searches.length; i++) {

//                     // if the search already exists
//                     if (find_cursor.searches[i].search_name == newsearchname) {
//                         res.status(409).json({ response: 'search already exists' });
//                         return
//                     }

//                     // if we need to modify the search name, identify by search_id
//                     else if (find_cursor.searches[i].search_name == search_id) {
//                         await user_collection.updateOne({ email: profile_email }, { $pull: { searches: find_cursor.searches[i] } })
//                         await user_collection.updateOne({ email: profile_email }, { $push: { searches: search_object } })
//                         // console.log(find_cursor)
//                         console.log("Overwrote prev search")
//                         res.status(200).json({ response: 'overwrote prev search' });
//                         return
//                     }

//                     //  else {
//                     //     var debug_res = await user_collection.updateOne({ email: profile_email }, { $push: { searches: search_object } })
//                     //     // var find_cursor = await user_collection.findOne({ email: profile_email })
//                     //     console.log(debug_res)
//                     //     res.status(200).send("find_cursor")
//                     //     break
//                     // }
//                 }

//                 //await user_collection.updateOne({ email: profile_email }, { $push: { searches: search_object } })
//                 // var find_cursor = await user_collection.findOne({ email: profile_email })
//                 console.log(debug_res)
//                 res.status(201).json({ response: 'added new search' });
//                 return





//                 // --- deny duplicate and overwriting
//                 // for (let i = 0; i < find_cursor.searches.length; i++) {
//                 //     if (find_cursor.searches[i].search_name == search_object.search_name) {
//                 //         console.log("Search already added")
//                 //         res.status(200).send("Search already added")
//                 //     } else {
//                 //         var debug_res = await user_collection.updateOne({ email: profile_email }, { $push: { searches: search_object } })
//                 //         var find_cursor = await user_collection.findOne({ email: profile_email })
//                 //         res.status(200).send(find_cursor)
//                 //         break
//                 //     }
//                 // }
//             }
//         }
//         catch (err) {
//             console.log(err)
//             res.status(400).send(err)
//         }
//     })

//     function create_search_object(body) {
//         var ret_object = {
//             search_name: body.search_name,
//             activity: body.activity,
//             location: body.location,
//             max_range: body.max_range,
//             max_budget: body.max_budget
//         }
//         return ret_object
//     }


//     // Delete active search DELETE https://shopeer.com/match/searches?search_id=[id]
//     // Removes a current active search from active_searches to past_searches in User Database
//     // Param: search id
//     // Body: User Id Token
//     // Response: success/ fail
//     app.delete("/match/searches", async (req, res) => {
//         var profile_email = req.query.email
//         var search = req.query.search
//         try {
//             var find_cursor = await user_collection.findOne({ email: profile_email })
//             if (!find_cursor) {
//                 res.status(404).json({ response: "User not found." })
//                 return
//             }
//             console.log(search)
//             var no_match_flag = 0
//             for (let i = 0; i < find_cursor.searches.length; i++) {
//                 if (find_cursor.searches[i].search_name == search) {
//                     await user_collection.updateOne({ email: profile_email }, { $pull: { searches: { search_name: search } } })
//                     // var find_cursor = await user_collection.findOne({ email: profile_email })
//                     res.status(200).json({ response: 'removed search' })
//                     no_match_flag = 1
//                     return
//                 }
//             }
//             if (!no_match_flag) {
//                 // var debug_res = await user_collection.updateOne({email:profile_email},{ $pull: { searches: { $match: search } } } )
//                 //var find_cursor = await user_collection.findOne({ email: profile_email })
//                 console.log("Search not in existence")
//                 //res.status(200).send(find_cursor)
//                 res.status(404).json({ response: 'search not found' })
//             }
//         }
//         catch (err) {
//             console.log(err)
//             res.status(400).send(err)
//         }
//     })







//     return app
// }
