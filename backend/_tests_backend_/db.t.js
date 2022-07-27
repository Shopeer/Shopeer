var mongodb = require('mongo-mock');
mongodb.max_delay = 0;//you can choose to NOT pretend to be async (default is 400ms)
var MongoClient = mongodb.MongoClient;
MongoClient.persist = "mongo.js";//persist the data to disk


const request = require('supertest');
const user_profile_router = require('./profile_mock')
const user_peers_router = require('./peers_mock');
const { exit } = require('process');

// Connection URL
var url = 'mongodb://localhost:27017/myproject';
// Use connect method to connect to the Server

describe('Tests for Profile Submodule', function () {

    var test = MongoClient.connect(url, {}, function (err, client) {
        var db = client.db();
        // Get the documents collection
        var collection = db.collection('documents');
        // Insert some documents
        var docs = [{ email: 'jimothy@gmail.com' }, { email: 'timothy@gmail.com' }];
        collection.insertMany(docs, function (err, result) {
        });
        console.log(collection.find())

        function cleanup(){            
            var state = collection.toJSON();
            // Do whatever you want. It's just an Array of Objects.
            state.documents.push({a : 2});
            
            // truncate
            state.documents.length = 0;
            
            // closing connection
            db.close();
          }
          
          setTimeout(cleanup, 1000);

          done()
          exit()
    })
    test.close()
    test.done()


    it('GET /profile', async function () {
        const response = await request(user_profile_router)
            .get('/profile')
            .query({ email: 'jimothy@gmail.com' })
            .set('Accept', 'application/json')
        expect(response.status).toEqual(200);
        expect(response.body.email).toEqual('jimothy@gmail.com');
        expect(response.headers["content-type"]).toMatch(/json/);
        done();
    });
});


// afterAll(async () => {
//     await new Promise(resolve => setTimeout(() => resolve(), 500)); // avoid jest open handle error
// });