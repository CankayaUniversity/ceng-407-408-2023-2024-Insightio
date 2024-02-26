import { app, shell, ipcMain, BrowserWindow } from 'electron'
import path, { join } from 'path'
import { electronApp, optimizer, is } from '@electron-toolkit/utils'
import { spawn } from 'child_process'
import http from 'http'
import fs from 'fs';

const ffmpegPath = path.join(__dirname, '../../bin/ffmpeg.exe');
const ffServer = http.createServer();
const ffServerPort = 9292
let dataMessageSent = false
let ffmpegProcess

function ffmpegExists() {
  return fs.existsSync(ffmpegPath);
}

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


  ipcMain.on('start-rtsp-stream', (event, rtspUrl) => {
    if (!ffmpegExists()) {
      console.error('FFmpeg binary does not exist at the specified path.');
      event.sender.send('ffmpeg-error', 'FFmpeg not found');
      return;
    }

    if (ffmpegProcess) {
      stopFFMPEG()
    }

    try {
      ffmpegProcess = spawn(ffmpegPath, [
        '-i', rtspUrl,
        '-f', 'image2pipe',
        '-vf', 'fps=30,scale=620:-1',
        '-codec:v', 'mjpeg',
        '-q:v', '1',
        '-',
      ]);

      ffmpegProcess.stderr.on('data', (data) => {
        if (!dataMessageSent) {
          mainWindow.webContents.send('rtsp-feed-started');
          dataMessageSent = true
        }
        console.error(`stderr: ${data}`);
      });

      ffmpegProcess.on('close', (code) => {
        console.log(`FFmpeg process exited with code ${code}`);
        ffmpegProcess = null;
      });
    } catch (error) {
      console.error('Error starting FFmpeg process:', error);
      event.sender.send('ffmpeg-error', 'Error starting FFmpeg process');
    }
  });

  ipcMain.on('stop-rtsp-stream', () => {
    stopFFMPEG();
  });

  ffServer.on('request', (req, res) => {
    if (!ffmpegProcess) {
      res.writeHead(503, { 'Content-Type': 'text/plain' });
      res.end('FFmpeg process not running');
      return;
    }

    // Set headers for MJPEG stream
    res.writeHead(200, {
      'Content-Type': 'multipart/x-mixed-replace; boundary=frame',
      'Connection': 'keep-alive',
    });

    ffmpegProcess.stdout.on('data', (data) => {
      res.write(`--frame\r\n`);
      res.write('Content-Type: image/jpeg\r\n\r\n');
      res.write(data);
      res.write('\r\n');
    });
  });
}

function stopFFMPEG() {
  if (ffmpegProcess) {
    ffmpegProcess.kill('SIGINT');
    ffmpegProcess = null;
    dataMessageSent = false;
    (async () => {
      await new Promise(resolve => setTimeout(resolve, 1000));
    })();
  }
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

  ffServer.listen(ffServerPort, '127.0.0.1', () => {
    console.log(`FFMPEG Server running on port ${ffServerPort}`);
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
