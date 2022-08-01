var express = require("express")
const suggestions_algo_router = express.Router()
const validator = require('validator')
var user_collection = require('../config/mongodb_connection')


function getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2) {
    var R = 6371; // Radius of the earth in km
    var dLat = deg2rad(lat2 - lat1);  // deg2rad below
    var dLon = deg2rad(lon2 - lon1);
    var a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2)
        ;
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    var d = R * c; // Distance in km
    return d;
}

function deg2rad(deg) {
    return deg * (Math.PI / 180)
}


// Get Suggested Matches GET https://shopeer.com/match/suggestions?user_id=[id]
// Returns a list of peer_ids based on the active searches of the current user taking into account the blocked list (let the database do most of the filtering) AND executes the match and recommendation algorithm to compute the peer_ids list 
// Param: user id
// Body: User Id Token, location, activity, distance, budget
// Response: List of peer objects {peer_id, name, bio, profile_picture} / empty list on fail
suggestions_algo_router.get("/suggestions", async (req, res) => {
    var profile_email = req.query.email
    console.log(req.query)
    try {
        var main_user_cursor = await user_collection.findOne({ email: profile_email })
        var target_searches = main_user_cursor.searches

        var remaining_user_cursor = await user_collection.find({ email: { $ne: profile_email } })

        var remaining_user_array = await remaining_user_cursor.toArray()

        var match_list = []

        console.log("----------")
        for (let i = 0; i < remaining_user_array.length; i++) {
            var each_search_array = remaining_user_array[i].searches
            var each_email = remaining_user_array[i].email
            console.log(each_search_array)
            var each_score = 0;
            for (let j = 0; j < each_search_array.length; j++) {
                if (target_searches.activity == each_search_array[j].activity) {
                    each_score++
                }
            }
            match_list.push([each_email, each_score])
            console.log(each_score)
            console.log(match_list)
        }

        // Bubble sort
        for (let i = 0; i < match_list.length - 1; i++) {
            console.log(match_list[i][1])
            console.log(match_list[i + 1][1])
            if (match_list[i][1] < match_list[i + 1][1]) {
                temp = match_list[i + 1]
                match_list[i + 1] = match_list[i]
                match_list[i] = temp
            }
        }

        const array = match_list
        customSlice = array => array.slice(0, 1)
        result = array.map(customSlice);

        console.log(result)

        res.status(200).send(result)
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }


})

module.exports = suggestions_algo_router;

