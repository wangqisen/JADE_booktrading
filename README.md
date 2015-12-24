# JADE_booktrading

JADE_booktrading is a demo of book trading shop based on JADE's tutorial.

To run this example,you need to set the CLASSPATH enviroment setting first to add all jars you need for JADE,then

use javac compiler to compile the java documents.

Finnaly type the following command to run the codes.

```
java jade.Boot -gui s1:BookSellerAgent m:BookManagerAgent "b1:BookBuyerAgent(book_title)"
```
buyer needs an argument of the title for the target book.
