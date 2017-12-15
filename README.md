# hotelApp

![hotelApp](https://i.imgur.com/BhSRHEz.jpg)

## Getting Started

:point_right: [hotelApp](http://hotelapp.us-east-2.elasticbeanstalk.com/index)

### Introduction and How to use

Try to get a quick and simple registration on my application by using the "hotelApp" link above. Login and look for hotel information, reviews and attractions in San Fraancisco Bay Area. Save a hotel by clicking the empty heart in the upper right-hand corner of the hotel detail. Like a review if it is interesting to you. Add a review for a hotel you had visited. Manage your own histories in MyPage. Finally, enjoy using my application.

**Notice that my application doesn't store your password directly, so you don't need to worry about me getting your personal information. Instead, I hash your password with randomly produced salt everytime before adding it into database. The user data in my database should looks like this:**

![hashed password and salt](https://i.imgur.com/DcqZwXG.png)

## Development and Deployment

### Frontend

* [Bootstrap_v4](https://getbootstrap.com/) - The web framework
* [SweetAlert](https://lipis.github.io/bootstrap-sweetalert/) - The alert tool
* [Google APIs](https://developers.google.com/maps/) - Displaying map and attractions
* [JavaScript](https://www.javascript.com/) - Supporting frontend events
* [AJAX](https://www.w3schools.com/xml/ajax_intro.asp) - Communication between frontend and backend

### Backend

* [Java](https://www.oracle.com/java/index.html) - Core programming language
* [Jetty Servlet](http://www.eclipse.org/jetty/) - Server handler
* [JDBC](http://www.oracle.com/technetwork/java/javase/jdbc/index.html) - Java database library
* [Velocity](http://velocity.apache.org/) - Template Engine
* [MySQL](https://www.mysql.com/) - Database

### Deployment

* [AWS](https://aws.amazon.com/) - Deployed on Amazon Web Service using Elastic Beanstalk with Java 8 environment and RDS MySQL database

### Other things

* All frontend static files are in */frontend*, Java source files are in */src*.
* Hotel information and review data is provided in */input*.
* More details please see Java document in *Java_Doc.zip*.
* If you want to build this environment on your local divice, make sure to change parameters in */doc/database.properties* to connect with your local database.

## Documentation

### References
* [University of San Francisco](https://www.usfca.edu/)
* [AWS](https://aws.amazon.com/)
* [Google APIs](https://developers.google.com/maps/)
* [Start Bootstrap](https://startbootstrap.com/)
* [Expedia](https://www.expedia.com/Activities)
* [Imgur](https://imgur.com/)
* [ORBO](https://imgur.com/gallery/zthrchM)

### License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### Acknowledgment

This is a course project, not using for any commercial purpose.

### Author

* **Brian Sung** - *Computer Science graduate student* - [LinkedIn](https://www.linkedin.com/in/brianisadog/)