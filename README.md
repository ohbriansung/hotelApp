# hotelApp

![hotelApp](https://i.imgur.com/BhSRHEz.jpg)

## Use this App

[hotelApp](http://206.189.215.73:5000/index)

## Introduction and How to Use

Try to get a quick and simple registration on my application by using the "hotelApp" link above. Login and look for hotel information, reviews and attractions in San Francisco Bay Area. Save a hotel by clicking the empty heart in the upper right-hand corner of the hotel detail. Like a review if it is interesting to you. Add a review for a hotel you had visited. Manage your own histories in MyPage. Finally, enjoy using my application.

**Notice that the application doesn't store your password directly, so you don't need to worry about anyone getting your personal information. Instead, I hash your password with randomly produced salt every time before adding it into database. The user data in my database should looks like this:**

![hashed password and salt](https://i.imgur.com/DcqZwXG.png)

## Frontend

0. [Bootstrap_v4](https://getbootstrap.com/) - The web framework
1. [SweetAlert](https://lipis.github.io/bootstrap-sweetalert/) - The alert tool
1. [Google APIs](https://developers.google.com/maps/) - Displaying map and attractions
1. [JavaScript](https://www.javascript.com/) - Supporting frontend events
1. [AJAX](https://www.w3schools.com/xml/ajax_intro.asp) - Communication between frontend and backend

## Backend

0. [Java](https://www.oracle.com/java/index.html) - Core programming language
1. [Jetty Servlet](http://www.eclipse.org/jetty/) - Server handler
1. [JDBC](http://www.oracle.com/technetwork/java/javase/jdbc/index.html) - Java database library
1. [Velocity](http://velocity.apache.org/) - Template Engine
1. [MySQL](https://www.mysql.com/) - Database

## Deployment

0. [Digital Ocean](https://www.digitalocean.com) - Deployed on Digital Ocean using Ubuntu 18.04
1. [AWS](https://aws.amazon.com/) - Used to be deployed on Amazon Web Service using Elastic Beanstalk with Java 8 environment and RDS MySQL database

## Other Notes

* All frontend static files are in [frontend](https://github.com/ohbriansung/hotelApp/tree/master/frontend) folder, Java source code is in [src](https://github.com/ohbriansung/hotelApp/tree/master/src).
* Hotel information and review data is provided in [input](https://github.com/ohbriansung/hotelApp/tree/master/input).
* More details please see Java document in [Java_Doc.zip](https://github.com/ohbriansung/hotelApp/blob/master/Java_Doc.zip?raw=true).
* If you want to build this environment on your local device, make sure to change parameters in [/doc/database.properties](https://github.com/ohbriansung/hotelApp/blob/master/doc/database.properties) to connect with your local database.

## Author

Chien-Yu (Brian) Sung

## Acknowledgment

This project is for academic purposes only.

## Other References
0. [University of San Francisco](https://www.usfca.edu/)
1. [AWS](https://aws.amazon.com/)
1. [Google APIs](https://developers.google.com/maps/)
1. [Start Bootstrap](https://startbootstrap.com/)
1. [Expedia](https://www.expedia.com/Activities)
1. [Imgur](https://imgur.com/)
1. [ORBO](https://imgur.com/gallery/zthrchM)
