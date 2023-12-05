[CompS350F Group 37]

Required:

JDK >=21
JavaFX SDK >=21

How to run our application?
*Method 1:*

Just download from this link, unzip then click app.exe:

https://drive.google.com/file/d/1cVMPOvROvCHO-Aydoo52gYs_5MV4Z73g/view?usp=sharing

The app.exe is configured to use a bundled JDK and JavaFX SDK. Therefore, no need for further installation.

(The file size of JDK/JavaFX SDK is too large to be included in github and therefore this link is provided for your convenience)

*Method 2:*

Step 1:Download files from github and unzip.


Step 2: Download JDK and JavaFX SDK on your own with both version >= 21. 

Download Link for JavaFX SDK:
https://gluonhq.com/products/javafx/

Download Link for JDK:
https://www.oracle.com/java/technologies/downloads/#jdk21-windows

Step 3:

Type this in to your terminal/cmd:

For Windows:

java --module-path "\path\to\javafx-sdk-21.0.1\lib" --add-modules javafx.controls,javafx.fxml -jar \path\to\350proj\app.jar 



For Mac/Linux:

java --module-path /path/to/javafx-sdk-21.0.1/lib --add-modules javafx.controls,javafx.fxml -jar /path/to/350proj/app.jar 


***Replace the paths with you own paths.***



_________________________________________________________________________
How to login?

Login using username and password stored in database.

Here is one example from each role:

1.Student:
username:s1289505      password:12895052
2.Teacher:
username:t7654321      password:AS8765432as
3.Admin
username:a1234567      password:12345678

