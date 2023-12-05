How to run our application?

Method 1:

Just download from this link, unzip then click app.exe:

https://drive.google.com/file/d/1cVMPOvROvCHO-Aydoo52gYs_5MV4Z73g/view?usp=sharing


Method 2:

Step 1:Download files from github


Step 2: Download JRE(Java Runtime Environment) and JavaFX sdk on your own with both version >= 21. 


Step 3:

type this in to your terminal/cmd:

For Windows:

java --module-path "\path\to\javafx-sdk-21.0.1\lib" --add-modules javafx.controls,javafx.fxml -jar \path\to\350proj\app.jar 


For Mac/Linux:

java --module-path "/path/to/javafx-sdk-21.0.1/lib" --add-modules javafx.controls,javafx.fxml -jar /path/to/350proj/app.jar 


***Replace the paths with you own paths.***