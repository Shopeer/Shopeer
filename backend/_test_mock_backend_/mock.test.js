const app = {
  use: jest.fn(),
  listen: jest.fn()
}
jest.doMock('express', () => {
  return () => {
    return app
  }
})

test('should invoke express once', () => {
  expect(app.use).toHaveBeenCalledTimes(1)
})

// import { Express } from 'jest-express/lib/express';
// import { server } from '../src/server.js';

// let app;

// describe('Server', () => {
//   beforeEach(() => {
//     app = new Express();
//   });

//   afterEach(() => {
//     app.resetMocked();
//   });

//   test('should setup server', () => {
//     const options = {
//       port: 3000,
//     };

//     server(app, options);

//     expect(app.set).toBeCalledWith('port', options.port);
//   });
// });