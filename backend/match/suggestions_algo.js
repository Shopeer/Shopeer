

var express = require("express")
const suggestions_algo_router = express.Router()
require('validator')

require('../config/app')


var user_collection = require('../config/mongodb_connection')

//const activities = ["groceries", "entertainment", "bulkbuy", "hiking", "restaurants", "fashion", "books"]


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
    // // console.log(req.query)
    try {
        // console.log(profile_email)
        var main_user_cursor = await user_collection.findOne({ email: profile_email })
        // console.log(main_user_cursor)
        if (main_user_cursor.searches.length === 0) {
            var arr = await recommend(main_user_cursor)
            res.status(200).send(arr)
            return
        }

        var email_score_pair_array = await get_scores(main_user_cursor)
        var user_object_array = []
        for (const pair of email_score_pair_array) {
            const obj = await user_collection.findOne({ email: pair.email });
            user_object_array.push(obj)
        }

        res.status(200).send(user_object_array)
        return
    }
    catch (err) {
        // console.log(err)
        res.status(400).send(err)
    }


})


// this function is for users with no searches 
// TODO It recommends users based on their match history
async function recommend(user) {

    var viable = await get_viable_matches(user)
    if (viable.length === 0) {
        // console.log("make some friends")
        return []
    }
    return viable


}

// this function excludes the user's peers, invites, and blocklist, as well as anyone who has blocked this user
async function get_viable_matches(user) {
    // var excluded_user_emails = user.peers.concat(user.invites, user.blocked)

    // var viable_matches = await (user_collection.find(
    //     {
    //         email: { $ne: user.email, $nin: excluded_user_emails },
    //         blocked: { $nin: [user.email] }
    //     }))
    //     .toArray()
    var viable_matches = await (user_collection.find({
        email: { $ne: user.email },
        peers: { $nin: [user.email] }
    }))
    .toArray()
    // // console.log(viable_matches)
    // console.log("excluded")
    // // console.log(excluded_user_emails)
    return viable_matches
}

async function get_scores(user) {
    // filter out user's peers, invitations, and blocklist. Filter out anyone who has blocked this user.
    //var blocked_by_emails = await (user_collection.find({ blocked: {$in: [user.email] }})).project( { email: 1, _id: 0 }).toArray()

    var user_scores = [] // array contains email/score pairs
    var viable_matches = await get_viable_matches(user)



    for (let i = 0; i < viable_matches.length; i++) {
        var potential_match = viable_matches[i]
        var user_score = 0
        // console.log("\n\n+++++++++++ EVALUATING POTENTIAL MATCH " + potential_match.email + " +++++++++++")

        for (let j = 0; j < user.searches.length; j++) {

            var this_search = user.searches[j]
            // console.log("\n---looking for similar searches to " + this_search.search_name)
            // the following aggregation returns the searches in potenial_match.searchlist 
            // that have the same activity as this_search
            // in the following array format:
            // [ {searches: {searchObject1} }, {searches: {searchObject2} } ]

            var search_matches = await (user_collection.aggregate([
                {
                    "$unwind": "$searches"
                },
                {
                    "$match": {
                        "searches.activity": { $in: this_search.activity },
                        "email": potential_match.email
                    }
                },
                {
                    "$project": {
                        "searches": 1,
                        _id: 0
                    }
                }
            ])).toArray()

            if (search_matches.length > 0) {
                // console.log("\n" + potential_match.email + " has also searched for one of " + this_search.activity)
                user_score += await get_search_similarity_score(this_search, search_matches)
            } else {
                // console.log("no similar searches found \n")
                continue
            }
        }
        // console.log("\n ++++++++ TOTAL SCORE IS " + user_score)
        user_scores.push({ "email": potential_match.email, "score": user_score })
        //user_scores = await getSuggestionScorePairs(user_scores, viable_matches)
    }

    user_scores.sort(function compareFn(a, b) {
        if (a.score > b.score ) {
            return -1;
        }
        if (a.score < b.score) {
            return 1;
        }
        // a must be equal to b
        return 0;
    })
    // console.log("sorted scores:")
    // console.log(user_scores)

    return user_scores
}

async function get_search_similarity_score(this_search, search_matches) {

    score = 0
    for (let i = 0; i < search_matches.length; i++) {
        var search_match = search_matches[i].searches
        // console.log("\nevaluating search similarity: " + this_search.search_name + " and " + search_match.search_name)

        var loc = await getLocationScore(this_search, search_match)
        if (loc == -1) {
            // location score is -1 if max_range is exceeded. 
            // In this case, this particular search no longer provides a potential match

            return 0
        }
        score += loc
        var budget = await getBudgetScore(this_search, search_match)
        score += budget

    }
    return score
}

// returns a score between 0 and 100 that linearly decreases as the absolute difference btwn budgets increases
// returns score 0 if the difference between budgets is greater than arbitrarily determined max_budget_difference
// returns score 100 if difference between budgets is 0
async function getBudgetScore(this_search, search_match) {
    var max_budget_difference = Math.max(100, this_search.max_budget)
    var difference = Math.abs(this_search.max_budget - search_match.max_budget)
    var score = Math.max(-100 / max_budget_difference * difference + 100, 0)
    // console.log("\n" + this_search.search_name + " budget: " + this_search.max_budget)
    // console.log(search_match.search_name + " budget: " + search_match.max_budget)
    // console.log("budget score: " + score)
    return score

}

// returns -1 if distance exceeds either user's max range.
// otherwise, returns a score between 0 and 100 that linearly decreases as distance increases
async function getLocationScore(this_search, search_match) {
    var distance = getDistanceFromLatLonInKm(this_search.location_lati, this_search.location_long, search_match.location_lati, search_match.location_long)
    // console.log("distance is " + distance)
    if (distance > this_search.max_range || distance > search_match.max_range) {
        // console.log("too far away!")
        return -1
    }
    var score = Math.max(-100 / this_search.max_range * distance + 100, 0)
    // console.log("\n max range is " + this_search.max_range)

    // console.log("location score: " + score)
    return score


}

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

