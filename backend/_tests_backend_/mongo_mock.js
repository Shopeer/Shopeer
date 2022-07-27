// var mongodb = require('mongo-mock');
// mongodb.max_delay = 0;//you can choose to NOT pretend to be async (default is 400ms)
// var MongoClient = mongodb.MongoClient;
// MongoClient.persist="mongo.js";//persist the data to disk

// // Connection URL
// var url = 'mongodb://localhost:27017/myproject';
// // Use connect method to connect to the Server
// MongoClient.connect(url, {}, function(err, client) {
//   var db = client.db();
//   // Get the documents collection
//   var collection = db.collection('documents');
//   // Insert some documents
//   var docs = [ {a : 1}, {a : 2}, {a : 3}];
//   collection.insertMany(docs, function(err, result) {
//     console.log('inserted',result);

//     collection.updateOne({ a : 2 }, { $set: { b : 1 } }, function(err, result) {
//       console.log('updated',result);

//       collection.findOne({a:2}, {b:1}, function(err, doc) {
//         console.log('foundOne', doc);

//         collection.removeOne({ a : 3 }, function(err, result) {
//           console.log('removed',result);

//           collection.find({}, {_id:-1}).toArray(function(err, docs) {
//             console.log('found',docs);
            
//             function cleanup(){            
//               var state = collection.toJSON();
//               // Do whatever you want. It's just an Array of Objects.
//               state.documents.push({a : 2});
              
//               // truncate
//               state.documents.length = 0;
              
//               // closing connection
//               db.close();
//             }
            
//             setTimeout(cleanup, 1000);
//           });
//         });
//       });
//     });
//   });
// });