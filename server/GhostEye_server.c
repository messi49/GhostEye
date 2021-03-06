#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <time.h>
#include <signal.h>
#include <errno.h>
#include <assert.h>

/* Cordinates */
struct coordinates{
	int left;
	int top;
	int right;
	int bottom;
	int type; //1: car, 2:sign, 3:human, 4:Unknown
	int reserved;
};

void getConnect(int port, int sock_num);
void disconnect();
int getInfo();
int getScokInfo();
int getPicture(int sock_num);
int photo(char *buf,int sum);
void sendPicture();
int sendCoordinates(struct coordinates *points,int num,int resolution);


//info of socket 
int sock0, sock1, sock2, sock3, sock4, sock5;
/*
 * whether or not server send image data to android
 * flag 0:not send, 1:send
 */ 
int flag;
int dataInfo[3];
char file_name[126];

int main(){
	int count=0;
	struct timeval start, end;
	struct timeval start_timeval, end_timeval;                                                                 
	double sec_timeofday, cycle_time;

	/* test */
	// left, top, right, bottom
	struct coordinates points[32] = {{0, 230, 200, 480, 1, 0},
		{310, 230, 450, 430, 2, 0},
		{460, 280, 470, 320, 3, 0},
		{560, 180, 640, 380, 0, 0},
		{20, 230, 200, 480, 1, 0},
		{340, 330, 480, 430, 2, 0},
		{360, 300, 470, 320, 3, 0},
		{460, 220, 640, 380, 0, 0},
		{30, 260, 200, 440, 1, 0},
		{310, 230, 350, 430, 2, 0},
		{460, 280, 480, 320, 3, 0},
		{540, 280, 640, 380, 0, 0},
		{50, 250, 200, 480, 1, 0},
		{350, 230, 450, 430, 2, 0},
		{410, 240, 470, 320, 3, 0},
		{460, 190, 620, 340, 0, 0},
		{0, 230, 200, 480, 1, 0},                
		{310, 230, 450, 430, 2, 0},
		{460, 280, 470, 320, 3, 0},
		{560, 180, 640, 380, 0, 0},   
		{20, 230, 200, 480, 1, 0},  
		{340, 330, 480, 430, 2, 0},
		{360, 300, 470, 320, 3, 0},
		{460, 220, 640, 380, 0, 0},
		{30, 260, 200, 440, 1, 0},
		{310, 230, 350, 430, 2, 0},
		{460, 280, 480, 320, 3, 0},
		{540, 280, 640, 380, 0, 0},
		{50, 250, 200, 480, 1, 0},
		{350, 230, 450, 430, 2, 0},
		{410, 240, 470, 320, 3, 0},
		{460, 190, 620, 340, 0, 0}};

	int num;	

	//connect to android
	getConnect(12345, 0);
	printf("connect0\n");
	getConnect(12346, 1);
	printf("connect1\n");
	/*
	   getConnect(12347, 2);
	   printf("connect2\n");
	   getConnect(12348, 3);
	   printf("connect3\n");
	   getConnect(12349, 4);
	   printf("connect4\n");
	   getConnect(12350, 5);
	   printf("connect5\n");
	 */
	//get infomation of whether or not server send image data to android
	if(getInfo() == -1)
		return 0;

	gettimeofday( &start_timeval, NULL );

	while(1){
		gettimeofday(&start, NULL);
		if(getSockInfo() == -1){
			disconnect();
			return 0;
		}
		switch(dataInfo[0]){
			case 1:
				//printf("getPicture1\n");                                                         
				if(getPicture(1) == -1){
					disconnect();
					return 0;

				}
				else if(flag == 1 || flag == 2){
					printf("send coordinates\n");
					sleep(2);
					if(sendCoordinates(points, 16, 2) == -1){
						disconnect();
						return 0;
					}
				}
				break;
			default:
				//printf("ERROR: dataInfo[0] is warong\n");
				//printf("dataInfo[0]: %d\n", dataInfo[0]);
				break;

		}
		gettimeofday(&end, NULL);
		cycle_time = (end.tv_sec - start.tv_sec)
			+ (end.tv_usec - start.tv_usec) / 1000000.0;
		printf("%f\n", cycle_time);
		printf("%d/100 Finish!!!\n",count);
		count++;
	}
	gettimeofday( &end_timeval, NULL );

	sec_timeofday = (end_timeval.tv_sec - start_timeval.tv_sec)
		+ (end_timeval.tv_usec - start_timeval.tv_usec) / 1000000.0;

	printf("endtime: %f\n", sec_timeofday/100);
	return 0;
}

void getConnect(int port, int sock_num){
	struct sockaddr_in addr;
	struct sockaddr_in client;
	int len;
	int sock,tmp;
	int yes = 1;

	sock = socket(AF_INET, SOCK_STREAM, 0);

	addr.sin_family = AF_INET;
	addr.sin_port = htons(port);
	addr.sin_addr.s_addr = INADDR_ANY;
	//make it available immediately to connect
	setsockopt(sock,SOL_SOCKET, SO_REUSEADDR, (const char *)&yes, sizeof(yes));
	printf("befora bind\n");
	bind(sock, (struct sockaddr *)&addr, sizeof(addr));
	printf("after bind\n");
	listen(sock, 5);
	len = sizeof(client);

	printf("before accept\n");
	tmp = accept(sock, (struct sockaddr *)&client, &len);
	printf("after accepst\n");
	if(tmp == -1){
		printf("ERROR: socket cannot accept\n");
		return ;
	}

	switch(sock_num){
		case 0:
			sock0 = tmp;
			break;
		case 1:
			sock1 = tmp;
			break;
		case 2:
			sock2 = tmp;
			break;
		case 3:
			sock3 = tmp;
			break;
		case 4:
			sock4 = tmp;
			break;
		case 5:
			sock5 = tmp;
			break;
		default:
			printf("ERROR: sock_num is wrong.\n");
			break;
	}
}

void disconnect(){
	close(sock0);
	close(sock1);
	/*
	close(sock2);
	close(sock3);
	close(sock4);
	close(sock5);
	*/
}

int getInfo(){
	if(recv(sock0, &flag, 4, 0) == -1){
		printf("ERROR: can not recieve info\n");
		return -1;
	}

	if(flag == 0){
		//printf("do not send image data to Android.\n");
	}
	else if(flag == 1){
		//printf("send image data to Android.\n");
	}
	else if(flag == 2){
		//printf("send image data to Android.\n");
	}
	else{
		printf("ERROR: flag is strange.\nflag = %d\n",flag);
		return -1;
	}	
}

int getSockInfo(){
	//get infomation that is number of sock and data size
	if(recv(sock0, &dataInfo, 12, 0) == -1){
		printf("ERROR: can not recieve Data info\n");
		return -1;
	}
	printf("dataIndo[0] = %d, dataInfo = %d, speed = %d\n", dataInfo[0], dataInfo[1], dataInfo[2]);
	return 0;
}
int getPicture(int sock_num){
	int sock;
	int len;
	int size = 0;
	unsigned int file_size = 555555;
	int sum = 0;
	int yes = 1;
	int image_size;
	static unsigned char size_str[20];

	//save image data
	char buf[400000];
	static unsigned char buf2[400000];

	struct timeval start, end;
	double transport_time;

	switch(sock_num){
		case 1:
			sock = sock1;
			break;
		case 2:
			sock = sock2;
			break;
		case 3:
			sock = sock3;
			break;
		case 4:
			sock = sock4;
			break;
		case 5:
			sock = sock5;
			break;
	}

	memset(buf, 0, sizeof(buf));

	image_size = dataInfo[1];
	printf("%d,",image_size);
	gettimeofday(&start, NULL);

	//printf("image_size:%d\n",image_size); 
	while(1){
		//recieve image data
		size = recv(sock, buf+sum, image_size-sum, 0);
		if(size == 0){
			printf("ERROR: cannot recive image data\n");
			return -1;
		}
		sum += size;
		//printf("size:%d__sum:%d\n",size,sum);
		if(sum == image_size){
			gettimeofday(&end, NULL);

			transport_time = (end.tv_sec - start.tv_sec)
				+ (end.tv_usec - start.tv_usec) / 1000000.0;

			printf("%f,",transport_time);

			//printf("sum == file_size\n");                
			// save image dataa
			//strcpy(buf2, buf);
			photo(buf,image_size);
			return 0;
		}
		else if(sum > image_size){
			printf("ERROR: sum > file_size\n");
			return -1;
		}
	}
}

int photo(char *buf,int sum){
	FILE *fpw;
	char *fname_w = "photo/";
	int i, size;

	struct timeval tv;
	struct tm *tmptr = NULL;	

	//get current time
	gettimeofday(&tv, NULL);
	tmptr = localtime(&tv.tv_sec);

	sprintf(file_name,"%s%02d:%02d:%02d:%3d.jpg",fname_w, tmptr->tm_hour, tmptr->tm_min, tmptr->tm_sec, tv.tv_usec/1000);

	//file open
	fpw = fopen( file_name, "wb" );
	if( fpw == NULL ){
		printf( "書込用 %sファイルが開けません\n", fname_w );
		return -1;
	}

	//write 
	fwrite( buf, sizeof( char ), sum, fpw );

	fclose( fpw );

	printf( "%sファイルのコピーが終わりました\n", file_name );

}

// send image data to andrid
void sendPicture(){
	//TCP
	char buf[32];
	char size_str[20];
	int n,yes=1;

	FILE *fpr;
	unsigned char temp[50000];

	int size = 0, re = 0;

	//File path
	//if you want to send the image data, you shoud set the this file path.
	if((fpr = fopen( file_name, "rb" )) == NULL ){
		printf("ERROR: can not open.\n");
		return;
	}

	//search size of image file 
	size = fread( temp, sizeof( unsigned char ), 150000, fpr);
	sprintf(size_str,"%d",size);
	//send size of image file to android
	send(sock1, &size, 4, 0);
	//send image data to android
	re = send(sock1, temp, size, 0);
	if( re == -1 ){
		switch(errno){
			case EPIPE:
				break;
		}
	}

	//fflush buffer
	fflush(stdout);
	fclose(fpr);
}

/*send coordinate of targeta
  sendCoordinates(struct coordinates *point, int num, int resolution) */
/*
 * ターゲットがいない場合は 
 *	x1 = -1;
 *	y1 = -1;
 *	に設定すること
 */
int sendCoordinates(struct coordinates *points, int num, int resolution){
	int re = 0;
	int i = 0;	

	//data send to server
	int data[4 + num*6];

	/* header
	   number of object
	   resolution
	   Reserverd
	   Reserverd
	 */
	data[0] = num;
	data[1] = resolution;
	data[2] = 0;
	data[3] = 0;

	while(i < num){
		data[4+i*6] = points->left;
		data[5+i*6] = points->top;
		data[6+i*6] = points->right;
		data[7+i*6] = points->bottom;
		data[8+i*6] = points->type;
		data[9+i*6] = points->reserved;
		printf("\nleft: %d,top: %d,right: %d,bottom: %d,type: %d\n", data[4+i*6], data[5+i*6],  data[6+i*6],  data[7+i*6], data[8+i*6]);
		i++;
		points++;
	}


	if(send(sock1, &data, 16+24*num, 0) == -1){
		printf("ERROR: can not send coordinate of target.\n");
		return -1;
	}
	return 0;	
}
