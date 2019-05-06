# This file is to be used in Google Colab
# Since I have a windows machine, I couldn't actually run the tflite conversion
#!pip install -U -q PyDrive

from pydrive.auth import GoogleAuth
from pydrive.drive import GoogleDrive
from google.colab import auth, files
from oauth2client.client import GoogleCredentials
from tensorflow.contrib import lite

# 1. Authenticate and create the PyDrive client.
auth.authenticate_user()
gauth = GoogleAuth()
gauth.credentials = GoogleCredentials.get_application_default()
drive = GoogleDrive(gauth)

# PyDrive reference:
# https://gsuitedevs.github.io/PyDrive/docs/build/html/index.html


# 3. Load a file by ID and print its contents.
downloaded = drive.CreateFile({'id': '1FCYC9JZTwCEBiCeLkfWoNPqDRZlIRinh'})

downloaded.GetContentFile('MobilenetCalorie_model_weights.h5')
converter = lite.TFLiteConverter.from_keras_model_file('MobilenetCalorie_model_weights.h5')
model = converter.convert()
print("converter taking too long")
title = 'calorie_model.tflite'
file = open( title , 'wb' )
file.write( model )
files.download(title)