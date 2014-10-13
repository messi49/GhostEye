#include <jni.h>
#include <android/log.h>

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

#define LOG_TAG	"CameraNative"
#define LOGI(...)	__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)	__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

//Prototype declaration
void getPicture(int sock);
void setPicture(char *buf, int sum);
void* sendServer(void *p);


//Global Functions
int sock0, sock1, sock2, sock3, sock4, sock5;
int recieve_flag = 0;
int coordinates[128];


struct data {
	int threadID;
	int data_size;
	int speed;
    unsigned char buf[200000];
};

//thread of transportation
struct data d;
pthread_cond_t cond;
static pthread_mutex_t mt = PTHREAD_MUTEX_INITIALIZER;

void getPicture(int sock){
	int size = 0;
	unsigned int file_size = 555555;
	int sum = 0;
	int image_size;

	//save image data
	static unsigned char buf[200000];

	memset(buf, 0, sizeof(buf));
	size = recv(sock, &image_size, 4, 0);
	while(1){
		//recieve image data
		size = recv(sock1, buf+sum, image_size-sum, 0);
		sum += size;

		if(sum == image_size){
            setPicture(buf,sum);
			break;
		}
	}

}

void setPicture(char *buf, int sum){
    FILE *fpw;
    char *fname_w = "/mnt/sdcard/";
    int i, size;
    char file_name[32];

    //sprintf(file_name,"%s%02d:%02d:%02d:%3d.jpg",fname_w, tmptr->tm_hour, tmptr->tm_min, tmptr->tm_sec, tv.tv_usec/1000);
    sprintf(file_name,"%s%s.jpg",fname_w, "recieve");

    fpw = fopen( file_name, "wb" );
    if( fpw == NULL ){
            printf( "書込用 %sファイルが開けません\n", fname_w );
            return;
    }

    fwrite( buf, sizeof( unsigned char ), sum, fpw );

    fclose( fpw );
}

JNIEXPORT void JNICALL Java_com_netim_CameraNative_connectSock(JNIEnv * env, jobject obj, jstring address, jint port_number, jint sock_num, jint flag){
	  struct sockaddr_in server;
	  int sock;

	  const char *address_number = (*env)->GetStringUTFChars(env, address, 0);

	  /* create socket */
	  sock = socket(AF_INET, SOCK_STREAM, 0);

	  /* Preparation of the structure for the specified destination */
	  server.sin_family = AF_INET;
	  // default: 12345
	  //server.sin_port = htons(12345);
	  server.sin_port = htons(port_number);

	  server.sin_addr.s_addr = inet_addr(address_number);

	  connect(sock, (struct sockaddr *)&server, sizeof(server));

	  switch(sock_num){
	  case 0:
		  sock0 = sock;
		  break;
	  case 1:
		  sock1 = sock;
		  break;
	  case 2:
		  sock2 = sock;
		  break;
	  case 3:
		  sock3 = sock;
		  break;
	  case 4:
		  sock4 = sock;
		  break;
	  case 5:
		  sock5 = sock;
		  break;
	  }

	  recieve_flag = flag;
}

JNIEXPORT void JNICALL Java_com_netim_CameraNative_closeSock(JNIEnv * env){
    if(close(sock0)<0){
            printf("error_sock2\n");
    }
	if(close(sock1)<0){
            printf("error_sock1\n");
    }
    if(close(sock2)<0){
            printf("error_sock2\n");
    }
	if(close(sock3)<0){
            printf("error_sock3\n");
    }
    if(close(sock4)<0){
            printf("error_sock4\n");
    }
    if(close(sock5)<0){
            printf("error_sock4\n");
    }
}

JNIEXPORT void JNICALL Java_com_netim_CameraNative_createThread(JNIEnv * env){
	pthread_t thread;

	//initialize cond_init and mutex_init
	pthread_cond_init(&cond, NULL);
	pthread_mutex_init(&mt,NULL);
	//create thread of transportation
    pthread_create(&thread, NULL, sendServer, &d);

}

JNIEXPORT void JNICALL Java_com_netim_CameraNative_sendInfo(JNIEnv * env,jobject thiz, jint flag){
	//send info of whether send image data to server
	send(sock0, &flag, 4, 0);
}

JNIEXPORT jstring JNICALL Java_com_netim_CameraNative_sendPhoto(JNIEnv * env,jobject thiz, jint data_size, jint speed){
//JNIEXPORT jstring JNICALL Java_com_netim_CameraNative_sendPhoto(JNIEnv * env,jobject thiz, jint data_size, jbyteArray data, jint speed){

	//char buff[200000];
//
//	switch(threadID){
//	case 1:
//		sockNum = sock1;
//		break;
//	case 2:
//		sockNum = sock2;
//		break;
//	case 3:
//		sockNum = sock3;
//		break;
//	case 4:
//		sockNum = sock4;
//		break;
//	case 5:
//		sockNum = sock5;
//		break;
//	}
//
	/*
	int sockNum;
	int dataInfo[2];

	jbyte* data_array = (jbyte*)(*env)->GetPrimitiveArrayCritical(env, data, NULL);

	//strcpy(buff, data_array);

	dataInfo[0] = 1;
	dataInfo[1] = data_size;

	send(sock0, &dataInfo, 8, 0);
	send(sock1, data_array, data_size, 0);

	*/

	int threadID = 1;

	//jbyte* data_array = (jbyte*)(*env)->GetPrimitiveArrayCritical(env, data, NULL);

	pthread_mutex_lock(&mt);

	d.threadID = threadID;
	d.data_size = data_size;
	d.speed = speed;
	//strcpy(d.buf, data_array);

	FILE *fpw;
	char file_name[126];
	int i, size;

	strcpy(file_name,"/mnt/sdcard/tmp.jpg");
	fpw = fopen( file_name, "rb" );

	if(fpw != NULL) {
		while(size = fread( d.buf, sizeof(unsigned char), d.data_size, fpw )) {
			for(i = 0 ; i < size ; i++) {
				//printf("%c", buf[i]);
			}
		}
		fclose(fpw);
	}

	fclose( fpw );


	/* ここまで */

	pthread_cond_signal(&cond);

	pthread_mutex_unlock(&mt);

	/* 座標受信 */
	char buff[256];
	char data_string[1024];
	int num=0;
	int resolution;
	int count=1;

	/* initialization */
	for(count=0; count < 1024; count ++){
		data_string[count] = '\0';
	}
	count=0;

	if(recieve_flag == 1 || recieve_flag == 2){
		recv(sock1, coordinates, 16, 0);
		num = coordinates[0];
		resolution = coordinates[1];
		sprintf(buff, "%d,%d,%d,%d", coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
		strcat(data_string, buff);

		recv(sock1, coordinates+16, num*24, 0);
		while(count<num){
			sprintf(buff, ",%d,%d,%d,%d,%d,%d", coordinates[16+count*6], coordinates[17+count*6], coordinates[18+count*6], coordinates[19+count*6], coordinates[20+count*6], coordinates[21+count*6]);
			strcat(data_string, buff);
			count++;
		}
	}

	//(*env)->ReleasePrimitiveArrayCritical(env, data, data_array, 0);

    return (*env)->NewStringUTF(env, data_string);

}

void* sendServer(void *p){

	int sockNum;
	int dataInfo[3];
    struct data* pdata = (struct data*)p;

    while(1){
    	pthread_mutex_lock(&mt);

    	pthread_cond_wait(&cond, &mt);

    	dataInfo[0] = pdata->threadID;
    	dataInfo[1] = pdata->data_size;
    	dataInfo[2] = pdata->speed;

    	switch(dataInfo[0]){
    	case 1:
    		sockNum = sock1;
    		break;
    	case 2:
    		sockNum = sock2;
    		break;
    	case 3:
    		sockNum = sock3;
    		break;
    	case 4:
    		sockNum = sock4;
    		break;
    	case 5:
    		sockNum = sock5;
    		break;
    	}

	send(sock0, &dataInfo, 8, 0);
	send(sockNum, pdata->buf, dataInfo[1], 0);

	pthread_mutex_unlock(&mt);
    }

//	// 自スレッド識別情報の取得
//	pthread_t self_thread = pthread_self();
//
//	// デタッチ
//	int status = pthread_detach(self_thread);
//	if (status != 0) {
//		fprintf(stderr, "failed to detatch\n");
//	}

}

/*
JNIEXPORT void JNICALL Java_com_tipic_CameraNative_getPhoto(JNIEnv * env){
	int size = 0;
	unsigned int file_size = 555555;
	int sum = 0;
	int image_size;

	//save image data
	static unsigned char buf[200000];

	memset(buf, 0, sizeof(buf));
	size = recv(sock1, &image_size, 4, 0);
	while(1){
		//recieve image data
		size = recv(sock1, buf+sum, image_size-sum, 0);
		sum += size;

		if(sum == image_size){
            setPicture(buf,sum);
			break;
		}
	}
}
*/
