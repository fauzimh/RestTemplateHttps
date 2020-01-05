#Rest Template with Https

This repo demonstrate how to use RestTemplate to call https and http.

This maven project contains 2 sub-modules: 
- rest-template-client
- rest-template-server

##Rest Template Server
rest-template-server runs on 2 ports: 
- Port 443 for https
- Port 80 for http

Server does not authenticate client

##Rest Template Client
rest-template-client will call both https and https.

When calling https, client: 
* will accept all certs <br> 
  This means, client does not care if the cert is not authenticated by the Certificate Authority(CA).

* would not validate hostname <br>
  Client does not care whether the hostname it is calling matches with the cert the server is sending. <br> 
  To enable hostname validation, remove the NoopHostnameVerifier from the spring active profile in application.properties.
 
