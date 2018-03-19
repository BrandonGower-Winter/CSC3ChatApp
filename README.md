# CSC3ChatApp
Chat App for CSC3002F

Guest logins available:
  Username: guest1, guest2, guest3
  Password: 123, 456, 789

Message Format: \<command\>| \<target\>| \<content\>

    Users File Format: <username>|<password>
    Friends File Format: <username>|<friend1>,<friend2>...<friendN>

Code 0: simple communication between 2 clients and if target is "all", a broadcast is sent to all live clients

        format: 0|<target>|<content>

Code 1: //reserved for file transfers

Code 2: registering a user to a local pseudo-database

    format: 2|<Username>|<Password>

Code 3: login/signin a user

    format:3|<Username>|<Password>

COde 4: logout/signout (pending progress)

    format: 4|<username>|<password>

Code 5: add friend/contact

    format: 5|<your current logged in username>|<friend's username>
    NB: Friend's username should be a registered first

Code 6: creating a group

    format: 6|<group name>|<username1.username2.username3.username4. ...>
    (all usernameNs shuold be registered)


Code 7: add a person to a group

    format: 7|<groupname>|<friend's username>


Code 9: send a message to a group

    format: 9|<group name>|<content>

Code 10: confirm file send;

    format 10|<target>|<confirmation status>

    confirmation status:
      0: yes
      1: no

Code 11: Send file bit:

    format 11|<target>|<file bit>

Code 50: Confirm user login status

    format: 50|<user>|<login status>

    login status:
              0: Login Successful
              1: Login Unsuccessful
              2: Registration Successful
              3: Registration Unsuccessful

Code 51: Send user file status

    format: 51|<Sender>|<file name>

Code 52: Send user file
    
    format 52|sender|<filename>%<file size>%<file part>%<filedata>
