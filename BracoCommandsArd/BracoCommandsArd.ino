#include "SoftwareSerial.h"
#include <Servo.h>

SoftwareSerial bluetooth(2, 3); //TX, RX (Bluetooth)
String incomingByte;      
Servo servoX;
Servo servoY;
Servo servoZ;
Servo garra;

void setup() {
  bluetooth.begin(9600);

  servoX.attach(9);
  servoY.attach(8);
  servoZ.attach(7);
  garra.attach(6);
}

void loop() {
  if (bluetooth.available() > 0) {
    
    while(bluetooth.available() > 0){
      incomingByte += bluetooth.read();
    }
    
    if (incomingByte == "DIREITA") {
      servoX.write(20);
      delay(200);
    }

    if (incomingByte == "ESQUERDA") {
      servoX.write(-20);
      delay(200);
    }
    if (incomingByte == "CIMA"){
      servoY.write(20);
      delay(200);
    }
    if (incomingByte == "BAIXO"){
      servoY.write(-20);
      delay(200);
    }
    if (incomingByte == "FRENTE"){
      servoZ.write(20);
      delay(200);
    }
    if (incomingByte == "TRAZ"){
      servoZ.write(-20);
      delay(200);
    }
    if (incomingByte == "FECHAR"){
      garra.write(20);
      delay(200);
    }
    if (incomingByte == "ABRIR"){
      garra.write(-20);
      delay(200);
    }
  }
}
