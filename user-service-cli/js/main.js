//
// Sample usage:
// $ node js/main.js -a encodePassword test
// (output) $2a$10$lEPPS.Z5WFW66eqFoi1WZ.N7eVNHhnEAgGGX52HG3pasLUlnp7mNe
//
// $ node js/main.js -a getAccountById 22
// (output)
//  { id: 22,
//    nickname: 'admin',
//    passwordHash: '$2a$10$W5YdtLrCN.3dH8hilF2queEvfJedIhzSEzszgcjJ8e/NrWBCURIUW',
//    contacts: [],
//    authorities: [ 'ROLE_ADMIN', 'ROLE_GENERIC_USER' ],
//    created: 1434783600000,
//    active: true }
//

var clientConfig = require('./config/brikar-client-config');
var getLog = require('./out/cli-log').getLog;
var process = require('process');

var BrikarClient = require('./service/brikar-client').BrikarClient;

var CLIENT;

function UserServiceClient(brikarClient, log) {
  this.brikarClient = brikarClient;
  this.log = log;
  this.consoleHandlerEnabled = true;
}

UserServiceClient.prototype._result = function UserServiceClient_result(funcName, result) {
  if (!this.consoleHandlerEnabled) {
    return result;
  }

  var log = this.log;
  result.then((d) => {
    console.log(d.response);
  }, (e) => {
    log.error("While calling", funcName, "- error:", e);
  });
  return result;
}

UserServiceClient.prototype.encodePassword = function UserServiceClient_encodePassword(password) {
  return this._result('encodePassword', this.brikarClient.request('POST', '/password/v1/encode', password, 'text/plain'));
}

UserServiceClient.prototype.getAccountById = function UserServiceClient_getAccountById(id) {
  return this._result('getAccountById', this.brikarClient.request('GET', '/user/v1/account/' + id));
}

function main() {
  var i;
  var traceEnabled = false;
  for (i = 0; i < process.argv.length; ++i) {
    if (process.argv[i] === '-v') {
      traceEnabled = true;
    }
  }

  var log = getLog({traceEnabled: traceEnabled, origin: 'main'});
  log.trace('Initializing...');

  var config = clientConfig.readConfigOrMakeDefault("user-service", getLog({origin: 'brikar-client-config'}));
  log.trace('Parsed config', config);

  var brikarClient = new BrikarClient(config, getLog({traceEnabled: traceEnabled, origin: 'brikar-client'}));
  CLIENT = new UserServiceClient(brikarClient, getLog({traceEnabled: traceEnabled, origin: 'user-service-client'}));

  for (i = 0; i < process.argv.length; ++i) {
    if (process.argv[i] === '-a') {
      var operation = process.argv[i + 1];
      if (operation == 'encodePassword') {
        CLIENT.encodePassword(process.argv[i + 2]);
      } else if (operation == 'getAccountById') {
        CLIENT.getAccountById(process.argv[i + 2]);
      } else {
        log.error("Unknown operation:", operation);
      }
    }
  }
}

main();

//
// Exports
//

exports.CLIENT = CLIENT;
