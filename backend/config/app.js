var express = require("express")
const app = express()
app.use(express.json())

// User Module Router
const {user_profile_router, getUser} = require('../user/profile.js');
app.use('/user', user_profile_router)

const user_peers_router = require('../user/peers.js');
app.use('/user', user_peers_router)

// Match Module Router
const searches_router = require('../match/searches');
app.use('/match', searches_router)

const suggestions_algo_router = require('../match/suggestions_algo.js');
app.use('/match', suggestions_algo_router)

// Chat Module Router
const roomsRouter = require('../chat/room');
app.use('/chat/room', roomsRouter)

module.exports = app