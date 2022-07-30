const request = require('supertest');
const express = require('express');
var bodyParser = require('body-parser')
const app = express()
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: false }))
// reuse original application?
const roomsRouter = require('../chat/room');
app.use('/chat/room', roomsRouter)
const mssgRouter = require('../chat/message');
app.use('/chat/message', mssgRouter)
const user_profile_router = require('../user/profile.js');
app.use('*', user_profile_router);
app.use('/user', user_profile_router)
const user_peers_router = require('../user/peers.js');
app.use('*', user_peers_router);
app.use('/user', user_peers_router)

const invalidRoomId = "clearlyfakeid"
const fakeRoomId = "62e2feb74ce5451dd12322a4"
const nonexistingUser = "nonexistingchatuser@test.com"
const testUserA = {
  name: "Alice",
  email: "alice@test.com"
}
const testUserB = {
  name: "Bob",
  email: "bob@test.com"
}
const testUserC = {
  name: "Cathy",
  email: "cathy@test.com"
}
const testUserD = {
  name: "Dee",
  email: "dee@test.com"
}
const testMessageA = {
  email: testUserA.email, 
  text: "sup!", time: "3 PM"
}
const testMessageB = {
  email: testUserA.email, 
  text: "this is a unique message to test POST message!", time: "3 PM"
}
const testRoom = {
  name: "Alice/Bob", 
  peerslist: [testUserA.email, testUserB.email], 
  chathistory: [] 
}
const testRoom_2 = {
  name: "Alice/Cathy", 
  peerslist: [testUserA.email, testUserC.email], 
  chathistory: [] 
}

var roomIds = [];

beforeAll(() => {
  return initializeDatabase()
});

afterAll(() => {
  return resetDatabase()

})

async function initializeDatabase() {
  try {
    console.log("Initializing...")
    // register some users
    await request(app).post('/user/registration').query({ name: testUserA.name, email: testUserA.email})
    await request(app).post('/user/registration').query({ name: testUserB.name, email: testUserB.email})
    await request(app).post('/user/registration').query({ name: testUserC.name, email: testUserC.email})
    await request(app).post('/user/registration').query({ name: testUserD.name, email: testUserD.email})
    // make A and B peers, and A and C peers
    await request(app).post('/user/invitations').query({ email: testUserA.email, target_peer_email: testUserB.email})
    await request(app).post('/user/invitations').query({ email: testUserB.email, target_peer_email: testUserA.email})
    await request(app).post('/user/invitations').query({ email: testUserA.email, target_peer_email: testUserC.email})
    await request(app).post('/user/invitations').query({ email: testUserC.email, target_peer_email: testUserA.email})
    // create two chatrooms, one with A/C and one with A/B
    var room = await request(app).post('/chat/room').set('Accept', 'application/json').send( testRoom )
    var room_2 = await request(app).post('/chat/room').set('Accept', 'application/json').send( testRoom_2 )
    roomIds.push(room.body.insertedId)
    roomIds.push(room_2.body.insertedId)
    
  } catch (err) {
    console.log(err)
  }
}
async function resetDatabase() {
  try {
    // this deletes all rooms created during these tests
    for ( let i = 0; i < roomIds.length; i++ ) {
      await request(app).delete('/chat/room').query({ room_id: roomIds[i] })
      // console.log(doc.body)
    }
    // delete user registrations
    await request(app).delete('/user/registration').query({ email: testUserA.email })
    await request(app).delete('/user/registration').query({ email: testUserB.email })
    await request(app).delete('/user/registration').query({ email: testUserC.email })
    await request(app).delete('/user/registration').query({ email: testUserD.email })
    
  } catch (err) {
    console.log(err)
  }
}


describe("Send message scenario", () => {

    it('should return 404 for nonexisting room', async function () {
      // first try to delete the room, just in case
      await request(app).delete('/room').query({ room_id: fakeRoomId })
      
      const response = await request(app).post('/chat/message').query({ room_id: fakeRoomId }).set('Accept', 'application/json').send( testMessageA )
      expect(response.body).toEqual({"response": "Room not found."});
      expect(response.status).toEqual(404);
    });
  
    it('should return 400 for invalid room id', async function () {
        
        const response = await request(app).post('/chat/message').query({ room_id: invalidRoomId }).set('Accept', 'application/json').send( testMessageA )
        expect(response.body).toEqual({"response": "Invalid room id."});
        expect(response.status).toEqual(400);
    });
  
    it('should return 400 for missing fields', async function () {
      const missingFieldsMessage = {text: "there's no email or time!"}
      const response = await request(app).post('/chat/message').query({ room_id: roomIds[0] }).set('Accept', 'application/json').send( missingFieldsMessage )
      expect(response.body).toEqual({"response": "Missing fields."});
      expect(response.status).toEqual(400);
    });
    it('should return 400 if the sender is not a member of this room', async function () {
      const badMessage = {
        email: "yepanotherfakeemail@test.com",
        text: "sup!", 
        time: "3 PM"
      }
      const response = await request(app).post('/chat/message').query({ room_id: roomIds[0] }).set('Accept', 'application/json').send( badMessage)
      expect(response.body).toEqual({"response": "User is not a member of this room."});
      expect(response.status).toEqual(400);
    });

    it('should successfully post a message', async function () {
      // posts to room 2
      const response = await request(app).post('/chat/message').query({ room_id: roomIds[1] }).set('Accept', 'application/json').send( testMessageB )
      expect(response.status).toEqual(201);
      expect(response.body.modifiedCount).toEqual(1);
      // check that a message was posted
      var check = await request(app).get('/chat/room/history').query({room_id: [roomIds[1] ]})
      // filter the room history for the message that was just posted
      const messageFound = check.body.filter(mssg => (
        mssg.email == testMessageB.email
        && mssg.text == testMessageB.text
        && mssg.time == testMessageB.time) 
      )
      console.log("message found:")
      console.log(messageFound)
      expect (messageFound.length).toEqual(1)
      
    });
  
});

describe("Get all rooms scenario", () => {

  it('should return 404 for nonexisting user', async function () {
    // first try to delete the user, just in case
    await request(app).delete('/user/registration').query({ room_id: nonexistingUser })
    
    const response = await request(app).get('/chat/room/all').query({ email: nonexistingUser }).set('Accept', 'application/json')
    expect(response.body).toEqual({"response": "User not found."});
    expect(response.status).toEqual(404);
  });

  it('should successfully return all rooms containing a user', async function () {
    const response = await request(app).get('/chat/room/all').query({ email: testUserA.email }).set('Accept', 'application/json')
    expect(response.body.length).toEqual(2);
    expect(response.body[0].name).toEqual("Alice/Bob");
    expect(response.body[1].name).toEqual("Alice/Cathy");
    expect(response.body[0]._id).toEqual(roomIds[0]);
    expect(response.body[1]._id).toEqual(roomIds[1]);
    expect(response.status).toEqual(200);
  });

});

describe("Get chatroom history scenario", () => {

  it('should return 404 for nonexisting room', async function () {
    // first try to delete the room, just in case
    await request(app).delete('/room').query({ room_id: fakeRoomId })
    
    const response = await request(app).get('/chat/room/history').query({ room_id: fakeRoomId }).set('Accept', 'application/json')
    expect(response.body).toEqual({"response": "Room not found."});
    expect(response.status).toEqual(404);
  });

  it('should return 400 for invalid room id', async function () {
      
    const response = await request(app).get('/chat/room/history').query({ room_id: invalidRoomId }).set('Accept', 'application/json')
    expect(response.body).toEqual({"response": "Invalid room id."});
    expect(response.status).toEqual(400);
  });

  it('should get message history of an existing room', async function () {
    // first post three messages to the Alice/Bob room
    var newMssg1 = {email: testUserA.email, text: "hey, this is Alice!", time: "3:05"}
    var newMssg2 = {email: testUserA.email, text: "how are you, Bob?", time: "3:06 PM"}
    var newMssg3 = {email: testUserB.email, text: "hi Alice, I'm doing great!", time: "3:07 PM"}
    // we also need to update the message with the message id, which is generated by express.
    // first message
    await request(app).post('/chat/message')
      .query({ room_id: roomIds[0] }).set('Accept', 'application/json')
      .send( newMssg1 )
    // second message
    await request(app).post('/chat/message')
      .query({ room_id: roomIds[0]}).set('Accept', 'application/json')
      .send( newMssg2 )
    // third message
    await request(app).post('/chat/message')
      .query({ room_id: roomIds[0] }).set('Accept', 'application/json')
      .send( newMssg3 )
    
    var response = await request(app).get('/chat/room/history').query({room_id: [roomIds[0] ]})
    expect(response.body.length).toEqual(3);
    expect(response.body[0].email).toEqual(newMssg1.email)
    expect(response.body[1].email).toEqual(newMssg2.email)
    expect(response.body[2].email).toEqual(newMssg3.email)
    expect(response.body[0].text).toEqual(newMssg1.text)
    expect(response.body[1].text).toEqual(newMssg2.text)
    expect(response.body[2].text).toEqual(newMssg3.text)
    expect(response.body[0].time).toEqual(newMssg1.time)
    expect(response.body[1].time).toEqual(newMssg2.time)
    expect(response.body[2].time).toEqual(newMssg3.time)
    expect(response.status).toEqual(200);
  });

});

describe("Post new room scenario", () => {

  it('should return 400 for missing or null room name', async function () {
    var badRoom = {
      name: "", 
      peerslist: [testUserA.email, testUserB.email], 
      chathistory: [] 
    }
    var response = await request(app).post('/chat/room').set('Accept', 'application/json').send( badRoom )
    expect(response.body).toEqual({"response": "Missing room name."});
    expect(response.status).toEqual(400);

    badRoom = {
      peerslist: [testUserA.email, testUserB.email], 
      chathistory: [] 
    }
    response = await request(app).post('/chat/room').set('Accept', 'application/json').send( badRoom )
    expect(response.body).toEqual({"response": "Missing room name."});
    expect(response.status).toEqual(400);
  });

  it('should return 400 for missing, invalid, or null peerslist', async function () {
    // empty peerslist
    var badRoom = {
      name: "room with bad peerslist", 
      peerslist: [], 
      chathistory: [] 
    }
    var response = await request(app).post('/chat/room').set('Accept', 'application/json').send( badRoom )
    expect(response.body).toEqual({"response": "Invalid peerslist."});
    expect(response.status).toEqual(400);

    // one peer
    badRoom = {
      name: "room with bad peerslist", 
      peerslist: [testUserA.email], 
      chathistory: [] 
    }
    response = await request(app).post('/chat/room').set('Accept', 'application/json').send( badRoom )
    expect(response.body).toEqual({"response": "Invalid peerslist."});
    expect(response.status).toEqual(400);

    // null peerslist
    badRoom = {
      name: "room with bad peerslist", 
      chathistory: [] 
    }
    response = await request(app).post('/chat/room').set('Accept', 'application/json').send( badRoom )
    expect(response.body).toEqual({"response": "Invalid peerslist."});
    expect(response.status).toEqual(400);
  });

  it('should return 404 for nonexistent users in the peerlist', async function () {
    var fakeEmails = ["clearlyFakeEmail@test.com", "anotherFakeEmail@test.com"]
    var badRoom = {
      name: "these users don't exist!", 
      peerslist: fakeEmails, 
      chathistory: [] 
    }
    var response = await request(app).post('/chat/room').set('Accept', 'application/json').send( badRoom )
    expect(response.body).toEqual({"response": "Users not found."});
    expect(response.status).toEqual(404);

    badRoom = {
      name: "one user exists", 
      peerslist: [fakeEmails[0], testUserA.email], 
      chathistory: [] 
    }
    response = await request(app).post('/chat/room').set('Accept', 'application/json').send( badRoom )
    expect(response.body).toEqual({"response": "Users not found."});
    expect(response.status).toEqual(404);
  });

  it('should successfully post a room when the users are peers', async function () {
    // currently, bob and dee are not peers
    var badRoom = {
      name: "are they peers?", 
      peerslist: [testUserB.email, testUserD.email], 
      chathistory: [] 
    }
    var response = await request(app).post('/chat/room').set('Accept', 'application/json').send( badRoom )
    expect(response.body).toEqual({"response": "Users are not peers."});
    expect(response.status).toEqual(400);

    // now make them peers
    await request(app).post('/user/invitations').query({ email: testUserB.email, target_peer_email: testUserD.email})
    await request(app).post('/user/invitations').query({ email: testUserD.email, target_peer_email: testUserB.email})

    var room = {
      name: "they're peers!", 
      peerslist: [testUserB.email, testUserD.email], 
      chathistory: [] 
    }
    response = await request(app).post('/chat/room').set('Accept', 'application/json').send( room )
    expect(response.status).toEqual(201);
    // check that a room was created
    var roomId = response.body.insertedId
    roomIds.push(roomId)
    var createdCheck = await request(app).get('/chat/room/history').set('Accept', 'application/json').query( {room_id: roomId} )
    expect(createdCheck.status).toEqual(200)

  });

});

describe("Delete room scenario", () => {

  it('should return 404 for nonexisting room', async function () {
    // first try to delete the room, just in case
    await request(app).delete('/chat/room').query({ room_id: fakeRoomId })
    
    const response = await request(app).delete('/chat/room').query({ room_id: fakeRoomId }).set('Accept', 'application/json').send( testMessageA )
    expect(response.body).toEqual({"response": "Room not found."});
    expect(response.status).toEqual(404);
  });

  it('should return 400 for invalid room id', async function () {
      
    const response = await request(app).delete('/chat/room').query({ room_id: invalidRoomId }).set('Accept', 'application/json').send( testMessageA )
    expect(response.body).toEqual({"response": "Invalid room id."});
    expect(response.status).toEqual(400);
  });

  it('should successfully delete a room', async function () {
    // first create a room
    var room = await request(app).post('/chat/room').set('Accept', 'application/json').send( testRoom )
    // try to delete it
    const response = await request(app).delete('/chat/room').query({ room_id: room.body.insertedId }).set('Accept', 'application/json').send( testMessageA )
    expect(response.body).toEqual({"response": "Deleted room."});
    expect(response.status).toEqual(200);
    // double check that it was removed from the database
    var deletedCheck = await request(app).get('/chat/room/history').set('Accept', 'application/json').query( {room_id: room.body.insertedId} )
    expect(deletedCheck.status).toEqual(404)
  });

});

describe("Get chatroom summary scenario", () => {

  it('should return 404 for nonexisting room', async function () {
    // first try to delete the room, just in case
    await request(app).delete('/room').query({ room_id: fakeRoomId })
    
    const response = await request(app).get('/chat/room/summary').query({ room_id: fakeRoomId }).set('Accept', 'application/json')
    expect(response.body).toEqual({"response": "Room not found."});
    expect(response.status).toEqual(404);
  });

  it('should return 400 for invalid room id', async function () {
      
    const response = await request(app).get('/chat/room/summary').query({ room_id: invalidRoomId }).set('Accept', 'application/json')
    expect(response.body).toEqual({"response": "Invalid room id."});
    expect(response.status).toEqual(400);
  });

  it('should get message summary of an existing room', async function () {
    // message summary includes the name of the room and the latest message object sent.
    // insert a new message to the second room with Alice/Cathy:
    var newMssg1 = {email: testUserA.email, text: "a late message", time: "3:10"}
    await request(app).post('/chat/message')
      .query({ room_id: roomIds[1] }).set('Accept', 'application/json')
      .send( newMssg1 )
    
    var response = await request(app).get('/chat/room/summary').query({room_id: [roomIds[1] ]})
    expect(response.body.name).toEqual(testRoom_2.name)
    expect(response.body.lastmessage.email).toEqual(newMssg1.email)
    expect(response.body.lastmessage.text).toEqual(newMssg1.text)
    expect(response.body.lastmessage.time).toEqual(newMssg1.time)
    expect(response.status).toEqual(200);
  });

});