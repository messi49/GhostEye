#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#define STR_MAX 256 /* 文字列入力用配列長 */
#define SLEEP_TIME 3 /* 監視周期  */
int main(void){

	FILE *fp;
	char filename[100];
	char buff[STR_MAX]; /* 文字列用 */
	int line=0;

	while(1){
		//printf("ps alx|grep GhostEye_server >watchman_log.txt\n");
		system("ps alx|grep GhostEye_server >watchman_log.txt");


		sprintf(filename, "watchman_log.txt");

		if((fp = fopen(filename, "r")) == NULL){    /* ファイルオープン */
			printf("ファイルがオープンできません\n");
			exit(1);        /* 強制終了 */
		}

		while(fgets(buff, STR_MAX, fp) != NULL){    /* 1行読み込み */
			++line;    /* 行数カウント */
			//printf("%d:%s", line, buff);    /* 1行表示 */
		}

		fclose(fp);
		if(line == 2){
			printf("GhostEye_server: Not Active\n");
			printf("./GhostEye_server\n");
			system("./GhostEye_server");
		}
		else{
			printf("GhostEye_server: Active\n");
			//initialize
			line = 0;
			//sleep
			sleep(SLEEP_TIME);
		}
	}

	return 0;
}
