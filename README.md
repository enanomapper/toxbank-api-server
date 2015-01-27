#The Java ToxBank API Protocol service software.

There are maven profiles  -P toxbank and -P enm for [ToxBank](http://www.toxbank.net) and [eNanoMapper](http://www.enanomapper.net) protocol services, respectively.

The tests assume 

##-P toxbank  

### Production DB
````
CREATE USER 'guest'@'localhost' IDENTIFIED BY 'guest';
GRANT ALL ON tb.* TO 'guest'@'localhost';
GRANT execute on procedure `tb`.createProtocolVersion to guest@localhost;
````

###Test DB
````
GRANT ALL ON `tb-test`.* TO 'guest'@'localhost';
GRANT execute on procedure `tb-test`.createProtocolVersion to  guest@localhost;
````

##-P enm 

### Production DB
````
CREATE USER 'guest'@'localhost' IDENTIFIED BY 'guest';
GRANT ALL ON enmprotocol.* TO 'guest'@'localhost';
GRANT execute on procedure `enmprotocol`.createProtocolVersion to guest@localhost;
````

Test DB
````
GRANT ALL ON `enmp_test`.* TO 'guest'@'localhost';
GRANT execute on procedure `enmp_test`.createProtocolVersion to  guest@localhost;
````
