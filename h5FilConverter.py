#The tf.keras file must contain both the model and the weights
import tensorflow as tf
from tensorflow.python.keras.models import Sequential, save_model, load_model
from tensorflow.python.keras.layers import Input, Conv2D
import os
os.environ["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID"   # see issue #152
os.environ["CUDA_VISIBLE_DEVICES"] = ""

# model_path = 'food41/food_test_c101_n1000_r64x64x3.h5'
model_path = "/Users/hmedina/TerminalProjects/testingCodes/ArtificialIntellLUL/CalorieImageClassification/food41/food_c101_n1000_r384x384x3.h5"
model = Sequential([
  Conv2D(1, 5),
])
save_model(model, model_path)
model = load_model(model_path)
config = model.get_config()
weights = model.get_weights()
print("CONFIG  ", config, "  WEIGHTS  ", weights)
converter = tf.lite.TFLiteConverter.from_keras_model_file(model_path)
tflite_model = converter.convert()
open("food64x64_converted.tflite", "wb").write(tflite_model)
