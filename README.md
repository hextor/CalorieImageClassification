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

Results are currently printed on debug mode on the `logcat` window.

Currently only the emulator works with the model, using it on a physical devices crashes the application.

## Python model
Currently the python model `mobilenet.py` is standard MobileNetV2 model. Will need to commit transfer learning with our image files which can be found on [kaggle/food41](https://www.kaggle.com/kmader/food41/version/5).

Assuming you have a python3 virtual environment [virtualenv](https://pypi.org/project/virtualenv/) installed.
```
. venv/bin/activate
pip3 install requirements.txt
python3 mobilenet.py
```
It will return a prediction of an image provided.
##### May require change of image source in the code.

