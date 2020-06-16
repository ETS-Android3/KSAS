# KSAS - Kenpo Set Assisting System

An application for assisting the learning of American Kenpo Karate's Blocking Set I using an Android smartphone.

## Model

The application already comes with a tensorflow lite model trained. This model was trained using a dataset composed of 240 movements captured from 20 subjects. The model used consists in a LSTM and the code used for training it can be found in this [link](https://github.com/AlbertoCasasOrtiz/Martial-Arts-Movements-Classifier). The LSTM achieved 100% of accuracy in the training set and 94% of accuracy in the testing set.

## KSAS Instructions

The application starts and asks you to put your device as a wearable. This can be done through the use of a wrist band similar to those used by athletes and runners.

Then, the applications ask you to select a set. The only available set is Blocking Set I.

Once the set is selected, the applications tell you some information about the set, and a video with the full execution of the set is shown.

In the next Activity, the user is asked to get into the starting position, and the motion capture starts:

* If the user executes a movement correctly, the app will congratulate the user and ask him to execute the next movement. A victory sound is played.

* If the user executed the wrong movement, the device will vibrate, the app will tell the user that the movement has been wrongly executed and will ask the user to execute the movement again. A buzz sound is played.

* If no movement has been detected, the app will ask the user to execute the movement again.

## License
This software is licensed under the [AGPL](https://choosealicense.com/licenses/agpl-3.0/) license.
