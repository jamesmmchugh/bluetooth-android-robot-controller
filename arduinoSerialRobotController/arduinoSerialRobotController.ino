
// --------------------------------------------------------------------------- Motors
int FORWARD = 0;
int BACK = 1;
byte STOP = 0;
int motor_left[] = {5, 6};
int motor_right[] = {9, 10};
// --------------------------------------------------------------------------- Sensors
int FRONT_RIGHT = 2;
int FRONT_LEFT = 4;
int FRONT_CENTER = 7;
int REAR_RIGHT = 12;
int REAR_LEFT = 13;

byte FRHIT = 1;
byte FLHIT = 2;
byte FCHIT = 4;
byte RRHIT = 8;
byte RLHIT = 16;

boolean sentFR = false;
boolean sentFL = false;
boolean sentFC = false;
boolean sentRR = false;
boolean sentRL = false;

// --------------------------------------------------------------------------- Setup
void setup() {
  Serial.begin(9600);
  // Setup motors
  int i;
  for(i = 0; i < 2; i++){
    pinMode(motor_left[i], OUTPUT);
    pinMode(motor_right[i], OUTPUT);
  }
    pinMode(FRONT_RIGHT, INPUT);
    pinMode(FRONT_LEFT, INPUT);
    pinMode(FRONT_CENTER, INPUT);
    pinMode(REAR_RIGHT, INPUT);
    pinMode(REAR_LEFT, INPUT);
  
  motor_stop();
}

// --------------------------------------------------------------------------- Loop
void loop() {
  byte hit = 0;
    sentFR = checkSensor(FRONT_RIGHT, FRHIT, sentFR);
    sentFL = checkSensor(FRONT_LEFT, FLHIT, sentFL);
    sentRR = checkSensor(REAR_RIGHT, RRHIT, sentRR);
    sentRL = checkSensor(REAR_LEFT, RLHIT, sentRL);
    sentFC = checkSensor(FRONT_CENTER, FCHIT, sentFC);
}

boolean checkSensor(int sensorPin, boolean messageId, boolean alert){
    if(digitalRead(sensorPin) == HIGH){
        if(alert){
            Serial.println(messageId);
            return false;
        }
    }
    else{
        return true;
    }
}

void serialEvent() {
  if (Serial.available()==4) {
    byte leftDirection = Serial.read();
    byte leftPower = Serial.read();
    byte rightDirection = Serial.read();
    byte rightPower = Serial.read();
    
    Serial.print("Got "); 
    Serial.print(leftDirection);
    Serial.print(": ");
    Serial.print(leftPower);
    Serial.print(", ");
    Serial.print(rightDirection);
    Serial.print(": ");
    Serial.print(rightPower);
    Serial.println();
    
    turn(leftDirection, leftPower, motor_left);
    turn(rightDirection, rightPower, motor_right);
  }
}

// --------------------------------------------------------------------------- Drive

void motor_stop(){
  analogWrite(motor_left[0], STOP); 
  analogWrite(motor_left[1], STOP); 
  
  analogWrite(motor_right[0], STOP); 
  analogWrite(motor_right[1], STOP);
}

void turn(byte dir, byte power, int motors[]){
  power = power * 2;
  if(dir >= 1){//forward
    analogWrite(motors[BACK], STOP); 
    analogWrite(motors[FORWARD], power); 
    Serial.println("forwards: ");
    Serial.println(power);
  }
  else{//backwards
    analogWrite(motors[FORWARD], STOP); 
    analogWrite(motors[BACK], power); 
    Serial.print("backwards: ");
    Serial.println(power);
  }
}


