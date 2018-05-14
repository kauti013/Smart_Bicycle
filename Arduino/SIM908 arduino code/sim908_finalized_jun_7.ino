
#include <SoftwareSerial.h>

int txPin = 13;
int rxPin = 12;
int modGSMPin = 11;
int modGPSPin = 10;
int pinDelay = 3000;
int8_t answer;
int8_t start_GPS();
char aux_str[30];
char phone_number[] = "+917259467276";
char latitude[15];
char longitude[15];
char altitude[6];
char date[16];
char time[7];
char satellites[3];
char speedOTG[10];
char course[10];
char frame[200];
char content[200];
char call_number[200];
int z=0;
int y=0;
uint8_t x = 0;
char message[180];
 
char pos;
 
char *p;
SoftwareSerial myGSM(rxPin, txPin);
void setup() {
  // put your setup code here, to run once:
  pinMode(txPin, OUTPUT);
  pinMode(rxPin, INPUT); // used to turn on and off GSM
  pinMode(modGSMPin, OUTPUT);
  pinMode(modGPSPin, OUTPUT);
  Serial.begin(115200);
  Serial.println("Initializing...");
  myGSM.begin(9600);
  powerOnGSM();
  Serial.println("Connecting..."); // checks for connection I believe
   //sendATcommand("AT+CLIP=1","OK",8000);
 //  delay(8000);
   
   L1: 
   z=0;
   while(z==0 && y==0){
    delay(500);
   Serial.println("infiniteloop");
   sendATcommand("","+CLIP",2000);
Serial.println(z);
   }
 // get_GPS();
//while(start_GPS()==0)
start_GPS();
  while ((sendATcommand("AT+CREG?", "+CREG: 0,1", 500) ||
          sendATcommand("AT+CREG?", "+CREG: 0,5", 500)) == 0);
  // 0 = not registered, not searching
  // 1 = registered on home network
  // 2 = not registered, but searching
  // 3 = registration denied
  // 4 = unknown (out of coverage?)
  // 5 = roaming, registered
  // 6 = SMS only, home network
  // 7 = SMS only, roaming
  // 8 = EMS
  // 9 = CSFB not preferred, home network
  // 10 = CSBFB not preferred, roaming  
  sendATcommand("AT+CREG=?", "OK",1000); // verifies network list
  sendATcommand("AT+GSN","OK",5000); // IEMI
  sendATcommand("ATX4","OK",5000);   // Listen for dialtone + busy signal when dialing
  delay(1000);
  
  //sendATcommand("AT+CLIP=1","OK",8000);
  delay(10000);
  Serial.println("CALL NUMBER IS");
  Serial.println(call_number);
 /*while(strcmp(call_number,"+917259467276")!=0)
 {
  Serial.println("WAITING FOR CALL");
 
  }*/
 
 if(strcmp(call_number,phone_number)==0){
  Serial.println("Comparison successful");
  get_GPS();
  //sendSMS("This is a test","+918884983673"); // Test SMS
  sendSMS(latitude,longitude,"+917259467276");
 hangUp();}
 else{hangUp();}
goto L1;
}
void loop() {
  // put your main code here, to run repeatedly:
  
}


void hangUp() {
  sendATcommand("ATH", "OK", 3000);
}
void sendSMS(char *message,char *msg,char *number) {
  Serial.println("Sending SMS...");
  sendATcommand("AT+CMGF=1", "OK", 1000); // prep SMS mode
  // sprintf(aux_str,"AT+CMGS=\"%S\"", number);  
  // answer = sendATcommand(aux_str,">", 2000);
  answer = sendATcommand("AT+CMGS=\"+917259467276\"",">",1000);
  if (answer == 1) {
    // myGSM.println(message);
   // myGSM.println("Test message.");
   myGSM.println(latitude);
   myGSM.println(longitude);
    myGSM.write(0x1A);
    answer = sendATcommand("", "OK", 20000);
    if (answer == 1) {
      Serial.println("Sent.");
      y=0;
    } else {
      Serial.println("Error");
    }
  } else {
    Serial.print("Error ");
    Serial.println(answer, DEC);
  }
}
void dial(char *number) {
  Serial.println("Dialing phone number...");
  sprintf(aux_str, "ATD%s;", number);
  sendATcommand(aux_str, "OK", 10000); // dial
}

void powerOnGSM() {
  uint8_t answer = 0;
  // check if the module is started
  answer = sendATcommand("AT", "OK", 5000);
  if (answer == 0) {
    // Send power on pulse
   digitalWrite(txPin,HIGH);
   pinMode(rxPin,LOW);
    digitalWrite(modGPSPin, HIGH);
    digitalWrite(modGSMPin, HIGH);
    delay(pinDelay);
    //digitalWrite(modPin, LOW);
    // wait for answer
     while (answer == 0 ) {
      answer = sendATcommand("AT", "OK", 2000);}
      
      Serial.println("BEFORE GPS POWER");
      // sendATcommand("AT+CGPSPWR=1", "OK", 2000);
    //sendATcommand("AT+CGPSRST=1", "OK", 2000);
Serial.println("WAITING FOR GPS");
    // waits for fix GPS
    //sendATcommand("AT+CGPSSTATUS?", "3D FIX", 5000);
        //sendATcommand("AT+CGPSSTATUS?", "2D FIX", 5000);
       
    }
  }



int8_t start_GPS(){

    unsigned long previous;

    previous = millis();
    // starts the GPS
    sendATcommand("AT+CGPSPWR=1", "OK", 2000);
    sendATcommand("AT+CGPSRST=1", "OK", 2000);

    // waits for fix GPS
    while(( (sendATcommand("AT+CGPSSTATUS?", "3D FIX", 50000) || 
        sendATcommand("AT+CGPSSTATUS?", "2D FIX", 50000)) != 0 ) && 
        ((millis() - previous) < 90000));

    if ((millis() - previous) < 90000)
    {
        return 1;
    }
    else
    {
        return 0;    
    }
    return answer;
}


int8_t get_GPS(){

    int8_t  answer;
   // int counter;
   unsigned long previous;
  answer = 0;
  //String content="";
    // First get the NMEA string
    // Clean the input buffer
    //Serial.flush();
 // while( myGSM.available() > 0) {
   //Serial.println("SERIAL READING");
  // Serial.println(myGSM.read());}
   //content=Serial.readStringUntil('\n');
   //Serial.println(content);
   //content.concat(myGSM.read());

/* if (content != "") {
    Serial.println(content);
  }*/

 memset(content, '\0', 100); // initalize string
  delay(100);
  while (myGSM.available() > 0) {
    myGSM.read(); }// clears the buffer}
  
    
    // request Basic string
//    sendATcommand("AT+CGPSOUT=0","OK",2000);
    sendATcommand("AT+CGPSINF=32", "OK", 2000);
//Serial.println("AT+CGPSINF=32\r\n");
//    counter = 0;
   answer = 0;
   // memset(frame, '\0', 100);    // Initialize the string
    previous = millis();
    // this loop waits for the NMEA string
    Serial.println("WAITING FOR NMEA STRING");
   // Serial.flush();
  /* do{
//Serial.println("LOOP IN PROCESS");
Serial.println("SERIAL INSIDE LOOP VALUE");
Serial.println(Serial.read());
        if(myGSM.available()){    
            frame[counter] = myGSM.read();
            counter++;
            Serial.println("FRAME CONTENT IS ");
            Serial.println(frame);
            // check if the desired answer is in the response of the module
            if (strstr(frame, "OK") != NULL)    
            {
                answer = 1;
            }
        }
        // Waits for the asnwer with time out
    }
    while((answer == 0) && ((millis() - previous) < 5000)); */ 
/*string content="";*/
 /* do {
    Serial.println("MY GSM READ VALUE");
    Serial.println(myGSM.read());
    if (myGSM.available()!=0) {
      frame[counter] = myGSM.read();
      //Serial.println(response[x]);
     counter++;
    // if (strstr(frame,",A") != NULL) {
         Serial.println(frame);
        answer = 1;
    // }
    }
  } while ((answer == 0) && ((millis() - previous) < 2000));*/

      
  //size_t counter;  
  /*Serial.println("VALUE OF CONTENT");
  Serial.println(content);  */
//counter= sizeof(content
uint8_t counter= 0;

 Serial.println("VALUE OF COUNTER");
  Serial.println(counter);  
counter=x;
    //content[counter-4] = '\0'; 
    
    // Parses the string 
    Serial.println(content);
    strtok(content, "\n");
    strtok(NULL,",");
   /* strcpy(longitude,strtok(NULL, ",")); // Gets longitude
    Serial.println("PRESENT LONGITTUDE");
    Serial.println(longitude);*/
    strtok(NULL,",");
    strtok(NULL,",");
    strcpy(latitude,strtok(NULL, ",")); // Gets latitude
    Serial.println("PRESENT LATITUDE IS ");
    Serial.println(latitude);
    strtok(NULL,",");
    strcpy(longitude,strtok(NULL, ",")); // Gets longitude
    Serial.println("PRESENT LONGITTUDE");
    Serial.println(longitude);
    strcpy(altitude,strtok(NULL, ".")); // Gets altitude 
    strtok(NULL, ",");    
    strcpy(date,strtok(NULL, ".")); // Gets date
    strtok(NULL, ",");
    strtok(NULL, ",");  
    strcpy(satellites,strtok(NULL, ",")); // Gets satellites
    strcpy(speedOTG,strtok(NULL, ",")); // Gets speed over ground. Unit is knots.
    strcpy(course,strtok(NULL, "\r")); // Gets course

convert2Degrees(latitude);
convert2Degrees(longitude);
   // return answer;
}



int8_t convert2Degrees(char* input){

    float deg;
    float minutes;
    boolean neg = false;    

    //auxiliar variable
    char aux[10];
    char aux_1[10];

    if (input[0] == '-')
    {
        neg = true;
        strcpy(aux, strtok(input+1, "."));

    }
    else
    {
        //strtok(input,".");
        strcpy(aux, strtok(input, "."));
        strcpy(aux_1, strtok(NULL, "."));
        Serial.println("AUX VALUE AFTER TOKENINZING");
        Serial.println(aux);
    }

    // convert string to integer and add it to final float variable
    deg = atof(aux);

    strcpy(aux, strtok(NULL, '\0'));
    minutes=atof(aux_1);
    minutes/=1000000;
    if (deg < 100)
    {
        minutes += deg;
        deg = 0;
    }
    else
    {
        minutes += int(deg) % 100;
        deg = int(deg) / 100;    
    }

    // add minutes to degrees 
    deg=deg+minutes/60;


    if (neg == true)
    {
        deg*=-1.0;
    }

    neg = false;

    if( deg < 0 ){
        neg = true;
        deg*=-1;
    }
    
    float numeroFloat=deg; 
    int parteEntera[10];
    int cifra; 
    long numero=(long)numeroFloat;  
    int size=0;
    
    while(1){
        size=size+1;
        cifra=numero%10;
        numero=numero/10;
        parteEntera[size-1]=cifra; 
        if (numero==0){
            break;
        }
    }
   
    int indice=0;
    if( neg ){
        indice++;
        input[0]='-';
    }
    for (int i=size-1; i >= 0; i--)
    {
        input[indice]=parteEntera[i]+'0'; 
        indice++;
    }

    input[indice]='.';
    indice++;

    numeroFloat=(numeroFloat-(int)numeroFloat);
    for (int i=1; i<=6 ; i++)
    {
        numeroFloat=numeroFloat*10;
        cifra= (long)numeroFloat;          
        numeroFloat=numeroFloat-cifra;
        input[indice]=char(cifra)+48;
        indice++;
    }
    input[indice]='\0';

Serial.println("CONVERTED VALUE of latitude nd longitude");
Serial.println(latitude);
Serial.println(longitude);
}

int8_t sendATcommand(char* ATcommand, char* expected_answer, unsigned int timeout) {
  uint8_t x = 0, answer = 0;
  char response[100];
  unsigned long previous;
  memset(response, '\0', 100); // initalize string
  delay(100);
  while (myGSM.available() > 0) {
    myGSM.read(); // clears the buffer
  }
  myGSM.println(ATcommand);
  // Serial.println(ATcommand);
  x = 0;
  previous = millis();
  do {
    if (myGSM.available() != 0) {
      response[x] = myGSM.read();
     /// Serial.println("RESPONSE GIVEN IN AT COMMAND");
      //Serial.println(response[x]);
      x++;
      //Serial.println("VALUE OF X ");
     // Serial.println(x);
      if (strstr(response, expected_answer) != NULL) {
         Serial.println(response);
        answer = 1;
      }
      if(strstr(response,",,E,A")!=NULL){
        strcpy(content,response);
          //Serial.println("VALUE OF CONTENT IN AT COMMAND");
//  Serial.println(content);  
        //Serial.println("AFTER PRINTING CONTENT");
      }
      if(strstr(response,"RING")!=NULL){
        Serial. println("PHONE IS RINGING :) ");
      if(strstr(response,"+917259467276")!=NULL){
       
       strcpy(call_number, "+917259467276");
        Serial.println("CALL NUMBER IN AT COmMMAND ");
        Serial.println(call_number);
        get_GPS();
        y=1;
        break;
        z=1;
        }else{
          
          z=2;
          //strcpy(call_number,"0000000000");
        }
    }}
  } while ((answer == 0) && ((millis() - previous) < timeout));
 // Serial.println("RESPONSE FROM AT COMMAND IS");
  Serial.println(response);
  return answer;
}
