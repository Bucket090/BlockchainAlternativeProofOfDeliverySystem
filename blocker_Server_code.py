#relay 
# minus pin 4 DC 5v
# plus pin 6 GND
# GPIO PIN

#Relay open port lock
#Relay middle port for power source (battery)

import bluetooth
import RPi.GPIO as GPIO
import _thread

#variables 
relay = 17 #pin11
turn_on = b'1'
turn_off = b'0'

# Server configurations variables
BluetoothMACAddress = bluetooth.read_local_bdaddr()[0]
port = 1
max_connection = 1
size = 1024
limit_request = 2 # might use it , the whole point is to limit the amount of time the courier could open the locker

#GPIO's setup 
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(relay,GPIO.OUT)

#handling the GPIO's output once a command has been received
def switchLock(message):    
    if message == True:
        GPIO.output(relay,GPIO.HIGH)
        print("Lock is off")
    elif message == False:
        GPIO.output(relay,GPIO.LOW)
        print("Lock is on")

        
#handling how the data is being received if a certain commands is sent it will turn on or off the lock accordingly
def on_new_client(client,client_info):
    while True:
        try:
            data = client.recv(size)
            print(data)
            if len(data) == 0:break
            elif data == turn_on:
                switchLock(True)
            elif data == turn_off:
                switchLock(False)
        except Exception:
            print("Client Disconnected", client_info[0])
            switchLock(False)
            client.close()
            break
    
#Instansiation of the Bluetooth server socket 
server_socket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
server_socket.bind((BluetoothMACAddress, port))
server_socket.listen(max_connection)
print("Server Started: " , BluetoothMACAddress)

#main program where the magic happens, server accepts only 1 connection at a time from a client
#Thread is started to handle the client's message and once the client disconnects the server connection will be kept alive 
while True:
    try:
        client_socket, client_info = server_socket.accept()
        print ("Accepted connection from ", client_info[0])
        _thread.start_new_thread(on_new_client,(client_socket,client_info))
    except Exception:
        print("Server Disconnected")
        server_socket.close()
        switchLock(False)
        break
