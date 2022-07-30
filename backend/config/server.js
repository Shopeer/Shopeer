require('http');
var express = require("express")
// const { IPv4 } = require("ipaddr.js")
const app = express()


const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
const uri = "mongodb://admin:shopeer@20.230.148.126:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)

const user_collection = mongoClient.db("shopeer_database").collection("user_collection")

console.log({ MongoClient, ObjectId })

var server = null;

app.use(express.json())

app.get("/", (req, res) => {
    res.send("Hello world\n")
})
app.post("/", (req, res) => {
    res.send(req.body.text)
})

// Express Routers
const user_profile_router = require('../user/profile.js');
app.use('*', user_profile_router);
app.use('/user', user_profile_router)

const user_peers_router = require('../user/peers.js');
app.use('*', user_peers_router);
app.use('/user', user_peers_router)


// const searches_router = require('../match/searches');
// app.use('*', searches_router);
// app.use('/match', searches_router)

const suggestions_algo_router = require('../match/suggestions_algo.js');
app.use('*', suggestions_algo_router);
app.use('/match', suggestions_algo_router)

const roomsRouter = require('../chat/room');
app.use('/chat/room', roomsRouter)

const mssgRouter = require('../chat/message');
app.use('/chat/message', mssgRouter)

// // local vm
// const IP = '192.168.64.15';
// const PORT = 3000;

// azure vm
// const IP = "20.230.148.126";
const PORT = "8080";

// grace
// const IP = "206.116.227.202";
// const PORT = "3000";
//-------------------------------------------------------------------------------
//-------------------------------------------------------------------------------
//-------------------------------------------------------------------------------



/////////////// match requests //////////////
// Get Active searches GET https://shopeer.com/match/searches
// Gets a list of all active_searches under the user from User Database
// Body: User Id Token
// Response: list of search ids
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



// Add New Active Search POST https://shopeer.com/match/searches
// Adds a new active search for suggested matches to User Database
// Body: User Id Token, location, distance, search_name, budget
// Response: search id


//  if the search param is empty, then create a new search, 
// if it's not empty, use it to find that search object and replace it with the search in the body
app.post("/match/searches", async (req, res) => {
    var profile_email = req.query.email
    // var search_id = req.query.search
    var newsearchname = req.body.search.search_name
    var search_object = create_search_object(req.body.search)
    // console.log(search_object)
    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (find_cursor == null) {
            res.status(404).json({ response: 'User not found' })
            return
        }
        // if this email has no searches yet, push the search
        if (find_cursor.searches.length == 0) {
            var debug_res = await user_collection.updateOne({ email: profile_email }, { $push: { searches: search_object } })
            // var find_cursor = await user_collection.findOne({ email: profile_email })
            res.status(201).json({ response: 'first search added!' });
            return
        }
        for (let i = 0; i < find_cursor.searches.length; i++) {
            if (find_cursor.searches[i].search_name == newsearchname) {
                res.status(409).json({ response: 'search already exists' });
                return
            }
        }
        await user_collection.updateOne({ email: profile_email }, { $push: { searches: search_object } })
        find_cursor = await user_collection.findOne({ email: profile_email })
        console.log(debug_res)
        res.status(201).json({ response: 'added new search' });
        return
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

app.put("/match/searches", async (req, res) => {
    var profile_email = req.query.email
    var search_id = req.query.search
    var newsearchname = req.body.search.search_name
    var search_object = create_search_object(req.body.search)
    // console.log(search_object)
    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (find_cursor == null) {
            res.status(404).json({ response: 'User not found' })
            return
        }

        if (search_id == newsearchname) {
            // if we wish to modify the body:
            await user_collection.updateOne(
                {email: req.query.email, searches: {$elemMatch: {search_name: search_id}}},
                {$set: {"searches.$": search_object }}
            )
            res.status(200).json({ response: 'overwrote prev search' });
            return
        } else {
            // if we wish to modify the search name:
            var duplicate = await user_collection.findOne(
                {email: req.query.email, searches: {$elemMatch: {search_name: newsearchname}}}
            )
            var check = await user_collection.findOne(
                {"email": req.query.email,"searches": {$elemMatch: {search_name: req.query.search}}}
            )
            if (!check) {
                res.status(404).json({ response: 'search not found' });
                return
            }
            if (!duplicate) {
                await user_collection.updateOne(
                    {"email": req.query.email,"searches": {$elemMatch: {search_name: req.query.search}}},
                    {$set: {"searches.$.search_name": newsearchname}}
                )
                res.status(200).json({ response: 'modified search_name' });
                return

            } else {
                console.log(duplicate)
                console.log("above is duplicate")   
                res.status(409).json({ response: 'this search_name already exists' });
            }
        }
    }
    
    catch (err) {
        console.log(err)
        res.status(400).send(err)
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


// Delete active search DELETE https://shopeer.com/match/searches?search_id=[id]
// Removes a current active search from active_searches to past_searches in User Database
// Param: search id
// Body: User Id Token
// Response: success/ fail
app.delete("/match/searches", async (req, res) => {
    var profile_email = req.query.email
    var search = req.query.search_name
    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (!find_cursor) {
            res.status(404).json({response: "User not found."})
            return
        }

        // if query null, delete all
        if (profile_email==null) {
            for (let i = 0; i < find_cursor.searches.length; i++) {
                if (find_cursor.searches[i].search_name == search) {
                    await user_collection.updateOne({ email: profile_email }, { $pull: { searches: {search_name: search} } })
                    // var find_cursor = await user_collection.findOne({ email: profile_email })
                    res.status(200).json({ response: 'removed search' })
                    no_match_flag = 1
                }
            }
            return

        }
        console.log(search)
        var no_match_flag = 0
        for (let i = 0; i < find_cursor.searches.length; i++) {
            if (find_cursor.searches[i].search_name == search) {
                await user_collection.updateOne({ email: profile_email }, { $pull: { searches: {search_name: search} } })
                // var find_cursor = await user_collection.findOne({ email: profile_email })
                res.status(200).json({ response: 'removed search' })
                no_match_flag = 1
                return
            }
        }
        if (!no_match_flag) {
            // var debug_res = await user_collection.updateOne({email:profile_email},{ $pull: { searches: { $match: search } } } )
            //var find_cursor = await user_collection.findOne({ email: profile_email })
            console.log("Search not in existence")
            //res.status(200).send(find_cursor)
            res.status(404).json({ response: 'search not found' })
        }
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})






async function run() {
  try {
      await mongoClient.connect()  // waits for sync op to finish
      console.log("Successfully connected to the database")
      // var server = app.listen(8081, '0.0.0.0', function () {
      server = app.listen(PORT, function () {
          var host = server.address().address
          var port = server.address().port
          console.log("Example app running at http://%s:%s", host, port)
      })
  }
  catch (err) {
      console.log(err)
      await mongoClient.close()
  }
}

run()