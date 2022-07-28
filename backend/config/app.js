var express = require("express")
const app = express()
app.use(express.json())


app.get("/", (req, res) => {
    res.send("Hello world\n")
})

// User Module Router
const user_profile_router = require('../user/profile.js');
app.use('*', user_profile_router);
app.use('/user', user_profile_router)

const user_peers_router = require('../user/peers.js');
app.use('*', user_peers_router);
app.use('/user', user_peers_router)

// Match Module Router
const searches_router = require('../match/searches');
app.use('*', searches_router);
app.use('/match', searches_router)

const suggestions_algo_router = require('../match/suggestions_algo.js');
app.use('*', suggestions_algo_router);
app.use('/match', suggestions_algo_router)

// Chat Module Router
const roomsRouter = require('../chat/room');
app.use('/chat/room', roomsRouter)

const mssgRouter = require('../chat/message');
app.use('/chat/message', mssgRouter)

module.exports = app