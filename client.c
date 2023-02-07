#include "server.h"

/************************************************************************
 * MAIN
 ************************************************************************/
int main() {
    char input[100];                    // buffer for user input
    int client_socket;                  // client side socket
    struct sockaddr_in client_address;  // client socket naming struct
    struct hostent *host_entry;         // host information struct
    char c;
    
    printf("Echo client\n");

    // resolve host name and store
    host_entry = gethostbyname(NIST_DNS);

    if (host_entry == NULL)
    {
        printf("Error resolving hostname!");
        exit(h_errno);
    }
    else
    {
        printf("Connected to %s\n", host_entry->h_name);
    }
    
    if (host_entry->h_addrtype == AF_INET)
    {
        // create an unnamed socket, and then name it
        client_socket = socket(AF_INET, SOCK_STREAM, 0);
        // create addr struct
        client_address.sin_family = AF_INET;
        client_address.sin_addr.s_addr = inet_addr(host_entry->h_addr);
        client_address.sin_port = htons(NIST_PORT);

        printf("Host type is AF_INET\n");
    }
    else if (host_entry->h_addrtype == AF_INET6)
    {
        // create an unnamed socket, and then name it
        client_socket = socket(AF_INET6, SOCK_STREAM, 0);
        // create addr struct
        client_address.sin_family = AF_INET6;
        client_address.sin_addr.s_addr = inet_addr(host_entry->h_addr);
        client_address.sin_port = htons(NIST_PORT);

        printf("Host type is AF_INET6\n");
    }
    
    // connect to server socket
    if (connect(client_socket, (struct sockaddr *)&client_address, sizeof(client_address)) == -1) {
        perror("Error connecting to server!\n");
        exit(EXIT_FAILURE);
    }
    
    while (TRUE) {
        printf("Input: ");
        // read string
        fgets(input, sizeof(input), stdin);
        
        int i = 0;
        while (*(input + i)) {
            // make the request to the server
            write(client_socket, input + i, sizeof(char));
            // get the result
            read(client_socket, &c, sizeof(char));
            if (c == 'q') {
                close(client_socket);
                printf("\nDone!\n");
                exit(EXIT_SUCCESS);
            }
            printf("%c", c);
            i++;
        }
    }
    
    return EXIT_SUCCESS;
}

