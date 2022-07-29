require('http');
var express = require("express")
express()

const suggestions_algo_router = express.Router()

// const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
const {MongoClient} = require("mongodb")
const uri = "mongodb://admin:shopeer@20.230.148.126:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)

const user_collection = mongoClient.db("shopeer_database").collection("user_collection")

// Get Suggested Matches GET https://shopeer.com/match/suggestions?user_id=[id]
// Returns a list of peer_ids based on the active searches of the current user taking into account the blocked list (let the database do most of the filtering) AND executes the match and recommendation algorithm to compute the peer_ids list 
// Param: user id
// Body: User Id Token, location, activity, distance, budget
// Response: List of peer objects {peer_id, name, bio, profile_picture} / empty list on fail
suggestions_algo_router.get("/suggestions", async (req, res) => {
    var profile_email = req.query.email
    console.log(req.query)
    try {
        var main_user_cursor = await user_collection.findOne({email:profile_email})

        var target_searches = main_user_cursor.searches

        var remaining_user_cursor = await user_collection.find({email: { $ne: profile_email }})

        var remaining_user_array = await remaining_user_cursor.toArray()

        var match_list = []
        
        console.log("----------")
        for (let i = 0; i < remaining_user_array.length; i++){
            var user = await user_collection.findOne({email: remaining_user_array[i].email})
            // console.log("is this user valid?")
            // console.log(user)

            var each_search_array = remaining_user_array[i].searches
            var each_email = remaining_user_array[i].email
            console.log(each_search_array)
            var each_score = 0;
            for (let j = 0; j < each_search_array.length; j++){
                if (target_searches.activity == each_search_array[j].activity){
                    each_score++
                }
            }
            match_list.push([each_email, each_score])
            console.log(each_score)
            console.log(match_list)
        }

        // Bubble sort
        for (let i = 0; i < match_list.length-1; i++) {
            console.log(match_list[i][1])
            console.log(match_list[i+1][1])
            if (match_list[i][1] < match_list[i+1][1]) {
                temp = match_list[i+1]
                match_list[i+1] = match_list[i]
                match_list[i] = temp
            }
        }

        const array = match_list
        customSlice = array => array.slice(0, 1)
        result = array.map(customSlice);

        console.log(result)

        obj_arr = []
        for (let i = 0; i < result.length; i++) {
            var currEmail = result[i]
            // console.log("\nsearching for email:\n")
            // console.log(result[i])
            // console.log("\n")
            //var main_user_cursor = await user_collection.findOne({email:profile_email})
            var cursor = await user_collection.findOne({email: currEmail.toString()})
            if (cursor == null) {
                console.log("suggested email not found")
                continue
            } else {
                obj_arr.push(cursor)
                // console.log("cursor is")
                // console.log(cursor)
            }

        }
        res.status(200).send(obj_arr)

        // if (get_object_array_from_email_array(result).length > 0 ) {
        //     res.status(200).send(get_object_array_from_email_array(result))
        // } else {
        //     res.status(404).send("there may be invalid emails in the suggestion list")
        // }

        
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }


})

// function generate_best_matches(all_users_array) {
//     var best_match_list
//     var score

//     console.log(best_match_list)


//     return best_match_list
// }


// search pref
// location (long, lat)
// range (max km)
// activity [most prefer, ... , least prefer]
// budget (max)


// Write mocking tests
// User module
// Match module


// Front end test
// Browse users
// Manage peers
// Manage blocking

// TODO Fix repeated code from peers.js
// async function get_object_array_from_email_array(email_array) {
//     // console.log(email_array)
//     // var array = []
//     // for (let i = 0; i < email_array.length; i++) {
//     //     var return_cursor = await user_collection.findOne({ email: email_array[i] })
//     //     if (!return_cursor) {
//     //         throw "Error: Invalid email"
//     //     }
//     //     // console.log(return_cursor)
//     //     array.push(return_cursor)
//     // }

//     var return_arr = await user_collection.find({ email: { $in: email_array } }).toArray()
//     console.log("return arr is ")
//     console.log(return_arr)
//     if (!return_arr) {
//         throw "Error: invalid email"
//     }
//     return return_arr
// }


module.exports = suggestions_algo_router;

