#The tf.keras file must contain both the model and the weights
## WORK IN PROGRESS TO ALLOW FILE TO BE USED IN TENSORFLOW LITE
import tensorflow as tf
from tensorflow.python.keras.models import Sequential, save_model, load_model
from tensorflow.python.keras.layers import Input, Conv2D
from tensorflow.python.keras.applications.mobilenet_v2 import MobileNetV2


# model_path = 'food41/food_test_c101_n1000_r64x64x3.h5'
model_path = "food_c101_n1000_r384x384x3.h5"
# model = Sequential([
#   Conv2D(1, 5),
# ])
# save_model(model, model_path)
# model = load_model(model_path, compile=True)
# new model
model = Sequential()
model.add(Dense(2, input_dim=3, name='dense_1'))  # will be loaded
model.add(Dense(10, name='new_dense'))  # will not be loaded

# load weights from first model; will only affect the first layer, dense_1.
model.load_weights(model_path, by_name=True)
# config = model.get_config()
# weights = model.get_weights()
# print("CONFIG  ", config, "  WEIGHTS  ", weights)
# compile(model, ".h5")
# model.compile(optimizer='adam',
#               loss='sparse_categorical_crossentropy',
#               metrics=['accuracy'])
# model.fit(epochs=5, steps_per_epoch=5)
converter = tf.lite.TFLiteConverter.from_keras_model_file(model)
tflite_model = converter.convert()
open("food64x64_converted.tflite", "wb").write(tflite_model)
