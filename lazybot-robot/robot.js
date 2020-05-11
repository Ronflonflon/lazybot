const io = require("socket.io-client"),
    ioMaster = io.connect("http://localhost:9090");

const botId = Math.floor(Math.random() * (999999 - 100000) + 100000);
const mineflayer = require('mineflayer');
const v = require('vec3');
const navigatePlugin = require('mineflayer-navigate')(mineflayer);
const botManager = require('./business/botManager');
const eventChat = require("./business/eventTchat");
const eventUpdateBot = require("./business/eventUpdateBot");

// LOCAL VARIABLES
let lastX = 0;
let lastZ = 0;
let lastY = 0;

// === BOT INITIALIZATION ===
let bot = botManager.connect(process.argv);
navigatePlugin(bot);
connectionMSMaster();

function connectionMSMaster() {
    console.log(botId);
    ioMaster.emit('connectBot', botId);
}

// === CHAT CONTROL ===
bot.on('chat', function (username, message) {
    eventChat.chatControl(bot, username, message);
});

// === EVENT UPDATE THE BOT
bot.on('health', function () {
    console.log("health");
    eventUpdateBot.updateBot(botId, bot, ioMaster);
});

bot.on('move', function () {
    if (Math.floor(bot.entity.position.x) !== lastX || Math.floor(bot.entity.position.y) !== lastY || Math.floor(bot.entity.position.z) !== lastZ) {
        console.log("move");
        eventUpdateBot.updateBot(botId, bot, ioMaster);
    }
    lastX = Math.floor(bot.entity.position.x);
    lastY = Math.floor(bot.entity.position.y);
    lastZ = Math.floor(bot.entity.position.z);
});

bot.on('playerCollect', function () {
    console.log("collect");
    eventUpdateBot.updateBot(botId, bot, ioMaster);
});

// === MASTER MS REQUESTS
ioMaster.on('getLoadMap', function (ray) {
    let blocks = botManager.loadChunkArround(bot, ray, 0, 0);
    ioMaster.emit("returnLoadMap", blocks);
});

ioMaster.on('test', function () {
    console.log("J'ai reçu le retour");
});

ioMaster.on('sendMessage', function (message) {
    bot.chat(message);
});

ioMaster.on('goToPos', function (x, y, z) {
    let positionToGo = v(x, y, z);
    bot.navigate.to(positionToGo);
});

ioMaster.on('connectionSuccess', function () {
    console.log("Connection effectuée !");
});

// === Events bot stops ===
ioMaster.on('quit', function () {
    exit()
});
process.on('SIGINT', function () {
    exit();
});
process.on('SIGUSR1', function () {
    exit();
});
process.on('SIGUSR2', function () {
    exit();
});

// Function bot stops
function exit() {
    process.exit();
}

// Function to execute when the bot stops
process.on('exit', function () {
    console.log("Event déco");
    ioMaster.emit("disconnectBot", botId);
});