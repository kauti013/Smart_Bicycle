 #include <LiquidCrystal.h>
LiquidCrystal lcd(12, 11, 5, 4, 3, 2);
 
int ldrPin = A0;
int ambientLight;
int redLed = 10;
char commandbuffer[100];
int mode=1;
int blight= 7;

void setup() {
  // put your setup code here, to run once:
 Serial.begin(9600);
 pinMode(10,OUTPUT);
   lcd.begin(16,2);
    lcd.print("Welcome!");
  lcd.clear();
    pinMode(blight,OUTPUT);
 digitalWrite(blight,HIGH);
 

}

void loop() {
  // put your main code here, to run repeatedly:
int i=0;
char commandbuffer[100];
strcpy(commandbuffer,"");

 
       if(Serial.available()>0) {
    delay(100);

    while ( Serial.available() >0 && i < 100) {
      commandbuffer[i++] = Serial.read();
    }
   // Serial.println(Serial.available());
    commandbuffer[i++] = '\0';
  }
  
  switch (atoi(commandbuffer)) {
    case 99:
     // Serial.println("IN INNER IF");
      mode=1;
      break;
      case 100:
     //delay(100);
     mode=0;
      digitalWrite(10, LOW);
      break;
      
      case 10:
 Serial.println("LEFT");
 lcd.setCursor(5,0);
     lcd.print("<--L");
    light();
 
break;
case 20:
 Serial.println("RIGHT");
 lcd.setCursor(5,0);
     lcd.print("R-->");
 light();
break;
case 30:
 Serial.println("SLIGHT LEFT");
 lcd.setCursor(5,0);
     lcd.print("<--SL");
  light();
break;
case 40:
 Serial.println("SLIGHT RIGHT");
 lcd.setCursor(5,0);
     lcd.print("SR-->");
    light();
break;
case 50:
 Serial.println("SHARP LEFT");
 lcd.setCursor(5,0);
     lcd.print("<--SHL");
    light();
break;
case 60:
 Serial.println("SHARP RIGHT");
 lcd.setCursor(5,0);
     lcd.print("SHR-->");
    light();
break;
case 70:
 Serial.println("UTURN");
 lcd.setCursor(4,0);
     lcd.print("U TURN");
   light();
break;
case 80:
 Serial.println("REACHED");
 lcd.setCursor(4,0);
     lcd.print("REACHED");
      for(int i=0;i<6;i++){digitalWrite(blight,HIGH);
     delay(300);
     // Serial.println("jere");
 digitalWrite(blight,LOW);
 delay(300);}

break;

case 1:
 Serial.println("CIRCLE");
 lcd.setCursor(6,0);
     lcd.print("C1");
   light();
break;
case 2:
 Serial.println("CIRCLE");
 lcd.setCursor(6,0);
     lcd.print("C2");
   light();
break;
case 3:
 Serial.println("CIRCLE");
 lcd.setCursor(6,0);
     lcd.print("C3");
 light();
break;
case 4:
 Serial.println("CIRCLE");
 lcd.setCursor(6,0);
     lcd.print("C4");
    light();
break;
case 5:
 Serial.println("CIRCLE");
 lcd.setCursor(6,0);
     lcd.print("C5");
    light();
break;
case 6:
 Serial.println("CIRCLE");
 lcd.setCursor(6,0);
     lcd.print("C6");
   light();
break;
case 7:
 Serial.println("CIRCLE");
 lcd.setCursor(6,0);
     lcd.print("C7");
    light();
break;
case 8:
 Serial.println("CIRCLE");
 lcd.setCursor(6,0);
     lcd.print("C8");
      light();
break;



default:
ambientLight = analogRead(ldrPin);
 if (mode==1 && ambientLight>600){
        digitalWrite(10, HIGH);
      }
   
      else{
        digitalWrite(10, LOW);
      }
//delay(1000);
break;
  }
  
}
void light(){
for(int i=0;i<3;i++){digitalWrite(blight,HIGH);
     delay(300);
     // Serial.println("jere");
 digitalWrite(blight,LOW);
 delay(300);}
//commandbuffer[0]='\0';
//goto L1;
strcpy(commandbuffer,"21"); //It will go to default anyway
lcd.clear();
}

