import keras
import os
from keras import backend as K
from keras.layers.core import Dense, Activation, Flatten, Dropout
from keras.optimizers import Adam
from keras.metrics import categorical_crossentropy
from keras.preprocessing.image import ImageDataGenerator
from keras.preprocessing import image
from keras.models import Sequential, Model
from keras.applications import imagenet_utils
from keras.layers import Dense,GlobalAveragePooling2D
from keras.applications import MobileNetV2
from keras.applications.mobilenet_v2 import preprocess_input
from keras.callbacks import ModelCheckpoint
import numpy as np
from PIL import Image
from keras.optimizers import Adam

image_dir = "images" # image directory is not available unless you download the files yourself
num_epochs = 10
num_images=200
batch = 8
height = 224
width = 224
base_model = MobileNetV2(weights='imagenet', include_top=False, input_shape=(height, width, 3))
data_trainer = ImageDataGenerator(preprocessing_function=preprocess_input, rotation_range=90, horizontal_flip=True, vertical_flip=True)
data_generator = data_trainer.flow_from_directory(image_dir, target_size=(height, width), batch_size=batch)


def build_finetune_model(base_model, dropout, fc_layers, num_classes):
    for layer in base_model.layers:
        layer.trainable = False

    x = base_model.output
    x = Flatten()(x)
    for fc in fc_layers:
        # New FC layer, random init
        x = Dense(fc, activation='relu')(x) 
        x = Dropout(dropout)(x)

    # New softmax layer
    predictions = Dense(num_classes, activation='softmax')(x) 
    
    finetune_model = Model(inputs=base_model.input, outputs=predictions)

    return finetune_model

class_list = [dI for dI in os.listdir('images') if os.path.isdir(os.path.join('images',dI))]
fclayers = [1024, 1024]
print("CLASS LIST", class_list)
dropout = 0.5
finetune_model = build_finetune_model(base_model, dropout=dropout, fc_layers=fclayers, num_classes=len(class_list))
adam = Adam(lr=0.00001)
finetune_model.compile(adam, loss='categorical_crossentropy', metrics=['accuracy'])

filepath= "MobilenetCalorie_model_weights.h5"

checkpoint = ModelCheckpoint(filepath, monitor=["acc"], verbose=1, mode='max')
callbacks_list = [checkpoint]

history = finetune_model.fit_generator(data_generator, epochs=num_epochs, workers=8, 
                                       steps_per_epoch=num_images // batch, 
                                       shuffle=True, callbacks=callbacks_list)



# mobile = keras.applications.mobilenet_v2.MobileNetV2()
# def prepare_image(file):
#     img_path = ''
#     img = image.load_img(img_path + file, target_size=(224,224))
#     img_array = image.img_to_array(img)
#     img_array_expanded_dims = np.expand_dims(img_array, axis=0)
#     return keras.applications.mobilenet_v2.preprocess_input(img_array_expanded_dims)
# # will display image
# Image.open('burrito.jpg').show()
# preprocessed_image = prepare_image('burrito.jpg')
# predictions = mobile.predict(preprocessed_image)
# results = imagenet_utils.decode_predictions(predictions)
# print(results)