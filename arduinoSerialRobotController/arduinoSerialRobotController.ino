// Use this code to test your motor with the Arduino board:

// if you need PWM, just use the PWM outputs on the Arduino
// and instead of digitalWrite, you should use the analogWrite command

// --------------------------------------------------------------------------- Motors
int motor_left[] = {2, 3};
int motor_right[] = {7, 8};

// --------------------------------------------------------------------------- Setup
void setup() {
  Serial.begin(9600);
  // Setup motors
  int i;
  for(i = 0; i < 2; i++){
    pinMode(motor_left[i], OUTPUT);
    pinMode(motor_right[i], OUTPUT);
  }
}

// --------------------------------------------------------------------------- Loop
void loop() { 
}

void serialEvent() {
  if (Serial.available()==1) {
    char motor = (char)Serial.read();

    if(motor == 'f') {
      drive_forward();
    }
    if(motor == 'b') {
      drive_backward();
    }
    if(motor == 'l') {
      turn_left();
    }
    if(motor == 'r') {
      turn_right();
    }
    if(motor == 's') {
      motor_stop();
    }
  }
}

// --------------------------------------------------------------------------- Drive

void motor_stop(){
  digitalWrite(motor_left[0], LOW); 
  digitalWrite(motor_left[1], LOW); 
  
  digitalWrite(motor_right[0], LOW); 
  digitalWrite(motor_right[1], LOW);
}

void drive_forward(){
  digitalWrite(motor_left[0], HIGH); 
  digitalWrite(motor_left[1], LOW); 
  
  digitalWrite(motor_right[0], HIGH); 
  digitalWrite(motor_right[1], LOW); 
}

void drive_backward(){
  digitalWrite(motor_left[0], LOW); 
  digitalWrite(motor_left[1], HIGH); 

  digitalWrite(motor_right[0], LOW); 
  digitalWrite(motor_right[1], HIGH); 
}

void turn_left(){
  digitalWrite(motor_left[0], LOW); 
  digitalWrite(motor_left[1], HIGH); 
  
  digitalWrite(motor_right[0], HIGH); 
  digitalWrite(motor_right[1], LOW);
}

void turn_right(){
  digitalWrite(motor_left[0], HIGH); 
  digitalWrite(motor_left[1], LOW); 
  
  digitalWrite(motor_right[0], LOW); 
  digitalWrite(motor_right[1], HIGH); 
}
