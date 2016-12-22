#!/bin/bash
java=$1
echo "executing the server using $java"

$java --add-exports java.base/sun.nio.ch=ALL-UNNAMED --add-exports java.base/sun.net.dns=ALL-UNNAMED  -jar dest/Thaw-1.0.jar
#--add-exports=java.base/sun.security.tools.keytool=ALL-UNNAMED --add-exports=java.base/sun.security.x509=ALL-UNNAMED