const request = require('supertest');
const assert = require('assert');
const express = require('express');


const app = require('./express')

// await syntax
describe('GET /user', function() {
  it('responds with json', async function() {
    const response = await request(app)
      .get('/user')
      .set('Accept', 'application/json')
    expect(response.headers["content-type"]).toMatch(/json/);
    expect(response.status).toEqual(200);
    expect(response.body.name).toEqual('john');
  });
});


// describe('GET /user', function() {
//   it('responds with json', function(done) {
//     return request(app)
//       .get('/user')
//       .set('Accept', 'application/json')
//       .expect('Content-Type', /json/)
//       .expect(200)
//       .then(response => {
//           assert(response.body.name, 'john')
//           done();
//       })
//       .catch(err => done(err))
//   });
// });

// describe('POST /user', function() {
//   it('user.name should be an case-insensitive match for "john"', function(done) {
//     request(app)
//       .post('/user')
//       .send('name=john') // x-www-form-urlencoded upload
//       .set('Accept', 'application/json')
//       .expect(function(res) {
//         res.body.id = 'some fixed id';
//         res.body.name = res.body.name.toLowerCase();
//       })
//       .expect(200, {
//         id: 'some fixed id',
//         name: 'john'
//       }, done);
//   });
// });


// describe('POST /user', function() {
//   it('responds with json', function(done) {
//     request(app)
//       .post('/users')
//       .send({name: 'john'})
//       .set('Accept', 'application/json')
//       .expect('Content-Type', /json/)
//       .expect(200)
//       .end(function(err, res) {
//         if (err) return done(err);
//         return done();
//       });
//   });
// });


// describe('GET /user', function() {
//   it('responds with json', function(done) {
//     request(app)
//       .get('/user')
//       .set('Accept', 'application/json')
//       .expect('Content-Type', /json/)
//       .expect(200, done);
//   });
// });


// it("test1", (done) => {
//   request(app)
//   .get('/user')
//   .expect('Content-Type', /json/)
//   .expect('Content-Length', '15')
//   .expect(200)
//   .end(function(err, res) {
//     if (err) throw err;
//   })
//   .end(done);
// })

// it("test2", () => {
//   request(app)
//   .get('/user_another')
//   .expect('Content-Type', /json/)
//   .expect('Content-Length', '14')
//   .expect(200)
//   .end(function(err, res) {
//     expect(res.body.name).toEqual('bob')
//     // expect(res.body.name).toEqual('bobb')
//     if (err) throw err;
//   });
// })