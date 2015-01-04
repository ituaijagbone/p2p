# P2P 

This is a Command-line interface P2P file system application. It uses the concepts of neighbors where each user has a list of neigbours it can connect to when searching for a file. When a user search for a file, the application queries its neighbors which in turn queries their own neighbor. This recursive call happens until the time to live of the request expires. When query results are returned, requesting client makes connection with any of the peers that have the file and downloaded it. 

#### ToDO 
[] Compelete Debug Testing on Amazon EC2

[] Proper Readme Write
