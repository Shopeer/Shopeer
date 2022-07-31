var express = require("express")
const app = express()
app.use(express.json())
const validator = require('validator')
var user_collection = require('../config/mongodb_connection')


/////////////// match requests //////////////
// Get Active searches GET https://shopeer.com/match/searches
// Gets a list of all active_searches under the user from User Database
// Body: User Id Token
// Response: list of search ids
app.get("/searches", async (req, res) => {
    var profile_email = req.query.email

    if (!validator.isEmail(profile_email)) {
        res.status(400).send("Error: Invalid email")
    } else {
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (!find_cursor) {
            res.status(404).json({ response: "User not found." })
            return
        }
        var search_arry = await find_cursor.searches
        res.status(200).send(search_arry)
    }
})



// Add New Active Search POST https://shopeer.com/match/searches
// Adds a new active search for suggested matches to User Database
// Body: User Id Token, location, distance, search_name, budget
// Response: search id


//  if the search param is empty, then create a new search, 
// if it's not empty, use it to find that search object and replace it with the search in the body
app.post("/searches", async (req, res) => {
    var profile_email = req.query.email

    if (!validator.isEmail(profile_email)) {
        res.status(400).send("Error: Invalid email")
    } else if (!error_check_search(req.body)) {
        res.status(400).send("Error: Bad fields")
    } else {
        var new_search_name = req.body.search_name
        var search_object = create_search_object(req.body)
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (find_cursor == null) {
            res.status(404).json({ response: 'User not found' })
        } else {

            if (find_cursor.searches.length == 0) {
                await user_collection.updateOne({ email: profile_email }, { $push: { searches: search_object } })
                find_cursor = await user_collection.findOne({ email: profile_email })
                res.status(201).json({ response: 'Added new search' });
            } else {
                for (let i = 0; i < find_cursor.searches.length; i++) {
                    if (find_cursor.searches[i].search_name == new_search_name) {
                        res.status(409).json({ response: 'Search already exists' });
                        break
                    }
                }
            }
        }
    }
})


app.put("/searches", async (req, res) => {
    var profile_email = req.query.email

    if (!validator.isEmail(profile_email)) {
        res.status(400).json("Error: Invalid Email")
    } else if (!error_check_search(req.body)) {
        res.status(400).json("Bad fields")
    } else {
        var old_search_name = req.query.search_name
        var new_search_name = req.body.search_name
        var search_object = create_search_object(req.body)
        var find_cursor = await user_collection.findOne({ email: profile_email })

        if (find_cursor == null) {
            res.status(404).json({ response: 'User not found' })
        } else if (old_search_name == new_search_name) {
            // updates search_name
            await user_collection.updateOne(
                { email: req.query.email, searches: { $elemMatch: { search_name: old_search_name } } },
                { $set: { "searches.$": search_object } }
            )
            res.status(200).json({ response: 'overwrote prev search' });
        } else {
            // updates body
            var duplicate = await user_collection.findOne(
                { email: req.query.email, searches: { $elemMatch: { search_name: new_search_name } } }
            )
            var check = await user_collection.findOne(
                { "email": req.query.email, "searches": { $elemMatch: { search_name: req.query.search_name } } }
            )
            if (!check) {
                res.status(404).json({ response: 'search not found' });
            } else if (!duplicate) {
                await user_collection.updateOne(
                    { "email": req.query.email, "searches": { $elemMatch: { search_name: req.query } } },
                    { $set: { "searches.$.search_name": new_search_name } }
                )
                res.status(200).json({ response: 'modified search_name' });
            } else {
                res.status(409).json({ response: 'this search_name already exists' });
            }
        }
    }
})

function create_search_object(body) {
    var ret_object = {
        search_name: body.search_name,
        activity: body.activity,
        location_name: body.location_name,
        location_long: body.location_long,
        location_lati: body.location_lati,
        max_range: body.max_range,
        max_budget: body.max_budget
    }
    return ret_object
}

function error_check_search(body) {
    console.log("i got there")
    if (body.search_name == null || body.activity == null || body.location_name == null || body.location_lati == null || body.location_long == null || body.max_range == null || body.max_budget == null) {
        return false
    }
    if (!validator.isAlphanumeric(body.search_name)) {
        return false
    }
    if (!validator.isAlpha(body.activity)) {
        console.log("i got here")
        return false
    }
    if (!validator.isAlpha(body.location_name)) {
        return false
    }
    if (!validator.isFloat(String(body.location_long))) {
        return false
    }
    if (!validator.isFloat(String(body.location_lati))) {
        return false
    }
    if (!validator.isInt(String(body.max_range))) {
        return false
    }
    if (!validator.isInt(String(body.max_budget))) {
        return false
    }
    return true
}


// Delete active search DELETE https://shopeer.com/match/searches?old_search_name=[id]
// Removes a current active search from active_searches to past_searches in User Database
// Param: search id
// Body: User Id Token
// Response: success/ fail
app.delete("/searches", async (req, res) => {
    var profile_email = req.query.email
    var search = req.query.search_name
    if (!validator.isEmail(profile_email)) {
        res.status(400).json("Error: Invalid Email")
    } else if (!validator.isAlphanumeric(search)) {
        res.status(400).json("Error: Search")
    } else {
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (!find_cursor) {
            res.status(404).json({ response: "User not found" })
            return
        }
        var no_match_flag = 0
        for (let i = 0; i < find_cursor.searches.length; i++) {
            if (find_cursor.searches[i].search_name == search) {
                await user_collection.updateOne({ email: profile_email }, { $pull: { searches: { search_name: search } } })
                res.status(200).json({ response: 'Removed search' })
                no_match_flag = 1
                break
            }
        }
        if (!no_match_flag) {
            res.status(404).json({ response: 'Search not found' })
        }
    }
})



module.exports = app