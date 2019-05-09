# CalorieImageClassification
Artificial Intelligence CSUF-CPSC-481 Project
- Hector M
- Alex L
- Monique C
- Aarib A

# How to run
## Android model
The android application was tested using [Android Studion v3.2.1](https://developer.android.com/studio/index.html).
Add the `camera` folder as an existing project and install any packages that you are prompted with.
To run with debug mode click on the cog with green arrow on the top right.

Results can be seen printed to screen of the emulator or physical device, can also be seen on debug mode in the `logcat` window.

Only the emulator works consistently with the model, using it on a physical devices could potentially crash the application.

## Python model
Currently the python model `transfer_learning/h5toTflite.py` had used the MobileNetV2 model, but complications appeared and some multple models were creating for testing. Images for our transfer learning model came from [kaggle/food41](https://www.kaggle.com/kmader/food41/version/5). Due to the size of the images only the link is provided, you will have to download the images if you'd like to test it.

Assuming you have a python3 virtual environment [virtualenv](https://pypi.org/project/virtualenv/) installed.
```
. venv/bin/activate
pip3 install requirements.txt
python3 transfer_learning/h5toTflite.py
```
This should take care of any installation issues and run the file properly.
In case it's needed the main installation lines needed are

```
pip3 install keras
pip3 install tensorflow
```
