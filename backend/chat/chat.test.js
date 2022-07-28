const request = require('supertest');
const express = require('express');
const app = express()
// reuse original application?
const roomsRouter = require('../chat/room');
app.use('/chat/room', roomsRouter)

const mssgRouter = require('../chat/message');
app.use('/chat/message', mssgRouter)

describe("Send message scenario", () => {
    const invalidRoomId = "clearlyfakeid"
    const fakeRoomId = "62e2feb74ce5451dd12322a4"

    it('should return 404 for nonexisting room', async function () {
      // first try to delete the room, just in case
      await request(app).delete('/').query({email: nonexistentEmail })
      // attempt to get a nonexisting user's peerlist
      const response = await request(app).get('/user/peers').query({ email: nonexistentEmail }).set('Accept', 'application/json')
      
      expect(response.body).toEqual({"response":"User not found."});
      expect(response.status).toEqual(404);
    });
  
    it('should return a peerlist sucessfully', async function () {
      // attempt to get rob's peerlist, which should contain bob and tim
      const response = await request(app).get('/user/peers').query({ email: emails[0] }).set('Accept', 'application/json')
      
      expect(response.body.length).toEqual(2);
      expect(response.body[0].name).toEqual(names[1]);
      expect(response.body[0].email).toEqual(emails[1]);
      expect(response.body[1].name).toEqual(names[2]);
      expect(response.body[1].email).toEqual(emails[2]);
      expect(response.status).toEqual(200);
    });
  
    it('should return an empty peerlist successfully', async function () {
      // attempt to get Jim's peerlist
      const response = await request(app).get('/user/peers').query({ email: emails[3] }).set('Accept', 'application/json')
      
      expect(response.body.length).toEqual(0);
      expect(response.status).toEqual(200);
    });
  
  });