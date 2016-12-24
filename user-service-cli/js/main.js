//
// Sample usage:
// node js/main.js -a encodePassword test
//

var clientConfig = require('./config/brikar-client-config');
var getLog = require('./out/cli-log').getLog;
var process = require('process');

var BrikarClient = require('./service/brikar-client').BrikarClient;

var CLIENT;

function UserServiceClient(brikarClient, log) {
  this.brikarClient = brikarClient;
  this.log = log;
}

UserServiceClient.prototype.encodePassword = function UserServiceClient_encodePassword(password) {
  var log = this.log;
  var result = this.brikarClient.request('POST', '/password/v1/encode', password, 'text/plain');
  result.then((d) => {
    console.log(d.response);
  }, (e) => {
    log.error("Error while calling encodePassword", e);
  });
  return result;
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
        var password = process.argv[i + 2];
        CLIENT.encodePassword(password);
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
