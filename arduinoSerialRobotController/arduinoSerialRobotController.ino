// Use this code to test your motor with the Arduino board:

// if you need PWM, just use the PWM outputs on the Arduino
// and instead of digitalWrite, you should use the analogWrite command

// --------------------------------------------------------------------------- Motors
int FORWARD = 0;
int BACK = 1;
byte STOP = 0;
byte FORWARD_THRESH = 127;
int motor_left[] = {5, 6};
int motor_right[] = {9, 10};

int FRONT_RIGHT = 2;
int FRONT_LEFT = 4;
int REAR_RIGHT = 12;
int REAR_LEFT = 13;

byte FRHIT = 0;
byte FLHIT = 1;
byte RRHIT = 2;
byte RLHIT = 3;

boolean sentFR = false;
boolean sentFL = false;
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

    pinMode(FRONT_RIGHT, INPUT);
    pinMode(FRONT_LEFT, INPUT);
    pinMode(REAR_RIGHT, INPUT);
    pinMode(REAR_LEFT, INPUT);
  }
  motor_stop();
}

// --------------------------------------------------------------------------- Loop
void loop() {
    sentFR = checkSensor(FRONT_RIGHT, FRHIT, sentFR);
    sentFL = checkSensor(FRONT_LEFT, FLHIT, sentFL);
    sentRR = checkSensor(REAR_RIGHT, RRHIT, sentRR);
    sentRL = checkSensor(REAR_LEFT, RLHIT, sentRL);
}

boolean checkSensor(int sensorPin, boolean messageId, boolean alert){
    if(digitalRead(sensorPin) == HIGH){
        if(alert){
            Serial.write(messageId);
            return false;
        }
    }
    else{
        return true;
    }
}

void serialEvent() {
  if (Serial.available()==2) {
    byte leftPower = Serial.read();
    byte rightPower = Serial.read();
    
    turn_left(leftPower);
    turn_right(rightPower);

//    if(motor == 'f') {
//      drive_forward();
//    }
//    if(motor == 'b') {
//      drive_backward();
//    }
//    if(motor == 'l') {
//      turn_left();
//    }
//    if(motor == 'r') {
//      turn_right();
//    }
//    if(motor == 's') {
//      motor_stop();
//    }
  }
}

// --------------------------------------------------------------------------- Drive

void motor_stop(){
  analogWrite(motor_left[0], STOP); 
  analogWrite(motor_left[1], STOP); 
  
  analogWrite(motor_right[0], STOP); 
  analogWrite(motor_right[1], STOP);
}

void drive_forward(){
  analogWrite(motor_left[0], HIGH); 
  analogWrite(motor_left[1], LOW); 
  
  analogWrite(motor_right[0], HIGH); 
  analogWrite(motor_right[1], LOW); 
}

void drive_backward(){
  analogWrite(motor_left[0], LOW); 
  analogWrite(motor_left[1], HIGH); 

  analogWrite(motor_right[0], LOW); 
  analogWrite(motor_right[1], HIGH); 
}

void turn_left(byte power){
  if(power > FORWARD_THRESH){
    power = 2 * (power - 127);
    analogWrite(motor_left[BACK], STOP); 
    analogWrite(motor_left[FORWARD], power); 
  }
  else{
    power = 2 * (127 - power);
    analogWrite(motor_left[FORWARD], STOP); 
    analogWrite(motor_left[BACK], power); 
  }
}

void turn_right(byte power){
  if(power > FORWARD_THRESH){
    power = 2 * (power - 127);
    analogWrite(motor_right[BACK], STOP); 
    analogWrite(motor_right[FORWARD], power); 
  }
  else{
    power = 2 * (127 - power);
    analogWrite(motor_right[FORWARD], STOP); 
    analogWrite(motor_right[BACK], power); 
  }
}
