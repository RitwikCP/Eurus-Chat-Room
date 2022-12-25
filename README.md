![zyro-image (12)](https://user-images.githubusercontent.com/94697656/209456476-d3378876-dd17-47c7-81e2-765ab42006f3.png)

*Work in progress: The next update will add end-to-end encryption for a secure transfer of messages.*

# Eurus-Chat-Room
A project made as part of the "Core Java Programming" course during my first semester in college.



## AN OVERVIEW
The term "chatroom" is primarily used to describe any synchronous conferencing format, sometimes even asynchronous conferencing. Thus, the word can convey any technology, from real-time online chat and exchange with strangers (online forums, for example) to fully immersive graphical social environments. 
The immediate use of a chat room is to transmit information through text with a pack of other users. Commonly speaking, the capability to converse with numerous people in the same conversation distinguishes chat rooms from instant messaging programs, which are more naturally developed for one-to-one communication. 
The users in a particular chat room are usually connected via a shared internet or a similar connection, and chat rooms cater to a vast spectrum of subjects. 

This repository, in particular, contains all the files created as part of my submission of a mini-project for the Core Java Programming course in my college. Working on this made me confident in using IntelliJ and allowed me to implement concepts like OOPs, multithreading, etc., and socket programming techniques I learned throughout the semester. Java is a powerful programming language, and this project undoubtedly gives all beginners a taste of its capabilities. 

## OBJECTIVE
To create a chat room application using Java that uses Transmission Contro Protocol. Here, clients can connect exchange messages within a server with all the clients connected to that server. 

## MOTIVATION
This was created as part of my "Core Java Programming" course in the first semester of my collge. The project was completed within seven days and aimed to showcase the skill set of coding that was developed throughout the semester. The minimum criteria of the mini-project for all students was that it must use TCP. This made me think of Group Chat as one particular application that can be implemented.

## FEATURES OF EURUS
Eurus is an example of Multithread Socket Programming using JavaFX and has the following features:
1. There are two files. One for Client and one for Server. The Server one takes one parameter (a port number) and the Client one takes two parameters (an IP address, a port number).
2. A client enters a username before the interface for Eurus is displayed.
3. A client remains in READ ONLY mode if he/she does not enter a username.
4. A client can use emojis as Eurus has been equipped to support emojis.
5. The architecture is server-client. Therefore, all the messages from the clients need to go to the server, which then distributes the message to all the clients.
6. The server records all the messages, the timestamp of all messages and name of users joined/exited at all times.
7. A client disconnects when he/she enters "bye" [case ignored].
8. When a client enters “All Users”, the server needs to send all active clients to that client.

## WORKING OF EURUS
The working has been clearly explained in the code through comments. One is encouraged to go through the comments in order to get an idea of the working of Eurus.

## SCREENSHOTS OF ENDPRODUCT


### Eurus Server
------------------------------------------------------------------------
<img width="705" alt="Screenshot 2022-12-24 at 11 33 40 PM" src="https://user-images.githubusercontent.com/94697656/209456757-515bf178-b4cb-4a43-88c4-0beeedfd626a.png">


### Entering Username
------------------------------------------------------------------------
<img width="502" alt="Screenshot 2022-12-24 at 11 34 15 PM" src="https://user-images.githubusercontent.com/94697656/209456759-7f1a00ae-63d2-426b-b8a8-42050ec7f1ad.png">


### Emojis
------------------------------------------------------------------------
<img width="850" alt="Screenshot 2022-12-25 at 12 44 53 AM" src="https://user-images.githubusercontent.com/94697656/209456760-cbb75964-9c18-41c9-8a93-80e45992859c.png">


### Server Keeping Track Of All Messages
------------------------------------------------------------------------
<img width="544" alt="Screenshot 2022-12-25 at 12 54 06 AM" src="https://user-images.githubusercontent.com/94697656/209456761-3276c10a-ea04-4f8c-838b-8512d8c2a57d.png">


### Client Interface
------------------------------------------------------------------------
<img width="838" alt="Screenshot 2022-12-25 at 12 54 40 AM" src="https://user-images.githubusercontent.com/94697656/209456765-50c34b5f-3201-4bd1-a719-bf29b3366d33.png">


## TECH/FRAMEWORK USED
[IntelliJ IDEA](https://www.jetbrains.com/idea/)

## CREDITS
The project was inspired by the following sources:
1. [Group Chat Application Project using Socket Programming in Java](https://www.codewithmurad.com/2020/10/group-chatting-application.html)
2. Luisgcenci (A Medium author)
