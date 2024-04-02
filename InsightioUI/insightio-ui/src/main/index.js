import { app, shell, ipcMain, BrowserWindow } from 'electron'
import path, { join } from 'path'
import { electronApp, optimizer, is } from '@electron-toolkit/utils'
import fs from 'fs';

const http = require('http');
const { spawn } = require('child_process');
let streams = {};

const PORT = 5175;

const ffmpegPath = process.env.NODE_ENV === 'development'
  ? path.join(__dirname, '../../bin/ffmpeg.exe')
  : path.join(process.resourcesPath, 'bin/ffmpeg.exe');

// Check if ffmpeg.exe exists
const ffmpegExists = fs.existsSync(ffmpegPath);

function generateUniqueId() {
  return Math.random().toString(36).substring(2, 12);
}

function handleStreamRequest(req, res, streamId) {
  if (!streams[streamId] || !ffmpegExists) {
    res.writeHead(404);
    res.end();
    return;
  }

  const streamUrl = streams[streamId].url;
  const ffmpegProcess = spawn(ffmpegPath, [
    '-i', streamUrl,
    '-c:v', 'libx264',
    '-preset', 'veryfast',
    '-tune', 'zerolatency',
    '-c:a', 'aac',
    '-f', 'mp4',
    '-movflags', 'frag_keyframe+empty_moov',
    '-'
  ]);

  streams[streamId].process = ffmpegProcess;
  ffmpegProcess.stdout.pipe(res);
  ffmpegProcess.stderr.on('data', data => console.error(data.toString()));

  req.on('close', () => {
    ffmpegProcess.kill('SIGKILL');
  });
}

const server = http.createServer((req, res) => {
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:5173');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
  res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');
  res.setHeader('Access-Control-Allow-Credentials', true);

  const streamId = req.url.substring(1); // Assuming the URL is the stream path
  if (streams[streamId].url) {
    handleStreamRequest(req, res, streamId);
  } else {
    res.writeHead(404);
    res.end();
  }
});

function createWindow() {
  // Create the browser window.
  const mainWindow = new BrowserWindow({
    width: 1315,
    height: 800,
    show: false,
    frame: false,
    transparent: false,
    resizable: false,
    autoHideMenuBar: true,
    icon: path.join(__dirname, '../../build/logo.ico'),
    webPreferences: {
      preload: join(__dirname, '../preload/index.js'),
      sandbox: false
    }
  })

  mainWindow.on('ready-to-show', () => {
    mainWindow.show()
  })

  mainWindow.webContents.setWindowOpenHandler((details) => {
    shell.openExternal(details.url)
    return { action: 'deny' }
  })

  // HMR for renderer base on electron-vite cli.
  // Load the remote URL for development or the local html file for production.
  if (is.dev && process.env['ELECTRON_RENDERER_URL']) {
    mainWindow.loadURL(process.env['ELECTRON_RENDERER_URL'])
  } else {
    mainWindow.loadFile(join(__dirname, '../renderer/index.html'))
  }

  // Expose the window control functions to the renderer process
  ipcMain.on('minimize-window', () => {
    mainWindow.minimize();
  });

  ipcMain.on('close-window', () => {
    mainWindow.close();
  });

  ipcMain.on('start-stream', (event, streamUrl) => {
    if (!ffmpegExists) {
      console.error('FFMPEG binary not found.');
      event.reply('ffmpeg-error', 'FFMPEG binary not found.');
      return;
    }

    const streamId = generateUniqueId();
    streams[streamId] = { url: streamUrl, process: null };
    event.reply('stream-started', streamId);
  });

  ipcMain.on('stop-stream', (event, streamId) => {
    if (streams[streamId] && streams[streamId].process) {
      streams[streamId].process.kill('SIGKILL');
    }
    delete streams[streamId];
    event.reply('stream-stopped', streamId);
  });

  ipcMain.on('stop-all-streams', (event) => {
    Object.keys(streams).forEach(streamId => {
      if (streams[streamId].process) {
        streams[streamId].process.kill('SIGKILL');
      }
    });
    streams = {};
    event.reply('all-streams-stopped');
  });
}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.whenReady().then(() => {
  // Set app user model id for windows
  electronApp.setAppUserModelId('com.electron')

  // Default open or close DevTools by F12 in development
  // and ignore CommandOrControl + R in production.
  // see https://github.com/alex8088/electron-toolkit/tree/master/packages/utils
  app.on('browser-window-created', (_, window) => {
    optimizer.watchWindowShortcuts(window)
  })

  server.listen(PORT, () => {
    console.log(`Server is listening on port ${PORT}`);
  });

  createWindow()

  app.on('activate', function () {
    // On macOS it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    if (BrowserWindow.getAllWindows().length === 0) createWindow()
  })
})

// Quit when all windows are closed, except on macOS. There, it's common
// for applications and their menu bar to stay active until the user quits
// explicitly with Cmd + Q.
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

// In this file you can include the rest of your app"s specific main process
// code. You can also put them in separate files and require them here.
