package io.github.trytonvanmeer.libretrivia.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothShareService {

    private static final String titleUUID = "LibreTriviaShare";
    private static final UUID deviceUUID =
            UUID.fromString("e4fb6ebf-615c-462c-bca1-38c8b8761543");

    private final BluetoothAdapter bluetoothAdapter;
    private final Handler handlerBt;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int currState;
    private int newState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    public BluetoothShareService(Context context, Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        currState = STATE_NONE;
        handlerBt = handler;
    }

    public synchronized int getState() {
        return currState;
    }

    public synchronized void start() {
        // Cancel any incoming threads
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (acceptThread == null) {
            acceptThread = new AcceptThread(true);
            acceptThread.start();
        }

    }

    public synchronized void connect(BluetoothDevice device) {
        if (currState == STATE_CONNECTED) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(device);
        connectThread.start();
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        connectedThread = new ConnectedThread(socket, socketType);
        connectedThread.start();
    }

    public synchronized void stop() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        currState = STATE_NONE;
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread tempThread;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (currState != STATE_CONNECTED) return;
            tempThread = connectedThread;
        }
        // Perform the write unsynchronized
        tempThread.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = handlerBt.obtainMessage(5);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Unable to connect device");
        msg.setData(bundle);
        handlerBt.sendMessage(msg);

        currState = STATE_NONE;

        // Start the service over to restart listening mode
        BluetoothShareService.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = handlerBt.obtainMessage(5);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Device connection was lost");
        msg.setData(bundle);
        handlerBt.sendMessage(msg);

        currState = STATE_NONE;

        // Start the service over to restart listening mode
        BluetoothShareService.this.start();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket serverSocket;
        private String socketType;

        public AcceptThread(boolean secure) {
            BluetoothServerSocket tempSocket = null;
            socketType = secure ? "Secure" : "Insecure";

            // Create a new listening server socket
            try {
                tempSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(titleUUID,
                        deviceUUID);
            } catch (IOException e) {
                Log.e("BluetoothShareService", "Socket Type: " + socketType + "listen() failed", e);
            }
            serverSocket = tempSocket;
            currState = STATE_LISTEN;
        }

        public void run() {
            Log.d("BluetoothShareService", "Socket Type: " + socketType +
                    "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + socketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (currState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    Log.e("BluetoothShareService", "Socket Type: " + socketType + "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothShareService.this) {
                        switch (currState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(),
                                        socketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e("BluetoothShareService", "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i("BluetoothShareService", "END mAcceptThread, socket Type: " + socketType);

        }

        public void cancel() {
            Log.d("BluetoothShareService", "Socket Type" + socketType + "cancel " + this);
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e("BluetoothShareService", "Socket Type" + socketType + "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;
        private String socketType;

        public ConnectThread(BluetoothDevice device) {
            bluetoothDevice = device;
            BluetoothSocket tempSocket = null;
            socketType = "Secure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tempSocket = device.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e("BluetoothShareService", "Socket Type: " + socketType + "create() failed", e);
            }
            bluetoothSocket = tempSocket;
            currState = STATE_CONNECTING;
        }

        public void run() {
            Log.i("BluetoothShareService", "BEGIN mConnectThread SocketType:" + socketType);
            setName("ConnectThread" + socketType);

            // Always cancel discovery because it will slow down a connection
            bluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                bluetoothSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    bluetoothSocket.close();
                } catch (IOException e2) {
                    Log.e("BluetoothShareService", "unable to close() " + socketType +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothShareService.this) {
                connectThread = null;
            }

            // Start the connected thread
            connected(bluetoothSocket, bluetoothDevice, socketType);
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e("BluetoothShareService", "close() of connect " + socketType + " socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d("BluetoothShareService", "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("BluetoothShareService", "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            currState = STATE_CONNECTED;
        }

        public void run() {
            Log.i("BluetoothShareService", "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (currState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    handlerBt.obtainMessage(2, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e("BluetoothShareService", "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                handlerBt.obtainMessage(3, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e("BluetoothShareService", "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("BluetoothShareService", "close() of connect socket failed", e);
            }
        }
    }

}
