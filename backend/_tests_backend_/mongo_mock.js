
// var mongodb = require('mongo-mock');
// mongodb.max_delay = 0;//you can choose to NOT pretend to be async (default is 400ms)
// var MongoClient = mongodb.MongoClient;
// MongoClient.persist="mongo.js";//persist the data to disk

// // Connection URL
// var url = 'mongodb://localhost:27017/myproject';


// // Use connect method to connect to the Server
// MongoClient.connect(url, {}, function (err, client) {
//     var db = client.db('shopeer_database');
//     // Get the documents collection
//     var collection = db.collection('user_collection');
//     // Insert some documents
//     var docs = [{ a: 1 }, { a: 2 }, { a: 3 }];
//     collection.insertMany(docs, function (err, result) {
//         console.log('inserted', result);
//     });
// });

// var global_collection


// MongoClient.connect(url, {}, function (err, client) {
//     var db = client.db('shopeer_database');
//     // Get the documents collection
//     var collection = db.collection('user_collection');

//     var docs = [{ b: 1 }, { b: 2 }, { b: 3 }];
//     collection.insertMany(docs, function (err, result) {
//         console.log('inserted', result);
//     });
//     console.log()
//     var global_collection = collection

// });

// global_collection.find({})


// exports.MongoClient = MongoClient