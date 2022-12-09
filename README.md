# IBM MQ Channel to SNI
This repository contains a selection of programs written in different languages that can be compiled and ran to convert an IBM MQ channel name to it's SNI format. This is particularly useful as during TLS communication the TLS Client hello will contain a SNI header that can be used by routers to ensure the encrypted traffic reaches the correct destination without the need to resolve the TLS handshake.

While the standard is the SNI header is set to the hostname the client is attempting to reach, by default IBM MQ uses the SNI header in order to provide its multiple certificates functionality. As such the SNI header is set to the MQ Channel name converted to a hostname. The rules IBM MQ uses to convert an IBM MQ channel name to it's SNI format are [documented here](https://www.ibm.com/docs/en/ibm-mq/latest?topic=requirements-how-mq-provides-multiple-certificates-capability).

If you want to use the SNI header to route IBM MQ traffic then you need to ensure you route off the correct SNI, the tools contained here can help convert an IBM MQ channel name to the SNI format for entry into your router or OpenShift Routes configuration.

Alternatively, from IBM MQ version 9.2.4 CD and 9.3.0 LTS the [OutboundSNI property](https://www.ibm.com/docs/en/ibm-mq/9.2?topic=programs-outboundsni-property) can be used to tell IBM MQ to use a hostname instead of a channel name
for the SNI header.

## Tool usage
While the tools are all written in different languages, they all function the same way. The tools take a single parameter as input and this is the channel name you want to convert. Alternatively, if no parameter is given, the tool will prompt you for the channel name.

If an invalid IBM MQ channel name is given then the tool will not convert it. Additionally, if a valid IBM MQ channel name is given but the channel will create an invalid URL then a warning message will be issued but the SNI format will be outputted. This is a known limitation with IBM MQ's use of SNI and so users of IBM MQ who wish route based on the SNI header must ensure that their channel names end with a capital letter.

## Compiling the tools

### Java
Java does not require any special libraries and be compiled with:
```
$ javac mqChannelToSNI.java
```

## Examples of use

### Java
Running the java tool supplying the Channel as a parameter:
```
$ java -cp . mqChannelToSNI My.Test.CHL
Converting IBM MQ Channel name: My.Test.CHL
SNI format is: m79-2e-t65-73-74-2e-chl.chl.mq.ibm.com
```

Running the java tool without any parameters:
```
$ java -cp . mqChannelToSNI
Enter IBM MQ channel name: Another.channel1
Converting IBM MQ Channel name: Another.channel1
SNI format is: a6e-6f-74-68-65-72-2e-63-68-61-6e-6e-65-6c-1.chl.mq.ibm.com
```

## Health Warning
These programs are provided as-is with no guarantees of support or updates. There are
also no guarantees of compatibility with any future versions of IBM MQ .

## Issues
For feedback and issues relating specifically to this package, please use
the [GitHub issue tracker](https://github.com/parrobe/mq-chl-to-sni/issues).

## Copyright

Copyright Rob Parker 2022
