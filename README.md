# lsit-ticket-system
Welcome!

RUNNING API
This is our ticketing system repository. To be able to run the application please run Main.java and then open in the browser http://localhost:8080/, then to be able to access API calls go to http://localhost:8080/swagger-ui/index.html. This will allow you to test our API. 

AUTHORISATION
However, before you try you need to do http://localhost:8080/authorise to log in with your GitLab account. To be able to access specific parts of API you need to be part of our groups roles/ticket-system/[ticket_customer, ticket_event_manager, ticket_marketing]. 

GROUPS
The customer group gives access to the customer controller and reads of events, the Event Manager group allows to keep track of events using the event controller and the Marketing group allows to use notification controller - otherwise the program will return 403 error as information of denied access. 

GCS CREDENTIALS
We connect to the created by us container in GCS through Google Library, we created fresh and unpublished credentials so the code should not break and allow to publish stuff into our container directly and access stuff from it too.

DOCKER
To run our Docker, use "docker build -t image ." command and then "docker run -p 8080:8080 image".

In case of any problems please get in touch with me via email: a.fiutowski@student.maastrichtuniversity.nl
