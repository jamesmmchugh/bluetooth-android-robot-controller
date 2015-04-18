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
int bumper = 7;

// --------------------------------------------------------------------------- Setup
void setup() {
  Serial.begin(9600);
  // Setup motors
  int i;
  for(i = 0; i < 2; i++){
    pinMode(motor_left[i], OUTPUT);
    pinMode(motor_right[i], OUTPUT);
  }
  motor_stop();
  pinMode(bumper, INPUT);
}

// --------------------------------------------------------------------------- Loop
void loop() { 
  if(digitalRead(bumper) == 1){
    motor_stop();
  }
}

void serialEvent() {
  if (Serial.available()==4) {
    byte leftDirection = Serial.read();
    byte leftPower = Serial.read() * 2;
    byte rightDirection = Serial.read();
    byte rightPower = Serial.read() * 2;
    
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
  if(dir <= 0){//forward
    //power = 2 * (power - 127);
    analogWrite(motors[BACK], STOP); 
    analogWrite(motors[FORWARD], power); 
  }
  else{//backwards
    //power = 2 * (127 - power);
    analogWrite(motors[FORWARD], STOP); 
    analogWrite(motors[BACK], power); 
  }
}


