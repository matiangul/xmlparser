# xmlparser
Java / Kotlin app able to process huge xml files. Currently it takes gzipped xml file of some logs from wikimedia. Then it logs some of the contributors mentioned there.

# Build
Run gradle `jar` build task

# Run
Donwload wikimedia dump of log events to all pages and users: https://dumps.wikimedia.org/commonswiki/20180920/commonswiki-20180920-pages-logging1.xml.gz wikimedia 
and then point it in the command run:

`java -jar build/libs/xmlparser-1.0.0.jar file-path=commonswiki-20180920-pages-logging1.xml.gz`
