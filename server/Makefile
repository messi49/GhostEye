TARGET = GhostEye_server
CC = gcc

$(TARGET) : GhostEye_server.o 
	$(CC) -o $@ GhostEye_server.o 

server.o : GhostEye_server.c
	$(CC) -c $? 

clean:;	rm -f GhostEye_server.o GhostEye_server
